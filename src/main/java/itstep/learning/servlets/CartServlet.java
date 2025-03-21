package itstep.learning.servlets;

import java.io.IOException;

import itstep.learning.dal.dao.DataContext;
import itstep.learning.rest.RestService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Singleton
public class CartServlet extends HttpServlet {
    private final DataContext dataContext;
    private final RestService restService;
    @Inject
    public CartServlet(DataContext dataContext,RestService restService){

        this.dataContext=dataContext;
        this.restService=restService;

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
