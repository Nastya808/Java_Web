package itstep.learning.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import itstep.learning.ioc.ServiceConfig;
import itstep.learning.rest.RestResponse;
import itstep.learning.services.time.TimeService;
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
    private final TimeService timeService;
    private final Gson gson = new Gson();

    public TimeServlet() {
        this.timeService = com.google.inject.Guice.createInjector(new ServiceConfig())
                .getInstance(TimeService.class);
    }

    @Inject
    public TimeServlet(TimeService timeService) {
        this.timeService = timeService;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Timestamp time = new Timestamp(timeService.getTimestamp().getTime());

        long timestamp = time.getTime();
        String isoFormat = DateTimeFormatter.ISO_DATE_TIME.format(time.toLocalDateTime());
        String localizedFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(time.toLocalDateTime());

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().print(
                gson.toJson(new RestResponse()
                        .setStatus(200)
                        .setMessage("Timestamp: " + timestamp + " / ISO: " + isoFormat + " / Localized: " + localizedFormat))
        );
    }
}
