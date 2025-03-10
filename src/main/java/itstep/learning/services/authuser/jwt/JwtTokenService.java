package itstep.learning.services.authuser.jwt;


 import java.util.Base64;
 import java.util.Date;
 import java.util.logging.Level;
 import java.util.logging.Logger;

 import itstep.learning.services.hash.HashService;
 import com.google.gson.JsonObject;
 import com.google.gson.JsonParser;
 import com.google.inject.Inject;
 import com.google.inject.Singleton;

@Singleton
public class JwtTokenService implements JwtToken {

    private final Logger logger;

    private final HashService hashService;

    private JsonObject header;
    private JsonObject payLoad;
    private String signature;
    private String secretKey;

    @Inject
    public JwtTokenService(Logger logger, HashService hashService) {
        header = JsonParser.parseString("{'alg':'HS256', 'typ':'JWT'}").getAsJsonObject();
        this.logger = logger;
        this.hashService = hashService;
    }

    @Override
    public void setHeader(JsonObject header) {
        this.header = header;
    }

    @Override
    public void setPayLoad(JsonObject payLoad) {
        this.payLoad = payLoad;
    }

    @Override
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String createJwtToken() {

        String headerJwt = new String(Base64.getUrlEncoder().encode(header.toString().getBytes()));
        String payLoadJwt = new String(Base64.getUrlEncoder().encode(payLoad.toString().getBytes()));
        signature = new String(
                Base64.getUrlEncoder().encode(hashService.digest(secretKey + headerJwt + "." + payLoadJwt).getBytes()));

        String jwtToken = headerJwt + "." + payLoadJwt + "." + signature;

        return jwtToken;
    }

    @Override
    public String fromJwt(String jwt) {

        String[] parts = jwt.split("\\.");

        if (parts.length != 3) {

            logger.log(Level.WARNING, "JwtTokenService::fromJwt Token format invalid");

            return null;
        }

        String jwtheader = parts[0];
        String jwtpayload = parts[1];
        String jwtsignature = new String(Base64.getUrlDecoder().decode(parts[2]));

        if (!jwtsignature.equals(hashService.digest(secretKey + jwtheader + "." + jwtpayload))) {

            logger.log(Level.WARNING, "JwtTokenService::fromJwt Token signuture error");

            return null;
        }
        jwtpayload = new String(Base64.getUrlDecoder().decode(jwtpayload));

        return jwtpayload;
    }

}