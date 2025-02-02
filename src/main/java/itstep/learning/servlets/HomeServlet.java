package itstep.learning.servlets;

import com.google.gson.Gson;
import com.mysql.cj.jdbc.MysqlDataSource;
import itstep.learning.rest.RestResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import oracle.jdbc.pool.OracleDataSource;

import java.io.IOException;
import java.sql.*;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message;
        try {
            // DriverManager.registerDriver(
            //         new com.mysql.cj.jdbc.Driver()
            // );
            // String connectionString = "jdbc:mysql://localhost:3308/java221";
            // Connection connection = DriverManager.getConnection(
            //         connectionString,
            //         "user221",
            //         "pass221"
            // );
            // String sql = "SELECT CURRENT_TIMESTAMP";

            // POM.XML: com.oracle.database.jdbc
            // OracleDataSource ods = new OracleDataSource();
            // ods.setURL( "jdbc:oracle:thin:@localhost/XE" );
            // Connection connection = ods.getConnection( "SYSTEM", "root" );
            // String sql = "SELECT CURRENT_TIMESTAMP FROM DUAL";

            MysqlDataSource mds = new MysqlDataSource();
            mds.setURL("jdbc:mysql://localhost:3308/java");
            Connection connection = mds.getConnection( "user221", "pass221" );
            String sql = "SELECT CURRENT_TIMESTAMP";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery( sql );
            resultSet.next();
            message = resultSet.getString( 1 );   // !!! JDBC відлік з 1
            // resultSet.close();

            resultSet = statement.executeQuery( "SHOW DATABASES" );
            StringBuilder sb = new StringBuilder();
            while ( resultSet.next() ) {
                sb.append( ", " );
                sb.append( resultSet.getString( 1 ) );
            }
            resultSet.close();
            statement.close();
            message += sb.toString();
        }
        catch( SQLException ex ) {
            message = ex.getMessage();
        }
        sendJson( resp, 200, message );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = new String( req.getInputStream().readAllBytes() );
        sendJson( resp, 201, body );
    }

    private void sendJson(HttpServletResponse resp, int status, String message ) throws IOException {
        resp.setContentType( "application/json" );
        resp.setHeader( "Access-Control-Allow-Origin", "http://localhost:5173" );
        resp.getWriter().print(
                gson.toJson(
                        new RestResponse()
                                .setStatus( status )
                                .setMessage( message )
                )
        );
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader( "Access-Control-Allow-Origin", "http://localhost:5173" );
        resp.setHeader( "Access-Control-Allow-Headers", "content-type" );
    }
}

