package com.geccocrawler.gecco.spider;

import com.geccocrawler.gecco.request.HttpGetRequest;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.linstener.SpiderExecutorListener;
import com.geccocrawler.gecco.spider.linstener.SpiderExecutorListenerSupport;
import com.geccocrawler.gecco.util.ObjectUtil;
import com.geccocrawler.gecco.util.SystemPropertyUtil;
import lombok.NonNull;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @author: zhangwei
 * @date: 14:35/2019-01-02
 */
public abstract class SingleSpiderExecutor extends ScheduleSpiderExecutor {

    private static final Log log = LogFactory.getLog(SingleSpiderExecutor.class);

    static final int DEFAULT_MAX_PENDING_EXECUTOR_TASKS = Math.max(16, SystemPropertyUtil.getInt("io.spider.executor.maxPendingTasks", Integer.MAX_VALUE));

    private static final int ST_NOT_STARTED = 1; // 未开始
    private static final int ST_STARTED = 2; // 已开始
    private static final int ST_PAUSEED = 3; // 暂停
    private static final int ST_SHUTTING_DOWN = 4; // 正在关闭中
    private static final int ST_SHUTDOWN = 5; // 已关闭

    private Queue<Runnable> taskQueue;

    private volatile Thread thread;

    private Executor executor;

//    private final Semaphore threadLock = new Semaphore(0);

    private int maxPendingTasks;

    private RejectedExecutionHandler rejectedExecutionHandler;

    private long lastExecutionTime;

    private volatile int state = ST_NOT_STARTED;

    private final SpiderExecutorListenerSupport listanerSupport = new SpiderExecutorListenerSupport();

    private static final AtomicIntegerFieldUpdater<SingleSpiderExecutor> STATE_UPDATER =
            AtomicIntegerFieldUpdater.newUpdater(SingleSpiderExecutor.class, "state");


    private static final long SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(1);

    private static final long MAX_SCHEDULE_PURGE_INTERVAL = TimeUnit.SECONDS.toNanos(2);

    protected SingleSpiderExecutor(SpiderExecutorGroup parent){
        this(parent, new NamedThreadFactory("thread-pool"));
    }

    protected SingleSpiderExecutor(SpiderExecutorGroup parent, ThreadFactory threadFactory){
        this(parent, new ThreadPerTaskExecutor(threadFactory));
    }

    protected SingleSpiderExecutor(SpiderExecutorGroup parent, Executor executor){
        this(parent, executor, RejectedExecutionHandlers.reject());
    }

    protected SingleSpiderExecutor(SpiderExecutorGroup parent, Executor executor, RejectedExecutionHandler rejectedExecutionHandler){
        this(parent, executor, DEFAULT_MAX_PENDING_EXECUTOR_TASKS, rejectedExecutionHandler);
    }


    protected SingleSpiderExecutor(@NonNull SpiderExecutorGroup parent, @NonNull Executor executor, int maxPendingTasks,
                                   @NonNull RejectedExecutionHandler rejectedExecutionHandler) {
        super(parent);
        this.executor = executor;
        this.maxPendingTasks = Math.max(16, maxPendingTasks);
        this.taskQueue = newTaskQueue(this.maxPendingTasks);
        this.rejectedExecutionHandler = ObjectUtil.checkNotNull(rejectedExecutionHandler, "rejectedExecutionHandler");
    }

    protected void updateLastExecutionTime(){
        lastExecutionTime = nanoTime();
    }

    @Override
    public void executor(@NonNull String url) {
        executor(new HttpGetRequest(url));
    }

    @Override
    public void executor(@NonNull HttpRequest request) {
        addTask(newScheduleTask(request));
        startThread();
    }

    protected abstract ScheduleTask newScheduleTask(HttpRequest request);

    @Override
    public void startThread() {
        if(state == ST_NOT_STARTED){
            if(STATE_UPDATER.compareAndSet(this, ST_NOT_STARTED, ST_STARTED)){
                try{
                    doStartThread();
                }catch (Throwable e){
                    STATE_UPDATER.set(this, ST_NOT_STARTED);
                }
            }
        }
    }

