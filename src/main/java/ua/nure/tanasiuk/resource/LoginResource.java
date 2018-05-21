package ua.nure.tanasiuk.resource;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ua.nure.tanasiuk.dto.LoginData;
import ua.nure.tanasiuk.dto.Token;
import ua.nure.tanasiuk.service.Oauth2AuthenticationService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.BasicAuthDefinition;

@Validated
@RestController
@RequestMapping(value = "/v1/login", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "login", description = "Operations for getting access token.")
public class LoginResource {

    private final Oauth2AuthenticationService oauth2AuthenticationService;

    public LoginResource(Oauth2AuthenticationService oauth2AuthenticationService) {
        this.oauth2AuthenticationService = oauth2AuthenticationService;
    }

    @ApiOperation("Login via email and password credentials")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "User was logged in via email"),
        @ApiResponse(code = 400, message = "Validation is failed, need to check OperationResponse.status")
    })
    @PostMapping("/email")
    public ResponseEntity loginViaEmail(@RequestBody LoginData loginData) {
        return ResponseEntity
            .ok(oauth2AuthenticationService.loginViaEmail(loginData.getEmail(), loginData.getPassword()));
    }

    @ApiOperation("Get new access and refresh token by refresh token.")
    @PostMapping("/refresh")
    @BasicAuthDefinition(key = "basicAuth")
    public ResponseEntity<OAuth2AccessToken> refreshToken(@RequestBody @Valid Token token)
        throws HttpRequestMethodNotSupportedException {
        return ResponseEntity.ok(oauth2AuthenticationService.getRefreshToken(token.getValue()));
    }
}
