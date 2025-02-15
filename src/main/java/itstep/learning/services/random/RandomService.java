package itstep.learning.services.random;

public interface RandomService {
    int randomInt();
    String noRestrictionsStr(int length);
    String fileNameRandomStr(int length);
}
