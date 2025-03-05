package itstep.learning.ioc;

import itstep.learning.filters.AuthFilter;
import itstep.learning.filters.AuthJwtFilter;
import itstep.learning.filters.CharSetFilter;
import itstep.learning.servlets.HomeServlet;
import com.google.inject.servlet.ServletModule;
import itstep.learning.servlets.RandomServlet;
import itstep.learning.servlets.UserServlet;

public class ServletConfig extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(CharSetFilter.class);
        filter("/*").through(AuthFilter.class);
        filter("/*").through(AuthJwtFilter.class);

        serve("/home").with(HomeServlet.class);
        serve("/user").with(UserServlet.class);
        serve("/random").with(RandomServlet.class);
    }
}