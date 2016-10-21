package com.demo.service.impl;

import com.demo.controller.AccessTokenController;
import com.demo.pojo.AccessToken;
import com.demo.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Calendar;
import java.util.Date;

@Service
public class TokenServiceImpl
    implements TokenService
{
    private String TOKEN_COUNT = "TOKEN_COUNT";

    private static final String API_SECRET = "aap1212";

    private static final String ISSUER = "DEMO.INC";

    private static final String URL = "http://www.example.com";

    @Override
    public AccessToken createAccessToken(AccessTokenController.TokenEntity tokenEntity, String userUid)
    {
        return null;
    }

    @Override
    public AccessToken createAccessTokenAPI(String userUid, String role, String subject) throws JsonProcessingException
    {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(API_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        Jedis jedis = getJedis();

        JwtBuilder builder = Jwts.builder().setId(jedis.get(TOKEN_COUNT))
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(ISSUER)
                .claim("user", userUid)
                .claim("url", URL)
                .signWith(signatureAlgorithm, signingKey);

        builder.setExpiration(getNextYearDate());

        AccessToken token = new AccessToken(jedis.get(TOKEN_COUNT), ISSUER, getNextYearDate(), URL, role, builder.compact());
        String tokenId = "token" + "__" + jedis.get(TOKEN_COUNT);
        ObjectMapper mapper = new ObjectMapper();
        try {
            jedis.set(tokenId, mapper.writeValueAsString(token));
            jedis.close();
        } catch (JsonProcessingException e) {
            throw e;
        }
        return token;
    }

    @Override
    public JSONObject createAccessToken(String userUid, String role, String subject) throws JsonProcessingException, ParseException {
        AccessToken accessToken = createAccessTokenAPI(userUid, role, subject);
        ObjectMapper mapper = new ObjectMapper();
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(mapper.writeValueAsString(accessToken));
        return object;
    }

    private Date getNextYearDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }
    private Jedis getJedis()
    {
        Jedis jedis = new Jedis("localhost");
        jedis.incr(TOKEN_COUNT);
        return jedis;
    }
}