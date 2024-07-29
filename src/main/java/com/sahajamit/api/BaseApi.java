package com.sahajamit.api;

import com.sahajamit.config.*;
import com.sahajamit.dto.AuthenticationRequest;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

@NoArgsConstructor
public class BaseApi extends CucumberSpringConfiguration {
    private final String OAUTH_PATH = "oauth/token";
    private String accessToken;


    protected RequestSpecification getRequestSpecification(){
        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://"+ ConfigUtil.getString("audience"))
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .addFilter(new AllureRestAssured())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .build();
        return RestAssured.given(requestSpecification);
    }

    protected RequestSpecification getRequestSpecification(String baseUrl){
        return RestAssured.given(getRequestSpecification().baseUri(baseUrl));
    }

    protected RequestSpecification getRequestSpecification(boolean addAuthToken){
        return RestAssured.given(getRequestSpecification()
                .header("Authorization",getOAuthToken()));
    }

    @Step("Getting the OAuth Access Token")
    protected String getOAuthToken(){
        if(Objects.isNull(accessToken)){
            AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                    .client_id(ConfigUtil.getString("client_id"))
                    .client_secret(ConfigUtil.getString("client_secret"))
                    .grant_type(ConfigUtil.getString("grant_type"))
                    .audience(ConfigUtil.getString("audience"))
                    .build();

            Response response = RestAssured.given(getRequestSpecification().body(authenticationRequest))
                    .post(OAUTH_PATH);
            int code = response.statusCode();
            JsonPath jsonPath = new JsonPath(response.asString());
            accessToken = jsonPath.getString ("access_token");
        }
        return accessToken;
    }
}
