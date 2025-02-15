package itstep.learning.services.random;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.time.TimeService;

import java.util.Random;

@Singleton
public class UtilRandomService implements RandomService {
    private final TimeService timeService;
    private final Random random = new Random();

    @Inject
    public UtilRandomService(TimeService timeService) {
        this.timeService = timeService;
    }

    public int randomInt() {
        return random.nextInt(1000);
    }

    public String noRestrictionsStr(int length) {
        return new RandomString().noRestrictionsStr(length);
    }

    public String fileNameRandomStr(int length) {
        return new RandomString().fileNameRandomStr(length);
    }
}
