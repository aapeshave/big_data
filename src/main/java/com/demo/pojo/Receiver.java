package com.demo.pojo;

import java.util.concurrent.CountDownLatch;

/**
 * Created by ajinkya on 11/16/16.
 */
public class Receiver {
    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessage(String message) {
        System.out.println("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }
}
