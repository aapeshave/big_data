package com.demo.controller;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

@RestController
@RequestMapping("/access_token")
public class AccessTokenController {
    private static final String API_SECRET = "aap1212";

    @POST
    @RequestMapping("/")
    private String createToken(@RequestBody(required = true) TokenEntity tokenEntity) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(API_SECRET);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(tokenEntity.id)
                .setIssuedAt(now)
                .setSubject(tokenEntity.subject)
                .setIssuer(tokenEntity.issuer)
                .claim("person", "Ajinkya")
                .signWith(signatureAlgorithm, signingKey);

        //TODO: You can add more claims
        return builder.compact();
    }

    @GET
    @RequestMapping("/validate")
    public String isTokenValidated(@RequestHeader String token, HttpServletResponse response) throws BadRequestException, IOException {

        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(API_SECRET))
                    .parseClaimsJws(token).getBody();
            System.out.println("ID: " + claims.getId());
            System.out.println("Subject: " + claims.getSubject());
            System.out.println("Issuer: " + claims.getIssuer());
            System.out.println("Person: " + claims.get("person"));
            System.out.println("Expiration: " + claims.getExpiration());
        } catch (ExpiredJwtException e) {
            response.sendError(429, "Token is expired");
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
           response.sendError(403, "Can not validate token");
        }
        return token;
    }


    public static class TokenEntity {
        @JsonProperty(required = true)
        public String id;
        @JsonProperty(required = true)
        public String issuer;
        @JsonProperty(required = true)
        public String subject;
        @JsonProperty
        public long ttlMillis;
    }
}
