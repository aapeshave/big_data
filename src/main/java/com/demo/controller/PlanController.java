package com.demo.controller;

import com.demo.service.PlanService;
import com.demo.service.SchemaService;
import com.demo.service.TokenService;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.ResourceNotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ajinkya on 11/28/16.
 */

@Controller
@Api(description = "API to do CRUD Operations on Plan")
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    TokenService _tokenService;

    @Autowired
    SchemaService _schemaService;

    @Autowired
    PlanService _planService;


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create a plan with benefits",
            notes = "Note: You need to provide authentication token",
            response = PlanAggregate.class)
    @ApiResponses(value = {
            @ApiResponse(code = 401,
                    message = "customer not authorized to make api call",
                    responseHeaders = @ResponseHeader(
                            name = "UNAUTHORIZED",
                            description = "customer not authorized to make api call")),
            @ApiResponse(code = 400,
                    message = "Bad Request",
                    responseHeaders = @ResponseHeader(
                            name = "BAD_REQUEST",
                            description = "bad request")),
            @ApiResponse(code = 500,
                    message = "Internal Server Error",
                    responseHeaders = @ResponseHeader(
                            name = "GENERAL_ERROR",
                            description = "unhandled exception occured"))
    })
    public PlanAggregate createPlan(
            @ApiParam(value = "Authentication Token. It is usually created when you create User Account.")
            @RequestHeader(required = true) String token,
            @ApiParam(value = "JSON Body for plan. Refer to Schemas for more info")
            @RequestBody String planBody,
            HttpServletResponse response) throws IOException {
        try {
            if (_tokenService.isTokenValidated(token, "SampleString")) {
                String userUid = _tokenService.getUserIdFromToken(token);
                Validate.notNull(userUid, "UserUid can not be null to do further actions");
                String pathToSchema = "SCHEMA__" + getPathToSchema(planBody);
                if (_schemaService.validateSchema(pathToSchema, planBody)) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode planNode = mapper.readTree(planBody);
                    ((ObjectNode) planNode).put("userUid", userUid);
                    String result = _planService.addPlan(planNode);
                    if (StringUtils.isNotBlank(result)) {
                        PlanAggregate responseEntity = new PlanAggregate();
                        JSONObject responseObject = (JSONObject) new JSONParser().parse(result);
                        responseEntity._id = (String) responseObject.get("_id");
                        processETag(response, responseObject);
                        responseEntity._objectInfo = responseObject;
                        return responseEntity;
                    } else {
                        response.sendError(500, "Our Servers are Having Problems");
                    }
                } else {
                    response.sendError(401, "Schema not found.");
                }
            }
        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token is expired. Exception: " + e.toString());
        } catch (SignatureException | MalformedJwtException e) {
            response.sendError(401, "Token is malformed. Exception: " + e.toString());
        } catch (ProcessingException e) {
            response.sendError(500, "Failed While Processing Schema");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/{uid}", method = RequestMethod.GET)
    public String getPlan(
            @ApiParam(value = "Authentication Token. It is usually created when you create User Account.")
            @RequestHeader(required = true) String token,
            @PathVariable("uid") String planUid,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        try {
            if (_tokenService.isTokenValidated(token, "SampleString")) {
                JSONObject plan = null;
                try {
                    plan = _planService.getPlan(planUid);
                    Validate.notNull(plan);
                    String eTag = request.getHeader("If-None-Match");
                    if (StringUtils.isNotBlank(eTag)) {
                        String eTagFromObject = (String) plan.get("ETag");
                        if (StringUtils.isNotEmpty(eTagFromObject)) {
                            if (StringUtils.equals(eTagFromObject, eTag)) {
                                response.sendError(304, "Object is not modified");
                            }
                        }
                    }
                    return plan.toJSONString();
                } catch (ResourceNotFoundException e) {
                    response.sendError(404, e.toString());
                }
            }
        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token is expired. Exception: " + e.toString());
        } catch (SignatureException | MalformedJwtException e) {
            response.sendError(401, "Token is malformed. Exception: " + e.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/{uid}/benefit", method = RequestMethod.PUT)
    public PlanAggregate addBenefitToPlan(@ApiParam(value = "Authentication Token. It is usually created when you create User Account.")
                                    @RequestHeader(required = true) String token,
                                    @ApiParam(value = "Uid of the plan for which benefit to be added")
                                    @PathVariable("uid") String planUid,
                                    @ApiParam(value = "JSON Body for benefit. Refer to Schemas for more info")
                                    @RequestBody String benefitBody,
                                    HttpServletResponse response) throws IOException {
        try {
            if (_tokenService.isTokenValidated(token, "Sample String"))
            {
                TokenService.TokenInfo tokenInfo = _tokenService.getTokenInfo(token);
                Validate.notNull(tokenInfo);
                if (StringUtils.isNotBlank(tokenInfo.role) && StringUtils.equals("admin", tokenInfo.role))
                {
                    String pathToSchema = "SCHEMA__" + getPathToSchema(benefitBody);
                    Validate.notNull(pathToSchema);
                    if (_schemaService.validateSchema(pathToSchema, benefitBody))
                    {
                        JSONObject result = _planService.addBenefitToPlan(benefitBody, planUid);
                        Validate.notNull(result);
                        PlanAggregate responseEntity = new PlanAggregate();
                        responseEntity._id = (String) result.get("_id");
                        processETag(response, result);
                        responseEntity._objectInfo = result;
                        return responseEntity;
                    }
                    else {
                        response.sendError(401, "Bad Schema. Please check schema or add ObjectName field in the payload");
                    }
                }
                else {
                    response.sendError(403, "Admin role is needed to perform this action. Consider getting an admin token");
                }
            }
        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token is expired. Exception: " + e.toString());
        } catch (SignatureException | MalformedJwtException e) {
            response.sendError(401, "Token is malformed. Exception: " + e.toString());
        } catch (ProcessingException e) {
            response.sendError(401, "Bad Schema. Please check schema or add ObjectName field in the payload. More Info: " + e.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String getPlanUsingParameters()
    {
        return null;
    }


    @ResponseBody
    @RequestMapping(value = "/{uid}/benefit/{benefitUid}", method = RequestMethod.PATCH)
    public PlanAggregate patchBenefit(@ApiParam(value = "Authentication Token. It is usually created when you create User Account.")
                                          @RequestHeader(required = true) String token,
                                      @ApiParam(value = "Uid of the plan for which benefit to be added")
                                          @PathVariable("uid") String planUid,
                                      HttpServletResponse response,
                                      @ApiParam(value = "Uid of the benefit for which benefit to be added")
                                      @PathVariable("benefitUid") String benefitUid,
                                      @ApiParam(value = "Body to be patched")
                                      @RequestBody String benefitData) throws IOException {
        try {
            if (_tokenService.isTokenValidated(token, "Sample String"))
            {
                TokenService.TokenInfo tokenInfo = _tokenService.getTokenInfo(token);
                Validate.notNull(tokenInfo);
                Integer flag = 0 ;
                if (StringUtils.isNotBlank(tokenInfo.role) && StringUtils.equals("admin", tokenInfo.role))
                {
                    JSONObject plan = null;
                    try {
                        plan = _planService.getPlan(planUid);
                        Validate.notNull(plan);
                        List<JSONObject> benefits = (ArrayList<JSONObject>) plan.get("benefit");
                        Validate.notNull(benefits);
                        for (JSONObject object : benefits)
                        {
                            if (object.containsValue(benefitUid))
                            {
                                JSONObject objectToBePatched = (JSONObject) new JSONParser().parse(benefitData);
                                JSONObject result = _planService.patchBenefitOfThePlan(planUid, object, objectToBePatched);
                                Validate.notNull(result);
                                PlanAggregate responseObject = new PlanAggregate();
                                responseObject._id = (String) result.get("_id");
                                responseObject._objectInfo = result;
                                flag =1 ;
                                return responseObject;
                            }
                        }
                        if (flag == 0)
                        {
                            response.sendError(404, "Benefit not found");
                        }
                    } catch (ResourceNotFoundException e) {
                        response.sendError(404, "Plan not found");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    response.sendError(403, "Admin role is needed to perform this action. Consider getting an admin token");
                }
            }
        } catch (ExpiredJwtException e) {
            response.sendError(401, "Token is expired. Exception: " + e.toString());
        } catch (SignatureException | MalformedJwtException e) {
            response.sendError(401, "Token is malformed. Exception: " + e.toString());
        }
        return null;
    }

    private void processETag(HttpServletResponse response, JSONObject responseObject) {
        String eTag = (String) responseObject.get("ETag");
        if (StringUtils.isNotBlank(eTag)) {
            response.addHeader("ETag", eTag);
            responseObject.remove("ETag");
        }
    }

    private String getPathToSchema(String payload) {
        JSONObject bodyObject = null;
        try {
            bodyObject = (JSONObject) new JSONParser().parse(payload);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Validate.notNull(bodyObject);
        return (String) bodyObject.get("objectName");
    }

    @ApiModel(description = "Plan Aggregate Model")
    public static class PlanAggregate {
        @ApiModelProperty(value = "Identifier. Use this to Request Plan from Server")
        @JsonProperty(required = true)
        String _id;
        @ApiModelProperty(value = "Additional Information about the plan")
        @JsonProperty(required = true)
        JSONObject _objectInfo;
    }
}
