package com.demo.service;


import com.demo.pojo.AccessToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public interface TokenService {

    /**
     * Validate the token
     *
     * @param tokenBody from user
     * @return true of token is validated
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

    /**
     * @param token about which info is needed
     * @return object containing info about the token
     */
    TokenInfo getTokenInfo(String token);

    class TokenInfo {
        @JsonProperty(required = true)
        public String tokenUid;
        @JsonProperty(required = true)
        public String tokenId;
        @JsonProperty(required = true)
        public String userUid;
        @JsonProperty(required = true)
        public String role;
        @JsonProperty
        public String issuer;
    }
}
