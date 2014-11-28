package com.github.arteam.simplejsonrpc.client;

import com.github.arteam.simplejsonrpc.client.domain.Position;
import com.google.common.base.Optional;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Date: 11/30/14
 * Time: 5:45 PM
 *
 * @author Artem Prigoda
 */
public class GsonProvider {

    public static Gson get() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                        return new JsonPrimitive(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                .withZoneUTC()
                                .print(src.getTime()));
                    }
                })
                .registerTypeAdapter(Date.class, new JsonDeserializer() {
                    @Override
                    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                        return new Date(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                                .withZoneUTC()
                                .parseMillis(json.getAsString()));
                    }
                })
                .registerTypeAdapter(Position.class, new TypeAdapter<Position>() {
                    @Override
                    public void write(JsonWriter out, Position value) throws IOException {
                        out.value(value != null ? value.getCode() : null);
                    }

                    @Override
                    public Position read(JsonReader in) throws IOException {
                        return Position.byCode(in.nextString());
                    }
                })
                .registerTypeAdapterFactory(new TypeAdapterFactory() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> typeToken) {
                        // Get actual type E from Optional<E>
                        if (typeToken.getRawType() != Optional.class) {
                            return null;
                        }
                        final Type actualType = ((ParameterizedType) typeToken.getType()).getActualTypeArguments()[0];
                        return (TypeAdapter<T>) new TypeAdapter<Optional<?>>() {
                            @Override
                            public void write(JsonWriter out, Optional<?> value) throws IOException {
                                gson.toJson(value.orNull(), actualType, out);
                            }

                            @Override
                            public Optional<?> read(JsonReader in) throws IOException {
                                return Optional.fromNullable(gson.fromJson(in, actualType));
                            }
                        };
                    }
                }).create();
    }
}
