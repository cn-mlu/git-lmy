package com.lmy.thread;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 队列线程管理
 *
 * @author : mingyang.lu
 * @date : 2023/12/20 17:43
 */
@Slf4j
public class QueueWorkerManager {

    private static ExecutorService threadPool;

//    /**
//     * 线程池
//     */
//    private static final ExecutorService threadPool = Executors.newFixedThreadPool(
//            3,
//            new ThreadFactory() {
//                private final AtomicInteger threadNumber = new AtomicInteger(1);
//
//                @Override
//                public Thread newThread(Runnable r) {
//                    return new Thread(r, "YourThread-" + threadNumber.getAndIncrement());
//                    // 在此可以设置更多线程属性，例如优先级等
//                }
//            }
//    );


    /**
     * 线程状态和队列名称的映射
     *
     * @date : 2023/12/20 17:44
     */
    private final static Map<String, ThreadState> threadStates = new ConcurrentHashMap<>();

    /**
     * 排队队列
     */
    private final static Map<String, ThreadState> qeueuUpMap = new HashMap<>();


    /**
     * 注册队列状态
     *
     * @param queueName: queueName
     * @author : mingyang.lu
     * @date : 2023/12/20 18:01
     */
    public static void registerQueue(String queueName) {
        threadStates.put(queueName, ThreadState.IDLE);
        threadPool = Executors.newFixedThreadPool(3);
    }

    /**
     * 1. 进来消息，会先放入map中，标识需要使用
     * 2. 判断状态，如果当前队列的状态为空闲，则执行
     * 3. 如果使用状态
     * 如果正在执行，判断是否有别的空闲队列
     * 如果被借用，则不处理，一直循环
     *
     * @param queueName: queueName
     * @param consumer:  consumer
     * @return : void
     * @author : mingyang.lu
     * @date : 2023/12/21 13:27
     */

    public static void process(String queueName, Consumer<Object> consumer) {
        qeueuUpMap.put(queueName, ThreadState.MAIN_QUEUE_WANT);
        if (threadStates.containsKey(queueName)) {
            // 获取队列状态
            // 如果空闲中，则执行
            boolean flag = true;
            while (flag) {
                ThreadState threadState = threadStates.get(queueName);
                //空闲中
                if (threadState == ThreadState.IDLE) {
                    //执行
//                    Future<?> future = threadPool.submit(() -> {
                    CompletableFuture.runAsync(() -> {
                        try {
                            threadStates.put(queueName, ThreadState.ACTIVE); // 标记为"使用中"
                            System.out.println("Processing queue: " + queueName);
                            // 执行队列的任务
                            consumer.accept(queueName);
                        } finally {
                            // 任务执行完后，标记为"空闲中"
                            threadStates.put(queueName, ThreadState.IDLE);
                            qeueuUpMap.remove(queueName);
                        }
                    }, threadPool);

                    flag = false;
                } else {
                    //使用中
                    if (threadState == ThreadState.ACTIVE) {
                        System.out.println("当前队列"+queueName+"线程状态为使用中，开始借用其他队列的线程");
                        String borrowKey = borrowThread();
                        if (StringUtils.isNotBlank(borrowKey)) {
                            System.out.println("借到了"+borrowKey+"的线程，开始执行"+queueName+"的任务");
                            CompletableFuture.runAsync(() -> {
                                try {
                                    threadStates.put(borrowKey, ThreadState.BORROWED); // 标记为"被借用中"
                                    System.out.println("Processing queue: " + queueName);
                                    // 执行队列的任务
                                    consumer.accept(queueName);
                                } finally {
                                    // 任务执行完后，标记为"空闲中"
                                    threadStates.put(borrowKey, ThreadState.IDLE);
                                }
                            }, threadPool);

                            flag = false;
                        }else{
                            System.out.println("当前队列"+queueName+"未借到其他线程");
                        }
                    } else if (threadState == ThreadState.BORROWED) {
                        qeueuUpMap.put(queueName,ThreadState.MAIN_QUEUE_WANT);
                        System.out.println("当前队列"+queueName+"线程状态为被借用中");
                    }
                    log.info("当前队列：{}，线程状态：{}", queueName, threadStates.get(queueName));
                }

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static String borrowThread() {
        for (String key : threadStates.keySet()) {
            ThreadState threadState = threadStates.get(key);
            if (threadState.equals(ThreadState.IDLE) && !qeueuUpMap.containsKey(key)) {
                System.out.println("队列名字：" + key + "状态为空闲可以被借");
                threadStates.put(key, ThreadState.BORROWED);
                return key;
            }
        }
        return "";
    }

    private enum ThreadState {
        /**
         * 空闲中
         */
        IDLE,
        /**
         * 自己使用中
         */
        ACTIVE,
        /**
         * 被借用使用中
         */
        BORROWED,
        /**
         * 主队列要用
         */
        MAIN_QUEUE_WANT
    }
}

