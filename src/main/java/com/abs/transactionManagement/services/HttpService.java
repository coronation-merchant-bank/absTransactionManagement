package com.abs.transactionManagement.services;

import com.abs.transactionManagement.exceptionhandler.CustomException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class HttpService {

    private final WebClient webClient;

    public HttpService(WebClient webClient) {
        this.webClient = webClient;
    }

    public ResponseEntity<String> post(Object requestBody, HttpHeaders httpHeaders, String url, int timeoutInSecs) {
        return webClient.post()
                .uri(url)
                .body(BodyInserters.fromValue(requestBody))
                .headers(headers -> headers.addAll(httpHeaders))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .toEntity(String.class).timeout(Duration.ofSeconds(timeoutInSecs), Mono.error(() -> {
                    throw new CustomException("Request Timeout, please try again");
                })).block();
    }

    public ResponseEntity<String> get(HttpHeaders httpHeaders, String url, int timeoutInSecs) {
        return webClient.get()
                .uri(url)
                .headers(headers -> headers.addAll(httpHeaders))
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> Mono.empty())
                .toEntity(String.class).timeout(Duration.ofSeconds(timeoutInSecs), Mono.error(() -> {
                    throw new CustomException("Request Timeout, please try again");
                })).block();
    }
}
