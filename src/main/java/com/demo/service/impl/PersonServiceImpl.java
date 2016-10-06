package com.demo.service.impl;


import com.demo.service.PersonService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class PersonServiceImpl
    implements PersonService
{
    @Override
    public String addPerson(String personData) {

        Jedis jedis = new Jedis("localhost");
        jedis.incr("PERSON_COUNT");
        String personUid = "person" + "__" + "someName" + "__" +jedis.get("PERSON_COUNT");
        jedis.set(personUid, personData);

        return jedis.get(personUid);
    }
}
