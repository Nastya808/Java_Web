package itstep.learning.services.time;


import java.time.Instant;
import java.util.Date;

public class UtilTimeService implements TimeService {

    @Override
    public Date getTimestamp() {
        return Date.from(Instant.now());
    }

}
