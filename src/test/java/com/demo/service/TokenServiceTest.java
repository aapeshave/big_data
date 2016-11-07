package com.demo.service;

import com.demo.pojo.AccessToken;
import com.demo.service.impl.TokenServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TokenServiceTest {

    TokenServiceImpl tokenService;

    @Before
    public void setUp() throws Exception {
        tokenService = new TokenServiceImpl();
    }

    @Test
    public void testCreateAccessToken() throws Exception {

    }

    @Test
    public void testIsTokenValidated() throws Exception {

    }

    @Test
    public void testCreateAccessTokenAPI() throws Exception {
        String userUid = "user__1";
        String role = "admin";
        String subject = "ACCESS_TOKEN";
        AccessToken accessToken = tokenService.createAccessTokenAPI(userUid, role, subject);
        Assert.assertNotNull(accessToken);
    }

    @Test
    public void testCreateAccessToken1() throws Exception {

    }
}