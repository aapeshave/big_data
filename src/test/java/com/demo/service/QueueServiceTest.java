package com.demo.service;

import com.demo.service.impl.QueueServiceImpl;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ajinkya on 11/15/16.
 */
public class QueueServiceTest {

    QueueServiceImpl _queueService;

    @Before
    public void setUp() throws Exception {
        _queueService = new QueueServiceImpl();
    }

    @Test
    public void testQueService() throws Exception {
        Assert.assertNotNull("Assert that queueService is not null", _queueService);
    }

    @Test
    public void testSendMessage() throws Exception{
        JSONObject sampleObject = new JSONObject();
        sampleObject.put("_id", "role__7");
        sampleObject.put("objectName", "role");
        sampleObject.put("_createdOn", "1479651873");
        sampleObject.put("roleName", "read_only");
        _queueService.sendMessage(sampleObject);
    }

}