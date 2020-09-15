package com.examples.pubsub.streaming.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TamagochiDtoValidator {

    private final static Logger LOG = LoggerFactory.getLogger(TamagochiDtoValidator.class);

    private static final String NAME_PATTERN = "[A-Za-z_]*";

    public static boolean isUserDtoValid(TamagochiDto tamagochiDto) {
        return Stream.of(tamagochiDto.getName(), tamagochiDto.getBellyful(), tamagochiDto.getHealth(),
                tamagochiDto.getMorale())
                .allMatch(TamagochiDtoValidator::stringIsValid);
    }

    private static boolean stringIsValid(String name) {
        if (name == null || name.isEmpty() || !Pattern.compile(NAME_PATTERN).matcher(name).matches()) {
            LOG.error("{} : is not a valid string", name);
            return false;
        }
        return true;
    }
}
