package itstep.learning.servlets;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignUpFormModel;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Singleton
public class UserServlet extends HttpServlet {
    private final DataContext dataContext;
    private final RestService restService;

    @Inject
    public UserServlet(DataContext dataContext, RestService restService) {
        this.dataContext = dataContext;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("GET /user")
                .setMeta(Map.of(
                        "DataType", "object",
                        "read", "GET /user",
                        "update", "PUT /user",
                        "delete", "DELETE /user"));

        String authHeader = req.getHeader("Authorization");

        if (authHeader == null) {

            restService.sendResponse(res, restResponse.setStatus(401)
                    .setData("Authorization header required"));
            return;

        }

        String authScheme = "Basic ";

        if (!authHeader.startsWith(authScheme)) {
            restService.sendResponse(res, restResponse.setStatus(401)
                    .setData("Authorization cheme error "));
            return;
        }

        String credentials = authHeader.substring(authScheme.length());
        try {
            credentials = new String(Base64.getDecoder().decode(credentials.getBytes()));
        } catch (Exception ex) {

            restService.sendResponse(res, restResponse.setStatus(422)
                    .setData("Decode error " + ex.getMessage()));
            return;
        }

        String[] parts = credentials.split(":", 2);

        if (parts.length != 2) {

            restService.sendResponse(res, restResponse.setStatus(422)
                    .setData("Format error spliting by ':' "));
            return;

        }

        User user = dataContext.getUserDao().autorize(parts[0], parts[1]);

        if (user == null) {
            restService.sendResponse(res, restResponse.setStatus(401)
                    .setData("Credentials rejected"));
            return;
        }

        restResponse.setCashTime(600).setStatus(200).setData(user);
        restService.sendResponse(res, restResponse);

    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RestResponse restResponse = new RestResponse()
                .setResourceUrl("DELETE /user")
                .setMeta(Map.of(
                        "dataType", "object",
                        "read", "GET /user",
                        "update", "PUT /user",
                        "delete", "DELETE /user"));
        restResponse
                .setStatus(200)
                .setData("Comming soon")
                .setCashTime(0);
        restService.sendResponse(resp, restResponse);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        RestResponse restResponse = new RestResponse()
                .setResourceUrl("PUT /user")
                .setMeta(Map.of(
                        "DataType", "object",
                        "read", "GET /user",
                        "update", "PUT /user",
                        "delete", "DELETE /user"));

        User userUpdate;
        try {

            userUpdate = restService.fromBody(req, User.class);

        } catch (Exception ex) {
            restService.sendResponse(resp, restResponse
                    .setStatus(422)
                    .setMessage(ex.getMessage()));
            return;
        }

        if (userUpdate == null || userUpdate.getUserId() == null) {
            restService.sendResponse(resp, restResponse
                    .setStatus(422)
                    .setMessage("Unparseable data or identity undefaint"));
            return;

        }
        User user = dataContext.getUserDao().getUserById(userUpdate.getUserId());

        if (user == null) {
            restService.sendResponse(resp, restResponse
                    .setStatus(404)
                    .setMessage("User not found"));
            return;

        }

        if (!dataContext.getUserDao().upDate(userUpdate)) {
            restService.sendResponse(resp, restResponse
                    .setStatus(500)
                    .setMessage("server error. See logs"));
            return;
        }

        restResponse.setCashTime(0).setStatus(200).setData(userUpdate);
        restService.sendResponse(resp, restResponse);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        restService.setCorsHeaders(resp);
    }

}
