package itstep.learning.services.config;

import java.io.InputStreamReader;
import java.io.Reader;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JsonConfigService implements ConfigService {

    private final Logger logger;
    private JsonElement confiElement;

    @Inject
    public JsonConfigService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public JsonPrimitive getValue(String path) {
        if (confiElement == null) {

            init();
        }
        String[] parts = path.split("\\.");

        JsonObject obj = confiElement.getAsJsonObject();

        for (int i = 0; i < parts.length - 1; i++) {

            JsonElement je = obj.get(parts[i]);
            if (je == null) {
                throw new IllegalStateException("Part of path '" + parts[i] + "' not found");

            }
            obj = je.getAsJsonObject();
        }
        return obj.getAsJsonPrimitive(parts[parts.length - 1]);
    }

    private void init() {
        try (Reader reader = new InputStreamReader(
                this.getClass().
                        getClassLoader().
                        getResourceAsStream("simple_appsettings.json"))
        ){

            confiElement = new Gson().fromJson(reader, JsonElement.class);

        } catch (Exception e) {

            logger.log(Level.WARNING, "JsonConfigService::init", e);
        }
    }

}

