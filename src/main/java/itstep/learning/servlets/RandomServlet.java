package itstep.learning.servlets;

import java.io.IOException;
import java.util.Map;

import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
import itstep.learning.services.random.RandomService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Singleton
public class RandomServlet extends HttpServlet {
    private final RandomService randomService;
    private final RestService restService;

    @Inject
    public RandomServlet(RandomService randomService, RestService restService) {
        this.randomService = randomService;
        this.restService = restService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String typeParam = req.getParameter("type");
        String lengthParam = req.getParameter("length");

        System.out.println("Received type: " + typeParam);
        System.out.println("Received length: " + lengthParam);

        if (typeParam == null || typeParam.isEmpty() || lengthParam == null || lengthParam.isEmpty()) {
            sendErrorResponse(resp, "Missing required parameters: type and length");
            return;
        }

        int length;
        try {
            length = Integer.parseInt(lengthParam);
            if (length <= 0) {
                sendErrorResponse(resp, "Length must be a positive integer");
                return;
            }
        } catch (NumberFormatException e) {
            sendErrorResponse(resp, "Invalid length format. Must be an integer.");
            return;
        }

        // Генерация строки по типу
        String message;
        switch (typeParam) {
            case "salt":
                message = randomService.noRestrictionsStr(length);
                break;
            case "fileName":
                message = randomService.fileNameRandomStr(length);
                break;
            default:
                sendErrorResponse(resp, "Invalid type parameter. Allowed values: salt, fileName.");
                return;
        }

        RestResponse restResponse = new RestResponse()
                .setStatus(200)
                .setResourceUrl("GET /random")
                .setMeta(Map.of(
                        "dataType", "string",
                        "read", "GET /random",
                        "type", typeParam,
                        "length", String.valueOf(length)
                ))
                .setData(message);

        restService.sendResponse(resp, restResponse);
    }

    private void sendErrorResponse(HttpServletResponse resp, String message) throws IOException {
        RestResponse errorResponse = new RestResponse()
                .setStatus(400)
                .setResourceUrl("GET /random")
                .setData(message);
        restService.sendResponse(resp, errorResponse);
    }
}
