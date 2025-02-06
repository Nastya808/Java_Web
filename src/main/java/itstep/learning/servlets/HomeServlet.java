package itstep.learning.servlets;

import com.google.gson.Gson;
import itstep.learning.models.UserSignUpFormModel;
import itstep.learning.rest.RestResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = new String(req.getInputStream().readAllBytes());
        UserSignUpFormModel user = gson.fromJson(body, UserSignUpFormModel.class);

        System.out.println("Received data:");
        System.out.println("Name: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Extra Emails: " + user.getExtraEmails());
        System.out.println("Phones: " + user.getPhones());
        System.out.println("City: " + user.getCity());
        System.out.println("Login: " + user.getLogin());

        sendJson(resp, 201, "Data received");
    }

    private void sendJson(HttpServletResponse resp, int status, String message) throws IOException {
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.getWriter().print(gson.toJson(new RestResponse().setStatus(status).setMessage(message)));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:5173");
        resp.setHeader("Access-Control-Allow-Headers", "content-type");
    }
}
