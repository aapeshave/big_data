package com.demo.service;

/**
 * Created by ajinkya on 11/15/16.
 */
public interface QueueService {

    void sendMessage(String message);

    String receiveMessage(String data);
}
