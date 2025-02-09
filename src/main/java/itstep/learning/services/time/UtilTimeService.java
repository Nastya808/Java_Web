package itstep.learning.services.time;

import java.time.Instant;
import java.util.Date;

public class UtilTimeService implements TimeService {
    private final long seed;  // Фіксований seed при створенні сервісу

    public UtilTimeService() {
        this.seed = System.nanoTime();  // Генеруємо seed один раз при створенні
    }

    @Override
    public Date getTimestamp() {
        return Date.from(Instant.now());
    }

    @Override
    public long getSeed() {
        return seed;  // Повертаємо збережений seed
    }
}
