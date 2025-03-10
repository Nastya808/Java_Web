package itstep.learning.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import itstep.learning.services.storage.StorageService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Singleton
public class StorageServlet extends HttpServlet {

    private final StorageService storageService;

    @Inject
    public StorageServlet(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String fileId = req.getPathInfo().substring(1);
        try (InputStream inputStream = storageService.get(fileId)) {

            int dotPosition=fileId.lastIndexOf('.');
            String ext=fileId.substring(dotPosition);
            resp.setContentType(mimeByExtention(ext));
            OutputStream writer = resp.getOutputStream();
            byte[] buf = new byte[131072];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                writer.write(buf, 0, len);
            }

        } catch (IOException ex) {
            resp.setStatus(404);
        }

    }

    private String mimeByExtention(String ext) {

        switch (ext) {
            case ".jpg":
                ext = ".jpeg";
            case ".jpeg":
            case ".gif":
            case ".webp":
            case ".png":
            case ".bmp":
                return "image/" + ext.substring(1);

            case ".txt":
                ext = ".plain";
            case ".css":
            case ".csv":
            case ".html":
                return "text/" + ext.substring(1);

            case ".js":
            case ".mjs":
                return "text/javascript";

            default:
                return "application/octet-stream";

        }

    }

}
