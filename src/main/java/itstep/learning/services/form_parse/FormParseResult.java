package itstep.learning.services.form_parse;

import java.util.Map;
import org.apache.commons.fileupload2.core.FileItem;

public interface FormParseResult {
    public Map<String,String> getFields();
    public Map<String,FileItem> getFiles();
}

