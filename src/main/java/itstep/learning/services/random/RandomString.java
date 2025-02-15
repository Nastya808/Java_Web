package itstep.learning.services.random;

import java.util.Random;

public class RandomString {
    private final Random random = new Random();
    private final char[] prohibitedCharacters = new char[]{'/', '\\', ':', '*', '?', '<', '>', '|'};

    public String noRestrictionsStr(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) (random.nextInt(94) + 33)); // Генеруємо символи в діапазоні [33, 126]
        }
        return result.toString();
    }

    public String fileNameRandomStr(int length) {
        StringBuilder result = new StringBuilder();
        while (result.length() < length) {
            char randChar = (char) (random.nextInt(94) + 33);
            if (!isProhibited(randChar)) {
                result.append(randChar);
            }
        }
        return result.toString();
    }

    private boolean isProhibited(char ch) {
        for (char prohibited : prohibitedCharacters) {
            if (ch == prohibited) {
                return true;
            }
        }
        return false;
    }
}
