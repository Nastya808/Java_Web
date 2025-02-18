package itstep.learning.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RestService {
    private final Gson gson = new Gson();

    public void sendResponse(HttpServletResponse resp, RestResponse restResponse) throws IOException {
        resp.setContentType("application/json");
        setCorsHeaders(resp);
        resp.getWriter().print(gson.toJson(restResponse));
    }

    public void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-headers", "authorization, content-type");
        resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,PATCH,OPTIONS");
    }

    public <T> T fromJson(String json, Class<T> classofT) {

        return gson.fromJson(json, classofT);
    }

    public <T> T fromBody(HttpServletRequest req, Class<T> classofT) throws JsonSyntaxException, IOException {

        String charsetNAme = req.getCharacterEncoding();

        if (charsetNAme == null) {

            charsetNAme = StandardCharsets.UTF_8.name();

        }

        return gson.fromJson(new String(
                req.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8

        ), classofT);
    }

}
