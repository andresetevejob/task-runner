package com.pipper.andreboot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="piper")
public class PiperProperties {
    @Getter
    @Setter
    private SerializationProperties serialization;
}
