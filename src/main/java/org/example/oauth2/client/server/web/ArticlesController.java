package org.example.oauth2.client.server.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@RestController
public class ArticlesController {

    @Autowired
    private WebClient webClient;

    @GetMapping(value = "/articles")
    public String[] getArticles(
      @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") OAuth2AuthorizedClient authorizedClient
    ) {
        return this.webClient
          .get()
          .uri("http://resource-server:8090/articles")
          .attributes(oauth2AuthorizedClient(authorizedClient))
          .retrieve()
          .bodyToMono(String[].class)
          .block();
    }

    @GetMapping(value = "greeting")
    public String getGreeting(@RegisteredOAuth2AuthorizedClient("articles-client-oidc") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange("http://resource-server:8090/greeting", HttpMethod.GET, request, String.class).getBody();
    }

    @GetMapping(value = "token")
    public String getToken(@RegisteredOAuth2AuthorizedClient("articles-client-oidc") OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> request = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange("http://resource-server:8090/token", HttpMethod.GET, request, String.class).getBody();
    }
}