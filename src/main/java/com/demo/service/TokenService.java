package com.demo.service;


import com.demo.controller.AccessTokenController;
import com.demo.pojo.AccessToken;
import com.demo.pojo.User;

public interface TokenService
{
    /**
     * Create Token from Token Entity
     * @param tokenEntity
     * @param userAccount
     * @return
     */
    AccessToken createAccessToken(AccessTokenController.TokenEntity tokenEntity, User userAccount);
}
