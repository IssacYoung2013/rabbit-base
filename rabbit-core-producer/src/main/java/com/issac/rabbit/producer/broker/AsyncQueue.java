package com.issac.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author: ywy
 * @date: 2020-11-21
 * @desc:
 */
@Slf4j
public class AsyncQueue {
    public static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    public static final int QUEUE_SIZE = 10000;

    private static ExecutorService senderAsync = new ThreadPoolExecutor(
            THREAD_SIZE, THREAD_SIZE, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(QUEUE_SIZE),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setName("rabbitmq_client_async_sneder");
                    return t;
                }
            },
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    log.error("async sender is error,runnable:{}",r);
                }
            }
    );

    public static void submit(Runnable runnable){
        senderAsync.submit(runnable);
    }
}
