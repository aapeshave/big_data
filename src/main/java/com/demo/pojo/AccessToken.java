package com.demo.pojo;


import java.util.Date;

public class AccessToken
{
    private String tokenId;
    private String issuer;
    private Date createdOn;
    private Date validTill;
    private String accessUrl;
    private String role;
    private String tokenUid;

    public AccessToken(String tokenId, String issuer, Date createdOn, Date validTill, String accessUrl, String role) {
        this.tokenId = tokenId;
        this.issuer = issuer;
        this.createdOn = createdOn;
        this.validTill = validTill;
        this.accessUrl = accessUrl;
        this.role = role;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTokenUid() {
        return tokenUid;
    }

    public void setTokenUid(String tokenUid) {
        this.tokenUid = tokenUid;
    }

    @Override
    public String toString() {
        return "AccessToken{" +
                "tokenUid='" + tokenUid + '\'' +
                ", role='" + role + '\'' +
                ", accessUrl='" + accessUrl + '\'' +
                ", validTill=" + validTill +
                ", createdOn=" + createdOn +
                ", issuer='" + issuer + '\'' +
                ", tokenId='" + tokenId + '\'' +
                '}';
    }
}
