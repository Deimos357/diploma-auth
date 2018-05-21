package ua.nure.tanasiuk.security;

import ua.nure.tanasiuk.model.UserIdentity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenEnhancer extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication auth) {
        UserIdentity user = (UserIdentity) auth.getPrincipal();
        final Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("displayName", user.getDisplayName());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return super.enhance(accessToken, auth);
    }

}
