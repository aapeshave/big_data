package com.demo.service;


import com.demo.controller.AccessTokenController;
import com.demo.pojo.AccessToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONObject;

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

    JSONObject createAccessToken(String userUid, String role, String subject) throws JsonProcessingException;
}
