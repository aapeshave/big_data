package com.demo.service;

import com.demo.service.impl.QueueServiceImpl;
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

}