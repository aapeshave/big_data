package com.demo.service.impl;

import com.demo.configuration.RabbitConfiguration;
import com.demo.service.QueueService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Created by ajinkya on 11/15/16.
 */
@Service
public class QueueServiceImpl
        implements QueueService {
    public static final String OUT_QUEUE = "outQueue";

    Queue _outQueue;

    @Autowired
    private ApplicationContext myRabbitContext;
    private AmqpTemplate myTemplate;
    private AmqpAdmin myAdmin;

    public QueueServiceImpl() {
        super();
        this.myRabbitContext = new AnnotationConfigApplicationContext(RabbitConfiguration.class);
        this.myTemplate = this.myRabbitContext.getBean(AmqpTemplate.class);
        this.myAdmin = this.myRabbitContext.getBean(AmqpAdmin.class);
        _outQueue = new Queue(OUT_QUEUE);
        myAdmin.declareQueue(_outQueue);
    }

    @Override
    public void sendMessage(String message) {
        myTemplate.send(_outQueue.getName(), processAndGetMessage(message));
        // TODO: This is to demonstrate that String can be sent directly
        // myTemplate.convertAndSend(_outQueue.getName(), message);
    }

    private Message processAndGetMessage(String data) {
        MessageProperties properties = new MessageProperties();
        properties.setPriority(0);
        byte[] messageBytes = data.getBytes();
        return new Message(messageBytes, properties);
    }

    @Override
    public String receiveMessage(String data) {
        System.out.println("Data received: " + data);
        return data;
    }

// TODO: This is to demonstrate that String can be received directly
//    @RabbitListener(queues = OUT_QUEUE)
//    public void onString(String data)
//    {
//        System.out.println("Data Received: " + data);
//    }

    @RabbitListener(queues = OUT_QUEUE)
    public void onMessage(Message data) {
        String message = new String(data.getBody(), StandardCharsets.UTF_8);
        System.out.println("Data Received: " + message);
    }
}
