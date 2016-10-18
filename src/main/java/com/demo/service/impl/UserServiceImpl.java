package com.demo.service.impl;

import com.demo.service.TokenService;
import com.demo.service.UserService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

/**
 * Created by ajinkya on 10/17/16.
 */
public class UserServiceImpl
    implements UserService
{

    private String PERSON_COUNT = "PERSON_COUNT";

    private String USER_COUNT = "USER_COUNT";

    @Autowired
    TokenService tokenService;

    @Override
    public String addUser(JSONObject userObject)
    {
        Jedis jedis = new Jedis("localhost");

        jedis.incr(PERSON_COUNT);
        jedis.incr(USER_COUNT);

        JSONObject personObject = (JSONObject) userObject.get("person");
        processKeys(userObject, jedis, personObject);



        return null;
    }

    private void processKeys(JSONObject userObject, Jedis jedis, JSONObject personObject) {
        String personUid = "person" + "__" + personObject.get("firstName") + "__" + jedis.get(PERSON_COUNT);
        String userUid = "user" + "__" + userObject.get("userName") + "__" + jedis.get(USER_COUNT);

        userObject.put("userUid", userUid);
        personObject.put("personUid",personUid);
    }
}
