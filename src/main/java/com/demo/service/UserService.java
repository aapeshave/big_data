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

    /**
     *
     * @param userPath
     * @param paramterName
     * @param parameterValue
     * @return
     */
    String updateUser(String userPath, String paramterName, String parameterValue);

    /**
     *
     * @param userObject
     * @return
     */
    String newAddUser(JSONObject userObject);
    
    /**
     * 
     * @param pathToObject
     * @return
     */
    JSONObject newGetUser(String pathToObject);

    /**
     *
     * @param userUid
     * @param parameterName
     * @param parameterKey
     * @param parameterValue
     * @return
     */
    Boolean newUpdateUser(String userUid, String parameterName, String parameterKey, String parameterValue);
}
