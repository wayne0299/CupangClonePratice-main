package com.github.cupangclone.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "datasource")
public class DataSourceProperties {

    private String username;
    private String password;
    private String url;
    private String driverClassName;

}
