package itstep.learning.ioc;

import itstep.learning.services.random.RandomService;
import itstep.learning.services.random.UtilRandomService;
import itstep.learning.services.time.TimeService;
import itstep.learning.services.time.UtilTimeService;
import com.google.inject.AbstractModule;

public class ServiceConfig extends AbstractModule {

    @Override
    protected void configure() {
        bind(RandomService.class).to(UtilRandomService.class);
        bind(TimeService.class).to(UtilTimeService.class);
    }
}
