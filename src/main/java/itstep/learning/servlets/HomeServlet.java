package itstep.learning.servlets;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.User;
import itstep.learning.models.UserSignUpFormModel;
import itstep.learning.rest.RestResponse;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;
import itstep.learning.services.random.RandomService;
import itstep.learning.services.time.TimeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import jakarta.servlet.http.HttpServlet;

@Singleton
public class HomeServlet extends HttpServlet {
    private final Gson gson = new Gson();
    private final RandomService randomService;
    private final TimeService timeService;
    private final KdfService kdfService;
    private final DbService dbService;
    private final DataContext dataContext;
    @Inject
    public HomeServlet(DataContext dataContext, DbService dbService,RandomService randomService,TimeService timeService,KdfService kdfService){

        this.randomService=randomService;
        this.timeService=timeService;
        this.kdfService=kdfService;
        this.dbService=dbService;
        this.dataContext=dataContext;

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message;
        try {
            Statement statement = dbService.getConnection().createStatement();
            message=dataContext.getUserDao().installTables()?"Install OK":"Install fail";
        } catch (SQLException e) {
            message = e.getMessage();
        }


        sendJson(resp, new RestResponse()
                .setResourceUrl("POST /home")
                .setStatus(200).setMessage(message+ ", " +" Seed "+ timeService.getSeed()+ " , random: "+randomService.randomInt()+", bound:1000"));


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = new String(req.getInputStream().readAllBytes());

        UserSignUpFormModel model;
        RestResponse restResponse=new RestResponse()
                .setResourceUrl("POST /home")
                .setCashTime(0);

        try {

            model= gson.fromJson(body, UserSignUpFormModel.class);

        } catch (Exception ex) {
            sendJson(resp, restResponse
                    .setStatus(422)
                    .setMessage(ex.getMessage())
            );
            return;
        }
        sendJson(resp, restResponse
                .setStatus(201)
                .setMessage("Create")
                .setMeta(Map.of(

                        "DataType", "object",
                        "read", "GET /home",
                        "update", "PUT /home",
                        "delete", "DELETE /home"))
                .setData(model));

    }

    // Setting cors
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-headers", "content-type");
    }

    /****/
    private void sendJson(HttpServletResponse resp, RestResponse restResponse) throws IOException {
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().print(
                gson.toJson(restResponse));
    }

}
