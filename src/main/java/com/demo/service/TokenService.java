package com.demo.service;


import com.demo.controller.AccessTokenController;
import com.demo.pojo.AccessToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface TokenService {
    /**
     * Create Token from Token Entity
     *
     * @param tokenEntity from client
     * @param userUid     of user
     * @return created token
     */
    AccessToken createAccessToken(AccessTokenController.TokenEntity tokenEntity, String userUid);

    /**
     * Validate the token
     *
     * @param tokenBody from user
     * @return
     */
    Boolean isTokenValidated(String tokenBody, String userUid) throws ExpiredJwtException, SignatureException, MalformedJwtException;

    /**
     * Gets userId from tokenBody. Does some part of validation
     *
     * @param tokenBody from user
     * @return userUid from token
     */
    String getUserIdFromToken(String tokenBody) throws UnsupportedJwtException, IllegalArgumentException;

    AccessToken createAccessTokenAPI(String userUid, String role, String subject) throws JsonProcessingException;

    JSONObject createAccessToken(String userUid, String role, String subject) throws JsonProcessingException, ParseException;
}
