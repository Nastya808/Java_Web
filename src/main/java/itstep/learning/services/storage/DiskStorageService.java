package itstep.learning.services.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import itstep.learning.services.config.ConfigService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DiskStorageService implements StorageService {

    private final ConfigService configService;

    @Inject
    public DiskStorageService(ConfigService configService) {
        this.configService = configService;

    }

    @Override
    public String put(InputStream inputStream, String ext) throws IOException {

        String itemId = UUID.randomUUID() + ext;
        File file = new File(configService.getValue("storage.path").getAsString() + itemId);

        try (FileOutputStream writer = new FileOutputStream(file)) {
            byte[] buf = new byte[131072];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                writer.write(buf, 0, len);
            }

        }

        return itemId;
    }

    @Override
    public InputStream get(String itemId) throws IOException {

        return new FileInputStream(configService.getValue("storage.path").getAsString() + itemId);
    }

}
