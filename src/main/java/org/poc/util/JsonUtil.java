package org.poc.util;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class JsonUtil {
    public static final DateTimeFormatter isoDateTimeFormatter = ISODateTimeFormat.dateTime().withZoneUTC();

    private static final ObjectMapper objectMapper = newObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Creates a new ObjectMapper that includes support for date serialization in ISO date format and
     * org.joda.time data formats.
     *
     * We use joda time rather than java.text.SimpleDateFormat because of its robust handling of different
     * date formats and, unlike SimpleDateFormatter, it is actually thread safe. This allows us to keep the
     * ObjectMapper thread safe as well.
     * (Credits for suggestion of using Joda time : Clemens Rossell)
     *
     * @return a new ObjectMapper with ISO-8601 and org.joda.time date serialization support
     */
    // Initializes object mapper with base serializers and deserializers
    public static ObjectMapper newObjectMapper() {
        return newObjectMapper("Base");
    }

    private static ObjectMapper newObjectMapper(String name) {
        ObjectMapper mapper = new ObjectMapper();
        // Enable auto-detection of all fields (public, private, protected) by default.
        // Setter and getter detection is disabled by default (can be selectively re-enabled with annotations).
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);

        // Set up customer serializers / deserializers
        SimpleModule module = new SimpleModule(name, new Version(1, 0, 0, null));

        // ISO 8601 date format handling for java.util.Date and org.joda.time.DateTime types.
        // We use joda time rather than java.text.SimpleDateFormat because of its robust handling of different
        // date formats and, unlike SimpleDateFormatter, it is actually thread safe. This allows us to keep the
        // ObjectMapper thread safe as well.
        module.addSerializer(new StdSerializer<Date>(Date.class) {
            @Override
            public void serialize(Date date, JsonGenerator jg, SerializerProvider sp) throws IOException {
                jg.writeString(isoDateTimeFormatter.print(date.getTime()));
            }
        });
        module.addSerializer(new StdSerializer<DateTime>(DateTime.class) {
            @Override
            public void serialize(DateTime date, JsonGenerator jg, SerializerProvider sp) throws IOException {
                jg.writeString(isoDateTimeFormatter.print(date));
            }
        });
        module.addSerializer(new StdSerializer<Instant>(Instant.class) {
            @Override
            public void serialize(Instant date, JsonGenerator jg, SerializerProvider sp) throws IOException {
                jg.writeString(isoDateTimeFormatter.print(date.toEpochMilli()));
            }
        });
        module.addDeserializer(Date.class, new StdScalarDeserializer<Date>(Date.class) {
            @Override
            public Date deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
                try {
                    return new Date(isoDateTimeFormatter.parseDateTime(jp.getText()).getMillis());
                } catch (IllegalArgumentException e) {
                    throw dc.mappingException("Unable to parse date: " + e.getMessage());
                }
            }
        });
        module.addDeserializer(DateTime.class, new StdScalarDeserializer<DateTime>(DateTime.class) {
            @Override
            public DateTime deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
                try {
                    return isoDateTimeFormatter.parseDateTime(jp.getText());
                } catch (IllegalArgumentException e) {
                    throw dc.mappingException("Unable to parse date: " + e.getMessage());
                }
            }
        });
        module.addDeserializer(Instant.class, new StdScalarDeserializer<Instant>(Instant.class) {
            @Override
            public Instant deserialize(JsonParser jp, DeserializationContext dc) throws IOException {
                try {
                    return Instant.ofEpochMilli(isoDateTimeFormatter.parseDateTime(jp.getText()).getMillis());
                } catch (IllegalArgumentException e) {
                    throw dc.mappingException("Unable to parse date: " + e.getMessage());
                }
            }
        });


        mapper.registerModule(module);
        return mapper;
    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to serialize object to json", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to de-serialize json: " + json, e);
        }
    }

    private JsonUtil() {}
}