    private void doStartThread(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                thread = Thread.currentThread();
                listanerSupport.onStart(SingleSpiderExecutor.this);
                updateLastExecutionTime();
                try {
                    SingleSpiderExecutor.this.run();
                }catch (Throwable t){
                    log.warn("Unexpected exception from an event executor: ", t);
                }finally {
                    try{
                        cleanup();
                    }finally {
                        STATE_UPDATER.set(SingleSpiderExecutor.this, ST_SHUTDOWN);
                    }
                }
            }
        });
    }

    protected abstract void run();

    protected void cleanup(){
        if(taskQueue != null){
            taskQueue.clear();
        }
        if(scheduleTaskQueue != null){
            scheduleTaskQueue.clear();
        }
    }


    @Override
    public final void pause() {
        if(state == ST_STARTED){
            if (STATE_UPDATER.compareAndSet(this, ST_STARTED, ST_PAUSEED)) {
                listanerSupport.onPause(this);
            }
        }
    }

    /**
     * 探测是否暂停
     * 如果 state 处于暂停状态,则阻塞线程
     */
    protected final void pauseExplore(){
        if(isPause()){
            try {
                synchronized (this) {
                    if(isPause()) {
                        this.wait();
                    }
                }
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    @Override
    public final void renew() {
        if(state == ST_PAUSEED){
            if(STATE_UPDATER.compareAndSet(this, ST_PAUSEED, ST_STARTED)){
                synchronized (this) {
                    this.notifyAll();
                }
                listanerSupport.onRenew(this);
            }
        }
    }


    @Override
    public final void shutdown() {
        int oldState = state;
        if(oldState < ST_SHUTTING_DOWN){
            if(STATE_UPDATER.compareAndSet(this, oldState, ST_SHUTTING_DOWN)){
                try {
                    if(thread != null && !thread.isInterrupted()){
                        thread.interrupt();// 将线程从阻塞状态中断
                    }
                }catch (Exception e){
                    // ignore
                }
                listanerSupport.onShutdown(this);
            }
        }
    }

    @Override
    public boolean isRuning() {
        return state == ST_STARTED;
    }

    @Override
    public boolean isPause() {
        return state == ST_PAUSEED;
    }

    @Override
    public boolean isShuttingDown() {
        return state >= ST_SHUTTING_DOWN;
    }

    @Override
    public boolean isShutdown() {
        return state == ST_SHUTDOWN;
    }

    @Override
    public boolean addEngicListener(SpiderExecutorListener listener) {
        return listanerSupport.addListener(listener);
    }

    @Override
    public boolean removeEngicListener(SpiderExecutorListener listener) {
        return listanerSupport.removeListaner(listener);
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<Runnable>(maxPendingTasks);
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> queue){
        for(;;){
            Runnable task = queue.poll();
            if(task == null){
                continue;
            }
            return task;
        }
    }

    protected boolean runAllTasks(){
        boolean ranAtLeastOne = false;
        BlockingQueue<Runnable> blockTaskQueue = (BlockingQueue<Runnable>) taskQueue;
        Runnable task = null;
        do {
            if(blockTaskQueue.isEmpty()){
                fetchFormScheduledTaskQueue();
                try {
                    task = blockTaskQueue.poll(Math.min(delayNanos(nanoTime()), MAX_SCHEDULE_PURGE_INTERVAL), TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    // ignore
                }
            }else {
                task = blockTaskQueue.poll();
            }
            if(task != null && isRuning()){
                pauseExplore(); // 响应暂停
                safeExecute(task);
                ranAtLeastOne = true;
            }
        }while (task != null && isRuning());

        if(ranAtLeastOne){
            updateLastExecutionTime();
        }

        return ranAtLeastOne;
    }

    protected static void safeExecute(Runnable task){
        try {
            task.run();
        }catch (Throwable t){
            log.warn(String.format("A task raised an exception. Task: %s", task.toString()), t);
        }
    }


    private boolean fetchFormScheduledTaskQueue(){
        long nanoTime = nanoTime();
        Runnable scheduledTask = pollScheduledTask(nanoTime);
        while (scheduledTask != null){
            if(!taskQueue.offer(scheduledTask)){
                scheduleTaskQueue().add((ScheduleTask) scheduledTask);
                return false;
            }
            scheduledTask = pollScheduledTask(nanoTime);
        }
        return true;
    }

    protected Runnable taskTask(){
        if(!(taskQueue instanceof BlockingQueue)){
            throw new UnsupportedOperationException();
        }
        BlockingQueue<Runnable> taskBlockQueue = (BlockingQueue<Runnable>) taskQueue;
        for(;;){
            ScheduleTask scheduleTask = peekScheduledTask();
            if(scheduleTask == null){
                Runnable task = null;
                try{
                    task = taskBlockQueue.take();
                }catch (InterruptedException ex){
                    // ignore
                }
                return task;
            }else{
                long delayNanos = scheduleTask.delayNanos();
                Runnable task = null;
                if(delayNanos > 0){
                    try {
                        task = taskBlockQueue.poll(delayNanos, TimeUnit.NANOSECONDS);
                    }catch (InterruptedException ex){
                        return null;
                    }
                }
                if(task == null){
                    fetchFormScheduledTaskQueue();
                    task = taskBlockQueue.poll();
                }
                if(task != null){
                    return task;
                }
            }
        }
    }


    protected long delayNanos(long currentTimeNanos){
        ScheduleTask scheduleTask = peekScheduledTask();
        if(scheduleTask == null){
            return SCHEDULE_PURGE_INTERVAL;
        }
        return scheduleTask.delayNanos(currentTimeNanos);
    }

    protected long deadlineNanos(){
        ScheduleTask scheduleTask = peekScheduledTask();
        if(scheduleTask == null){
            return nanoTime() + SCHEDULE_PURGE_INTERVAL;
        }
        return scheduleTask.deadlineNanos();
    }

    protected Runnable peekTask(){
        return taskQueue.peek();
    }

    protected boolean haskTask(){
        return !taskQueue.isEmpty();
    }

    public int pendingTasks(){
        return taskQueue.size();
    }

    protected void addTask(Runnable task){
        ObjectUtil.checkNotNull(task, "task");
        if(!offerTask(task)){
            reject(task);
        }
    }

    final boolean offerTask(Runnable task){
        if(isShutdown()){
            reject();
        }
        return taskQueue.offer(task);
    }

    protected boolean removeTask(Runnable task){
        ObjectUtil.checkNotNull(task, "task");
        return taskQueue.remove(task);
    }

    protected static void reject() {
        throw new RejectedExecutionException("event executor terminated");
    }

    protected final void reject(Runnable task) {
        rejectedExecutionHandler.rejected(task, this);
    }
}
