package itstep.learning.services.storage;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    public String put(InputStream inputStream, String ext)throws IOException;

    public InputStream get(String itemId)throws IOException;

    public boolean deleteImg(String path);
}
