package com.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

/**
 * Created by ajinkya on 10/17/16.
 */
@Service
public interface UserService {
    /**
     *
     * @param userObject
     * @return
     */
    String addUser(JSONObject userObject) throws JsonProcessingException, ParseException;

    /**
     *
     * @param userPath
     * @return
     */
    String getUser(String userPath) throws ParseException;
}