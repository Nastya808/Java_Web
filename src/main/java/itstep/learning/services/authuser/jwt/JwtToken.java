package itstep.learning.services.authuser.jwt;


import com.google.gson.JsonObject;

public interface JwtToken {
    public void setHeader(JsonObject header);

    public void setPayLoad(JsonObject payLoad);

    public void setSecretKey(String secretKey);

    public String createJwtToken();

    public String fromJwt(String jwt);

}
