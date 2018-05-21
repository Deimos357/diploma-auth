package ua.nure.tanasiuk.service;

import static ua.nure.tanasiuk.common.Constants.ErrorCodes.EMAIL_OR_PASSWORD_IS_INVALID;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import ua.nure.tanasiuk.exception.PhoneValidationException;
import ua.nure.tanasiuk.model.UserIdentity;
import ua.nure.tanasiuk.security.SecurityConfigBean;

@Service
public class Oauth2AuthenticationService {
    private final AuthorizationServerEndpointsConfiguration configuration;
    private final TokenEndpoint tokenEndpoint;
    private final SecurityConfigBean securityConfigBean;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final ResourceServerTokenServices tokenServices;

    public Oauth2AuthenticationService(
        AuthorizationServerEndpointsConfiguration configuration,
        TokenEndpoint tokenEndpoint,
        SecurityConfigBean securityConfigBean,
        PasswordEncoder passwordEncoder,
        UserService userService,
        DefaultTokenServices tokenServices) {
        this.configuration = configuration;
        this.tokenEndpoint = tokenEndpoint;
        this.securityConfigBean = securityConfigBean;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.tokenServices = tokenServices;
    }

    public OAuth2AccessToken loginViaEmail(String email, String password) {
        List<UserIdentity> users = userService.findByEmail(email);
        if (!users.isEmpty() && users.size() == 1) {
            UserIdentity user = users.get(0);
            if (passwordEncoder.matches(password, user.getPassword())) {
                return createOauth2Token(user);
            }
        }

        throw new PhoneValidationException(EMAIL_OR_PASSWORD_IS_INVALID);
    }

    public OAuth2AccessToken getRefreshToken(String refreshToken)
        throws HttpRequestMethodNotSupportedException {
        Principal principal = securityConfigBean::getDefaultClient;
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            principal, null, new ArrayList<>());
        Map<String, String> parameters = new HashMap<>();
        parameters.put("grant_type", "refresh_token");
        parameters.put("refresh_token", refreshToken);
        ResponseEntity<OAuth2AccessToken> oAuth2AccessTokenResponseEntity = tokenEndpoint
            .postAccessToken(authentication, parameters);
        OAuth2AccessToken body = oAuth2AccessTokenResponseEntity.getBody();
        OAuth2Authentication oAuth2Authentication = tokenServices.loadAuthentication(body.getValue());
        UserIdentity userIdentity = UserIdentity.builder()
            .id(Long.parseLong(oAuth2Authentication.getName()))
            .build();
        return body;
    }

    private OAuth2AccessToken createOauth2Token(UserIdentity userIdentity) {
        OAuth2Request oauth2Request = new OAuth2Request(new HashMap<>(), securityConfigBean.getDefaultClient(),
            userIdentity.getAuthorities(), true, new HashSet<>(), new HashSet<>(), null, new HashSet<>(),
            new HashMap<>());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            userIdentity, null, new ArrayList<>());

        OAuth2Authentication auth = new OAuth2Authentication(oauth2Request, authenticationToken);

        AuthorizationServerTokenServices tokenService = configuration.getEndpointsConfigurer().getTokenServices();

        return tokenService.createAccessToken(auth);
    }
}
