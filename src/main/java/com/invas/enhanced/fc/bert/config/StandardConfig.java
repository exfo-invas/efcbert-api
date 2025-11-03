package com.invas.enhanced.fc.bert.config;

import com.invas.enhanced.fc.bert.model.event.StandardTestResponse;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class StandardConfig {

    StandardTestResponse standardTestResponse;
}
