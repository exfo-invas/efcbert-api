package com.invas.enhanced.fc.bert.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class StandardConfig {

    String fcRate;
    double frameSize;

}
