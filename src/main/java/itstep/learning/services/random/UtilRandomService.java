package itstep.learning.services.random;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.time.TimeService;

import java.util.Random;

@Singleton
public class UtilRandomService implements RandomService {

    private final TimeService timeService;
    private final Random random;

    @Inject
    public UtilRandomService(TimeService timeService) {
        this.timeService = timeService;
        this.random = new Random(timeService.getSeed()); // Ініціалізація генератора з сидом
    }

    @Override
    public int randomInt() {
        return random.nextInt(1000); // Генерація випадкового числа від 0 до 999
    }
}