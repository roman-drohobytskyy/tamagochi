package com.examples.pubsub.streaming.util;

import com.examples.pubsub.streaming.dto.TamagochiDto;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TamagochiDtoValidator {

    private final static Logger LOG = LoggerFactory.getLogger(TamagochiDtoValidator.class);

    private static final String NAME_PATTERN = "[A-Za-z_]*";

    public static boolean isUserDtoValid(TamagochiDto tamagochiDto) {
        return Stream
            .of(tamagochiDto.getName(), tamagochiDto.getBellyful(), tamagochiDto.getHealth(),
                tamagochiDto.getMorale())
            .allMatch(TamagochiDtoValidator::stringIsValid);
    }

    private static boolean stringIsValid(String value) {
        if (value == null || value.isEmpty() || !Pattern.compile(NAME_PATTERN).matcher(value)
            .matches()) {
            LOG.error("Field {} : is not a valid string", value);
            return false;
        }
        return true;
    }
}
