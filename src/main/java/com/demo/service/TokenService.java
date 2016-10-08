package com.demo.service;


import com.demo.controller.AccessTokenController;
import com.demo.pojo.AccessToken;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface TokenService
{
    /**
     * Create Token from Token Entity
     * @param tokenEntity
     * @param userUid
     * @return
     */
    AccessToken createAccessToken(AccessTokenController.TokenEntity tokenEntity, String userUid);

    AccessToken createAccessTokenAPI(String userUid, String role, String subject) throws JsonProcessingException;
}
