package com.demo.service.impl;

import com.demo.configuration.RabbitConfiguration;
import com.demo.service.QueueService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

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
        // Message newMessage = new Message();

        myTemplate.convertAndSend(_outQueue.getName(), message);
    }

    @Override
    @RabbitListener(queues = OUT_QUEUE)
    public String receiveMessage(String data) {
        System.out.println("Data received: " + data );
        return data;
    }
}
