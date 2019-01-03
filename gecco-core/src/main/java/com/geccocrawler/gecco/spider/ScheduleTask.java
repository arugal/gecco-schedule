package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.spider.internal.DefaultPriorityQueue;
import com.geccocrawler.gecco.spider.internal.PrioprityQueueNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Queue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: zhangwei
 * @date: 13:07/2019-01-02
 */
public abstract class ScheduleTask implements Runnable, PrioprityQueueNode, Delayed {

    private static final Log log = LogFactory.getLog(ScheduleTask.class);

    private static final AtomicLong nextTaskId = new AtomicLong();
    private static final long START_TIME = System.nanoTime();

    static long nanoTime() {
        return System.nanoTime() - START_TIME;
    }

    static long deadlineNanos(long delay){
        long deadlineNaos = nanoTime() + delay;
        return deadlineNaos < 0 ? Long.MAX_VALUE : deadlineNaos;
    }

    private final long id = nextTaskId.getAndIncrement();
    private long deadlineNanos;
    private final long periodNanos;
    private int queueIndex = INDEX_NOT_IN_QUEUE;
    final SpiderExecutor executor;

    public ScheduleTask(SpiderExecutor executor){
        this.executor = executor;
        this.deadlineNanos = 0;
        this.periodNanos = 0;
    }

    public ScheduleTask(SpiderExecutor executor, long nanoTime, long period){
        this.executor = executor;
        if(period == 0){
            throw new IllegalArgumentException("period: 0 (expected: != 0)");
        }
        deadlineNanos = nanoTime;
        periodNanos = period;
    }

    public ScheduleTask(SpiderExecutor executor, long nanoTime){
        this.executor = executor;
        this.deadlineNanos = nanoTime;
        this.periodNanos = 0;
    }

    public long deadlineNanos() {
        return deadlineNanos;
    }

    public long delayNanos() {
        return Math.max(0, deadlineNanos() - nanoTime());
    }

    public long delayNanos(long currentTimeNanos) {
        return Math.max(0, deadlineNanos() - (currentTimeNanos - START_TIME));
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(delayNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int priorityQueueIndex(DefaultPriorityQueue<?> queue) {
        return queueIndex;
    }

    @Override
    public void priorityQueueIndex(DefaultPriorityQueue<?> queue, int i) {
        queueIndex = i;
    }

    @Override
    public void run() {
        // 执行抓取任务
        try{
            // 抓取
            spider();
            if(periodNanos > 0){
                // 周期性任务,设置周期后重新入队
                if(!executor.isShuttingDown()){
                    long p = periodNanos;
                    if(p > 0){
                        deadlineNanos += p;
                    }else{
                        deadlineNanos = nanoTime() - p;
                    }
                    Queue<ScheduleTask> scheduleTaskQueue = ((ScheduleSpiderExecutor) executor).scheduleTaskQueue;
                    scheduleTaskQueue.add(this);
                }
            }
        }catch (Exception e){
            // ignore
        }
    }

    protected abstract void spider();

    @Override
    public int compareTo(Delayed o) {
        if (this == o) {
            return 0;
        }

        ScheduleTask that = (ScheduleTask) o;
        long d = deadlineNanos() - that.deadlineNanos();
        if (d < 0) {
            return -1;
        } else if (d > 0) {
            return 1;
        } else if (id < that.id) {
            return -1;
        } else if (id == that.id) {
            throw new Error();
        } else {
            return 1;
        }
    }
}
