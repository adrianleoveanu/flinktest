package util;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

public class MapIt extends RichMapFunction<String, String> {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String map(String value) throws Exception {
        //Map<String, Object> mapped = objectMapper.readValue(value, Map.class);
        //Object rsvp = mapped.get("rsvp_id");
        return value ;
    }
}
