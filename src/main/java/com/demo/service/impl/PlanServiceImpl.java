package com.demo.service.impl;

import com.demo.service.PlanService;
import com.demo.service.QueueService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ajinkyapeshave on 11/30/16.
 */
@Service
public class PlanServiceImpl implements PlanService {


    private Jedis jedisConnection = new Jedis();

    @Autowired
    QueueService _queueService;

    @Override
    public String addPlan(JsonNode planNode) {
        if (planNode.has("userUid")) {
            JSONObject responseMap = new JSONObject();
            Iterator<String> objectIterator = planNode.fieldNames();
            while (objectIterator.hasNext()) {
                String key = objectIterator.next();
                JsonNode entry = planNode.get(key);
                if (entry instanceof TextNode) {
                    responseMap.put(key, entry);
                } else if (entry instanceof ArrayNode) {
                    Map<Integer, String> arrayMap = new HashMap<>();
                    Integer numberOfObjects = 0;
                    String objectType = null;
                    for (JsonNode entryInArray : entry) {
                        numberOfObjects++;
                        try {
                            String uid = processJsonObject(entryInArray);
                            arrayMap.put(numberOfObjects, uid);
                            objectType = uid.split("__")[0];
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Validate.notNull(objectType);
                        responseMap.put(objectType, arrayMap);
                    }
                } else if (entry != null) {
                    try {
                        String uid = processJsonObject(entry);
                        String objectType = uid.split("__")[0];
                        Validate.notEmpty(objectType);
                        responseMap.put(objectType, uid);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            responseMap.put("_createdOn", getUnixTimestamp());
            TextNode objectType = (TextNode) responseMap.get("objectName");
            String objectTypeToUse = String.valueOf(objectType.asText());
            jedisConnection.incr(objectTypeToUse);
            String uid = objectTypeToUse + "__" + jedisConnection.get(objectTypeToUse);
            responseMap.put("_id", uid);
            try {
                responseMap.put("ETag", calculateETag(responseMap));
            } catch (NoSuchAlgorithmException | ParseException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            jedisConnection.set(uid, responseMap.toJSONString());
            _queueService.sendMessage(responseMap);
            return responseMap.toJSONString();
        }
        return null;
    }

    private String processJsonObject(JsonNode incomingNode) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject toPersist = (JSONObject) parser.parse(incomingNode.toString());
        String uid = getUuidForObject(toPersist);
        String createdOn = getUnixTimestamp();
        toPersist.put("_id", uid);
        toPersist.put("_createdOn", createdOn);
        jedisConnection.set(uid, toPersist.toJSONString());
        _queueService.sendMessage(toPersist);
        return uid;
    }

    private String getUuidForObject(JSONObject toPersist) {
        String objectType = (String) toPersist.get("objectName");
        jedisConnection.incr(objectType);
        return objectType + "__" + jedisConnection.get(objectType);
    }

    private String getUnixTimestamp() {
        Long unixDate = new Date().getTime() / 1000;
        return unixDate.toString();
    }

    private String calculateETag(JSONObject object) throws NoSuchAlgorithmException,
            UnsupportedEncodingException,
            ParseException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] bytesOfMessage = object.toJSONString().getBytes("UTF-8");
        byte[] theDigest = messageDigest.digest(bytesOfMessage);
        return theDigest.toString();
    }
}
