package itstep.learning.servlets;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.Category;
import itstep.learning.dal.dto.Product;
import itstep.learning.rest.RestResponse;
import itstep.learning.rest.RestService;
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
    private final RestService restService;
    private final DataContext dataContext;

    @Inject
    public ProductServlet(DataContext dataContext, RestService restService, StorageService storageService,
                          FormParseService formParseService) {
        this.formParseService = formParseService;
        this.storageService = storageService;
        this.restService = restService;
        this.dataContext = dataContext;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FormParseResult formParseResult = formParseService.parseRequest(req);
        RestResponse restResponse = new RestResponse()
                .setResourceUrl("POST /product")
                .setMeta(Map.of(
                        "DataType", "object",
                        "read", "GET /product",
                        "update", "PUT /product",
                        "delete", "DELETE /product"));
        Product product = new Product();
        String str;

        str = formParseResult.getFields().get("product-title");
        if (str == null || str.isBlank()) {

            restService.sendResponse(resp, restResponse.setStatus(400).setData("Missing or empty 'product-title' "));
            return;

        }
        product.setProductTitle(str);

        str = formParseResult.getFields().get("product-description");

        if (str == null || str.isBlank()) {

            restService.sendResponse(resp,
                    restResponse.setStatus(400).setData("Missing or empty 'product-description' "));
            return;

        }
        product.setProductDescription(str);

        str = formParseResult.getFields().get("product-code");

        if (str == null || str.isBlank()) {
            restService.sendResponse(resp,
                    restResponse.setStatus(400).setData("Missing or empty 'product-code' "));
            return;
        }

        if (dataContext.getProductDao().checkSlugCode(str)) {

            restService.sendResponse(resp,
                    restResponse.setStatus(409).setData("Conflict product code"));
            return;
        }

        product.setProductSlug(str);

        str = formParseResult.getFields().get("product-price");

        try {

            product.setPrice(Double.parseDouble(str));
        } catch (NumberFormatException | NullPointerException ex) {
            restService.sendResponse(resp,
                    restResponse.setStatus(400).setData("Missing or empty 'product-price' "));
            return;

        }

        str = formParseResult.getFields().get("product-stock");

        try {

            product.setStrock(Integer.parseInt(str));
        } catch (NumberFormatException | NullPointerException ex) {
            restService.sendResponse(resp,
                    restResponse.setStatus(400).setData("Missing or empty 'product-stock' "));
            return;

        }

        str = formParseResult.getFields().get("category-id");
        try {

            product.setCategoryId(UUID.fromString(str));
        } catch (Exception ex) {
            restService.sendResponse(resp,
                    restResponse.setStatus(400).setData("Missing or empty 'product-category id' "));
            return;

        }

        FileItem file1 = formParseResult.getFiles().get("product-image");

        if (file1.getSize() > 0) {
            int dotPosition = file1.getName().lastIndexOf('.');
            String ext = file1.getName().substring(dotPosition);
            str = storageService.put(file1.getInputStream(), ext);
        } else {

            str = null;
        }

        product.setProductImageId(str);

        product = dataContext.getProductDao().addNewProduct(product);

        if (product == null) {
            storageService.deleteImg(str);

            restService.sendResponse(resp, restResponse.setStatus(500)
                    .setData("Internal Error.  See logs"));
            return;

        }

        restService.sendResponse(resp, restResponse.setStatus(200).setData(product));

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        if ("categories".equals(type)) {// .../product?type=categories

            getCategories(req, resp);

        } else if ("category".equals(type)) { // .../product?type=category&id=1235
            getCategory(req, resp);
        } else {
            getProducts(req, resp);
        }
    }

    private void getCategories(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String imgPath = String.format(Locale.ROOT, "%s://%s:%d%s/storage/",
                req.getScheme(),
                req.getServerName(),
                req.getServerPort(),
                req.getContextPath()

        );
        List<Category> categories = dataContext.getCategoryDao().getList();

        for (itstep.learning.dal.dto.Category c : categories) {

            c.setCategoryImageId(imgPath + c.getCategoryImageId());
        }

        restService.sendResponse(resp,
                new RestResponse()
                        .setResourceUrl("GET /product?type=categories")
                        .setMeta(Map.of(
                                "dataType", "array"))
                        .setStatus(200)
                        .setCashTime(86400)
                        .setData(categories)

        );

    }

    private void getCategory(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String slug = req.getParameter("slug");
        RestResponse restResponse = new RestResponse()
                .setResourceUrl("GET /product?type=category&slug=" + slug)
                .setMeta(Map.of(
                        "DataType", "object"))
                .setCashTime(86400);
        Category category;
        try {
            category = dataContext.getCategoryDao().getCategoryBySlug(slug);
        } catch (RuntimeException ex) {
            restService.sendResponse(resp, restResponse.setStatus(500).setData("Take a look to the logs"));
            return;
        }

        if (category == null) {
            restService.sendResponse(resp, restResponse.setStatus(400).setData("category not found")

            );
            return;
        }
        restService.sendResponse(resp, restResponse.setStatus(200).setData(category));

    }

    private void getProducts(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
