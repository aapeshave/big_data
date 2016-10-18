package com.demo.service.impl;

import com.demo.pojo.AccessToken;
import com.demo.service.TokenService;
import com.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.Map;

/**
 * Created by ajinkya on 10/17/16.
 */
@Service
public class UserServiceImpl
    implements UserService
{

    private String PERSON_COUNT = "PERSON_COUNT";

    private String USER_COUNT = "USER_COUNT";

    @Autowired
    TokenService tokenService;

    @Override
    public String addUser(JSONObject userObject) throws JsonProcessingException, ParseException {
        Jedis jedis = new Jedis("localhost");

        jedis.incr(PERSON_COUNT);
        jedis.incr(USER_COUNT);

        JSONObject personObject = (JSONObject) userObject.get("person");
        processKeys(userObject, jedis, personObject);

        JSONObject token = tokenService.createAccessToken((String) userObject.get("userUid"), (String) userObject.get("role"), "ACCESS_TOKEN");

        JSONArray tokens = (JSONArray) userObject.get("tokens");
        if (tokens == null)
        {
            tokens = new JSONArray();
        }
        assert tokens != null;
        tokens.add(token);
        userObject.put("tokens", tokens);

        jedis.set((String )userObject.get("userUid"), userObject.toJSONString());
        jedis.close();

        JSONObject response = new JSONObject();
        response.put("userUid", userObject.get("userUid"));
        response.put("personUid", personObject.get("personUid"));
        response.put("Authorization Header", token.get("tokenUid"));

        return response.toJSONString();
    }

    private void processKeys(JSONObject userObject, Jedis jedis, JSONObject personObject) {
        String personUid = "person" + "__" + personObject.get("firstName") + "__" + jedis.get(PERSON_COUNT);
        String userUid = "user" + "__" + userObject.get("userName") + "__" + jedis.get(USER_COUNT);

        userObject.put("userUid", userUid);
        personObject.put("personUid",personUid);
    }
}
