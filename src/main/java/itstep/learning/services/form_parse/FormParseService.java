package itstep.learning.services.form_parse;


import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;

public interface FormParseService {

    public FormParseResult parseRequest(HttpServletRequest req)throws IOException;
}