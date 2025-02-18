package itstep.learning.ioc;

import itstep.learning.servlets.HomeServlet;
import com.google.inject.servlet.ServletModule;
import itstep.learning.servlets.RandomServlet;
import itstep.learning.servlets.UserServlet;

public class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        serve("/home").with(HomeServlet.class);
        serve("/user").with(UserServlet.class);
        serve("/random").with(RandomServlet.class);
    }
}