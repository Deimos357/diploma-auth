package ua.nure.tanasiuk.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @JsonProperty("token")
    private String value;

    @JsonProperty("accessToken")
    public void setAccessToken(String value) {
        this.value = value;
    }

    @JsonProperty("refreshToken")
    public void setRefreshToken(String value) {
        this.value = value;
    }
}
