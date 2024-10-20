package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.CellDto;
import dto.CoordinateDto;
import dto.deserializer.CellDtoDeserializer;
import dto.deserializer.CoordinateMapDeserializer;
import dto.serializer.CellDtoSerializer;
import dto.serializer.CoordinateMapSerializer;
import engine.api.Engine;
import engine.impl.EngineImpl;
import jakarta.servlet.ServletContext;

import java.util.Map;

public class ServletUtils {

    private static final String ENGINE_ATTRIBUTE_NAME = "engine";
    private static final String GSON_ATTRIBUTE_NAME = "gson";

    private static final Object engineLock = new Object();
    private static final Object gsonLock = new Object();

    public static Engine getEngine(ServletContext servletContext) {

        synchronized (engineLock) {
            if (servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(ENGINE_ATTRIBUTE_NAME, EngineImpl.create());
            }
        }
        return (Engine) servletContext.getAttribute(ENGINE_ATTRIBUTE_NAME);
    }

    public static Gson getGson(ServletContext servletContext) {

        synchronized (gsonLock) {
            if (servletContext.getAttribute(GSON_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(GSON_ATTRIBUTE_NAME, new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(CellDto.class,new CellDtoSerializer())
                        .registerTypeAdapter(CellDto.class,new CellDtoDeserializer())
                        .registerTypeAdapter(new TypeToken<Map<CoordinateDto, CoordinateDto>>(){}.getType(), new CoordinateMapSerializer())
                        .registerTypeAdapter(new TypeToken<Map<CoordinateDto, CoordinateDto>>(){}.getType(), new CoordinateMapDeserializer())
                        .create());
            }
        }
        return (Gson) servletContext.getAttribute(GSON_ATTRIBUTE_NAME);
    }
}

