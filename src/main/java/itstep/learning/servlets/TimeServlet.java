package itstep.learning.servlets;

import com.google.gson.Gson;
import itstep.learning.rest.RestResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Timestamp time = new Timestamp(System.currentTimeMillis());

        // Формат в виде Timestamp (long)
        long timestamp = time.getTime();

        // Формат в виде ISO
        String isoFormat = DateTimeFormatter.ISO_DATE_TIME.format(time.toLocalDateTime());

        // Формат в стиле SHORT
        String localizedFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(time.toLocalDateTime());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(
                gson.toJson(new RestResponse().setStatus(200).setMessage(
                        "Timestamp: " + timestamp + " / ISO: " + isoFormat + " / Localized: " + localizedFormat
                ))
        );
    }
}
