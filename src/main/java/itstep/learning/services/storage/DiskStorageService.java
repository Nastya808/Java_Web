package itstep.learning.services.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import itstep.learning.services.config.ConfigService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DiskStorageService implements StorageService {

    private final ConfigService configService;
    private final Logger logger;

    @Inject
    public DiskStorageService(Logger logger, ConfigService configService) {
        this.configService = configService;
        this.logger = logger;

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

    @Override
    public boolean deleteImg(String itemId) {


        logger.log(Level.WARNING, "DiskStorageService::deleteImg ItemId {0}", itemId);



        if (!itemId.isBlank() && !itemId.isEmpty() && itemId != null) {
            File targetDelete = new File(configService.getValue("storage.path").getAsString() + itemId);

            try {

                if(targetDelete.delete()){
                    logger.log(Level.WARNING, "DiskStorageService::deleteImg {0}", targetDelete);
                    return true;
                }

            } catch (SecurityException ex) {
                logger.log(Level.WARNING, "DiskStorageService::deleteImg {0}", ex.getMessage());

            }
        } else {
            logger.log(Level.WARNING, "DiskStorageService::deleteImg {0}", "File name is empty");
            return false;

        }
        return false;

    }

}
