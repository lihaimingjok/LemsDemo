package com.pcjz.lems.business.common.jackson;


import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by ThinkPad on 2017-08-17.
 */

public class JacksonMapper {

        private static final ObjectMapper mapper = new ObjectMapper();

        private JacksonMapper() {

        }
        public static ObjectMapper getInstance() {
            return mapper;

        }
}
