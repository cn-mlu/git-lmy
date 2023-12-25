package com.lmy.thread;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author mingyang.lu
 * @date 2023/12/20 17:11
 */
@Slf4j
public class TestThread {
    public static void main(String[] args) {
        QueueWorkerManager.registerQueue("aaa");
        QueueWorkerManager.registerQueue("bbb");
        QueueWorkerManager.registerQueue("ccc");
        AA a = new AA();
        a.setA("张三");

        log.info("a222222222222222222a");
        AA b = new AA();
        b.setA("bb");


        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                Random random = new Random();
                try {
                    Thread.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("发送AAA消息"+i);
                QueueWorkerManager.process("aaa", (queueName) -> test(a));
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                Random random = new Random();
                try {
                    Thread.sleep(random.nextInt(20));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("发送BBB消息"+i);
                QueueWorkerManager.process("bbb", (queueName) -> test(b));

            }
        }).start();

//
//        QueueWorkerManager.process("aaa", (queueName) -> test(a));
//        QueueWorkerManager.process("aaa", (queueName) -> test(a));
//        QueueWorkerManager.process("bbb", (queueName) -> test(b));
//        QueueWorkerManager.process("bbb", (queueName) -> test(b));
    }

    private static void test(AA a) {
        System.out.println(a.getA());
        Random random = new Random();
        try {
            Thread.sleep(random.nextInt(50));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Data
    static class AA {
        private String a;
    }
}
