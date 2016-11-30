package com.demo.controller;

import com.demo.service.PlanService;
import com.demo.service.SchemaService;
import com.demo.service.TokenService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ajinkya on 11/28/16.
 */

@Controller
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    TokenService _tokenService;

    @Autowired
    SchemaService _schemaService;

    @Autowired
    PlanService _planService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String newGetUser(@RequestHeader(required = true) String token,
                             @RequestBody String planBody,
                             HttpServletResponse response) throws IOException {
        try {
            if (_tokenService.isTokenValidated(token, "SampleString")) {
                String userUid = _tokenService.getUserIdFromToken(token);
                Validate.notNull(userUid, "UserUid can not be null to do further actions");
                String pathToSchema = "SCHEMA__" + getPathToSchema(planBody);
                if (_schemaService.validateSchema(pathToSchema, planBody))
                {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode planNode = mapper.readTree(planBody);
                    ((ObjectNode) planNode).put("userUid", userUid);
                    String result = _planService.addPlan(planNode);
                    return result;
                }
                else {
                    response.sendError(401, "Schema not found.");
                }
            }
        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token is expired. Exception: " + e.toString());
        } catch (SignatureException | MalformedJwtException e) {
            response.sendError(401, "Token is malformed. Exception: " + e.toString());
        } catch (ProcessingException e) {
            response.sendError(500, "Failed While Processing Schema");
        }
        return null;
    }

    private String getPathToSchema(String payload)
    {
        JSONObject bodyObject = null;
        try {
            bodyObject = (JSONObject) new JSONParser().parse(payload);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Validate.notNull(bodyObject);
        return  (String) bodyObject.get("objectName");
    }

}
