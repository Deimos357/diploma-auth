package ua.nure.tanasiuk.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "spring.security")
public class SecurityConfigBean {
    private String jwtSignature;
    private String defaultClient;
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;
}
