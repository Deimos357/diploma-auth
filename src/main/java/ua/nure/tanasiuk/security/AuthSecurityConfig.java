package ua.nure.tanasiuk.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableGlobalMethodSecurity
public class AuthSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecurityConfigBean securityConfig;

    public AuthSecurityConfig(SecurityConfigBean securityConfig) {
        this.securityConfig = securityConfig;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.anonymous()
            .and().authorizeRequests().anyRequest().permitAll()
            .and().csrf().disable();
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        CustomTokenEnhancer converter = new CustomTokenEnhancer();
        converter.setSigningKey(securityConfig.getJwtSignature());
        return converter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }

    @Configuration
    @EnableAuthorizationServer
    public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
        private final SecurityConfigBean securityConfig;

        private final UserDetailsService userDetailsService;

        public OAuth2AuthorizationServerConfig(SecurityConfigBean securityConfig,
                                               UserDetailsService userDetailsService) {
            this.securityConfig = securityConfig;
            this.userDetailsService = userDetailsService;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(tokenStore())
                .userDetailsService(userDetailsService)
                .authenticationManager(authenticationManager())
                .reuseRefreshTokens(false)
                .accessTokenConverter(accessTokenConverter());
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                .withClient(securityConfig.getDefaultClient())
                .accessTokenValiditySeconds(securityConfig.getAccessTokenValiditySeconds())
                .refreshTokenValiditySeconds(securityConfig.getRefreshTokenValiditySeconds())
                .scopes("trust")
                .authorizedGrantTypes("refresh_token");
        }
    }
}
