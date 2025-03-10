package itstep.learning.servlets;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.fileupload2.core.FileItem;

import itstep.learning.services.form_parse.FormParseResult;
import itstep.learning.services.form_parse.FormParseService;
import itstep.learning.services.storage.StorageService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Singleton
public class ProductServlet extends HttpServlet {
    private final FormParseService formParseService;
    private final StorageService storageService;

    @Inject
    public ProductServlet(StorageService storageService, FormParseService formParseService) {
        this.formParseService = formParseService;
        this.storageService = storageService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FormParseResult formParseResult = formParseService.parseRequest(req);
        FileItem file1 = formParseResult.getFiles().get("file1");
        String message;
        if (file1.getSize() > 0) {
            int dotPosition=file1.getName().lastIndexOf('.');
            String ext=file1.getName().substring(dotPosition);
            String fileId=storageService.put(file1.getInputStream(), ext);
            message =fileId;
        } else {
            message = "No file submitted";


        }
        resp.getWriter().print(message);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        if ("categories".equals(type)) {

            getCategories(req, resp);

        } else if ("category".equals(type)) {
            getCategory(req, resp);
        } else {
            getProducts(req, resp);
        }
    }

    private void getCategories(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void getCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    private void getProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
