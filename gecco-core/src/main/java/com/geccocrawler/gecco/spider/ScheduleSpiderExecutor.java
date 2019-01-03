package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.internal.DefaultPriorityQueue;
import com.geccocrawler.gecco.spider.internal.PriotiryQueue;
import com.geccocrawler.gecco.util.ObjectUtil;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * @author: zhangwei
 * @date: 23:18/2019-01-01
 */
public abstract class ScheduleSpiderExecutor extends AbstractSpiderExecutor {


    private static final Comparator<ScheduleTask> SCHEDULE_SPIDER_TASK_COMPARATOR = new Comparator<ScheduleTask>() {
        @Override
        public int compare(ScheduleTask o1, ScheduleTask o2) {
            return o1.compareTo(o2);
        }
    };

    public ScheduleSpiderExecutor() {

    }

    public ScheduleSpiderExecutor(SpiderExecutorGroup group) {
        super(group);
    }

    PriotiryQueue<ScheduleTask> scheduleTaskQueue;

    static long nanoTime(){
        return ScheduleTask.nanoTime();
    }

    PriotiryQueue<ScheduleTask> scheduleTaskQueue(){
        if(scheduleTaskQueue == null){
            scheduleTaskQueue = new DefaultPriorityQueue<ScheduleTask>(SCHEDULE_SPIDER_TASK_COMPARATOR, 16);
        }
        return scheduleTaskQueue;
    }

    private static boolean isNullOrEmpty(Queue<ScheduleTask> queue){
        return queue == null || queue.isEmpty();
    }

    protected final Runnable pollScheduledTask(){
        return pollScheduledTask(nanoTime());
    }

    protected final Runnable pollScheduledTask(long nanoTime){
        Queue<ScheduleTask> scheduleTaskQueue = this.scheduleTaskQueue;
        ScheduleTask scheduleTask = scheduleTaskQueue == null ? null : scheduleTaskQueue.peek();
        if(scheduleTask == null){
            return null;
        }
        if(scheduleTask.deadlineNanos() <= nanoTime){
            scheduleTaskQueue.remove();
            return scheduleTask;
        }
        return null;
    }

    protected final long nextScheduledTaskNano(){
        Queue<ScheduleTask> scheduleTaskQueue = this.scheduleTaskQueue;
        ScheduleTask scheduleTask = scheduleTaskQueue == null ? null : scheduleTaskQueue.peek();
        if(scheduleTask == null){
            return  - 1;
        }
        return Math.max(0, scheduleTask.deadlineNanos() - nanoTime());
    }

    final ScheduleTask peekScheduledTask(){
        Queue<ScheduleTask> scheduleTaskQueue = this.scheduleTaskQueue;
        if(scheduleTaskQueue == null){
            return null;
        }
        return scheduleTaskQueue.peek();
    }

    protected final boolean hasScheduledTasks(){
        Queue<ScheduleTask> scheduleTaskQueue = this.scheduleTaskQueue;
        ScheduleTask scheduleTask = scheduleTaskQueue == null ? null : scheduleTaskQueue.peek();
        return scheduleTask != null && scheduleTask.deadlineNanos() <= nanoTime();
    }


    @Override
    public void schedule(String url, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(url, "url");
        schedule(new HttpGetRequest(url), delay, unit);
    }

    @Override
    public void schedule(HttpRequest request, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(request, "request");
        ObjectUtil.checkNotNull(unit, "unit");
        if(delay < 0){
            delay = 0;
        }
        schedule(newScheduleTask(request, unit.toNanos(delay)));
    }

    @Override
    public void scheduleAtFixedRate(String url, long initialDelay, long period, TimeUnit unit) {
        ObjectUtil.checkNotNull(url, "url");
        scheduleAtFixedRate(new HttpGetRequest(url), initialDelay, period, unit);
    }

    @Override
    public void scheduleAtFixedRate(HttpRequest request, long initialDelay, long period, TimeUnit unit) {
        ObjectUtil.checkNotNull(request, "request");
        ObjectUtil.checkNotNull(unit, "unit");
        if(initialDelay < 0){
            throw new IllegalArgumentException(
                    String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if(period <= 0){
            throw new IllegalArgumentException(
                    String.format("period: %d (expected: > 0)", period));
        }
        schedule(newScheduleTask(request, ScheduleTask.deadlineNanos(unit.toNanos(initialDelay)), unit.toNanos(period)));
    }

    @Override
    public void scheduleWithFixedDelay(String url, long initalDelay, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(url, "url");
        scheduleWithFixedDelay(new HttpGetRequest(url), initalDelay, delay, unit);
    }

    @Override
    public void scheduleWithFixedDelay(HttpRequest request, long initialDelay, long delay, TimeUnit unit) {
        ObjectUtil.checkNotNull(request, "request");
        ObjectUtil.checkNotNull(unit, "unit");
        if (initialDelay < 0) {
            throw new IllegalArgumentException(
                    String.format("initialDelay: %d (expected: >= 0)", initialDelay));
        }
        if (delay <= 0) {
            throw new IllegalArgumentException(
                    String.format("delay: %d (expected: > 0)", delay));
        }
        schedule(newScheduleTask(request, ScheduleTask.deadlineNanos(unit.toNanos(initialDelay)), -unit.toNanos(delay)));
    }

    final void schedule(final ScheduleTask task){
        scheduleTaskQueue().add(task);
        startThread();
    }

    protected abstract ScheduleTask newScheduleTask(HttpRequest request, long delay);

    protected abstract ScheduleTask newScheduleTask(HttpRequest request, long initialDelay, long period);

    final void removeScheduled(final ScheduleTask task){
        scheduleTaskQueue().removeTyped(task);
    }

}
