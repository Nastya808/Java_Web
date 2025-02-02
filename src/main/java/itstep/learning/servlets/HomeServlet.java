package itstep.learning.servlets;

import com.mysql.cj.jdbc.MysqlDataSource;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.*;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String message = "";

        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setURL("jdbc:mysql://localhost:3306/");
            dataSource.setUser("root");
            dataSource.setPassword("");


            try (Connection connection = dataSource.getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SHOW DATABASES")) {

                StringBuilder databases = new StringBuilder();
                while (resultSet.next()) {
                    if (databases.length() > 0) databases.append(", ");
                    databases.append(resultSet.getString(1));
                }
                message = databases.toString();
            }
        } catch (SQLException ex) {
            message = "Ошибка SQL: " + ex.getMessage();
        }

        resp.setContentType("text/plain; charset=UTF-8");
        resp.getWriter().print(message);
    }
}
