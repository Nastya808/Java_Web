package itstep.learning.ioc;

import itstep.learning.servlets.HomeServlet;
import com.google.inject.servlet.ServletModule;

public class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        serve("/home").with(HomeServlet.class);
    }
}