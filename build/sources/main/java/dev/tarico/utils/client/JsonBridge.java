package dev.tarico.utils.client;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JsonBridge {
    public static JsonElement parseString(String json) throws JsonSyntaxException {
        return JsonBridge.parseReader(new StringReader(json));
    }

    public static JsonElement parseReader(Reader reader) throws JsonIOException, JsonSyntaxException {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            JsonElement element = JsonBridge.parseReader(jsonReader);
            if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            }
            return element;
        }
        catch (MalformedJsonException e) {
            throw new JsonSyntaxException((Throwable)((Object)e));
        }
        catch (IOException e) {
            throw new JsonIOException((Throwable)e);
        }
        catch (NumberFormatException e) {
            throw new JsonSyntaxException((Throwable)e);
        }
    }

    public static JsonElement parseReader(JsonReader reader) throws JsonIOException, JsonSyntaxException {
        boolean lenient = reader.isLenient();
        reader.setLenient(true);
        try {
            JsonElement jsonElement = Streams.parse((JsonReader)reader);
            return jsonElement;
        }
        catch (StackOverflowError e) {
            throw new JsonParseException("Failed parsing JSON source: " + (Object)((Object)reader) + " to Json", (Throwable)e);
        }
        catch (OutOfMemoryError e) {
            throw new JsonParseException("Failed parsing JSON source: " + (Object)((Object)reader) + " to Json", (Throwable)e);
        }
        finally {
            reader.setLenient(lenient);
        }
    }

    @Deprecated
    public JsonElement parse(String json) throws JsonSyntaxException {
        return JsonBridge.parseString(json);
    }

    @Deprecated
    public JsonElement parse(Reader json) throws JsonIOException, JsonSyntaxException {
        return JsonBridge.parseReader(json);
    }

    @Deprecated
    public JsonElement parse(JsonReader json) throws JsonIOException, JsonSyntaxException {
        return JsonBridge.parseReader(json);
    }
}
