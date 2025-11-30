package com.example.driver_service.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UserServiceClient {
    private final WebClient webClient;
    private final ReactiveCircuitBreaker circuitBreaker;

    public record InternalUserInfo(String userId, String name) {}

    public record UserCreateRequest(
            String email,
            String password,
            String role,
            String username,
            String phoneNumber
    ) {}

    public record UserCreateResponse(
            Long id,
            String userId,
            String email,
            String username
    ) {}

    public UserServiceClient(WebClient.Builder builder,
                             @Value("${services.user-service.url}") String serviceUrl,
                             ReactiveCircuitBreakerFactory cbFactory) {
        this.webClient = builder.baseUrl(serviceUrl).build();
        this.circuitBreaker = cbFactory.create("user-service");
    }

    // 유저 생성 (기사 가입 시 호출)
    public Mono<UserCreateResponse> createUser(UserCreateRequest request) {
        Mono<UserCreateResponse> apiCall = webClient.post()
                                                    .uri("/internal/api/users")
                                                    .bodyValue(request)
                                                    .retrieve()
                                                    .bodyToMono(UserCreateResponse.class)
                                                    .doOnError(e -> log.error("기사 계정(User) 생성 실패. email: {}", request.email(), e));

        // 회원가입은 실패하면 롤백되어야 하므로 Fallback(가짜 데이터 반환)을 쓰지 않습니다.
        // 서킷이 열려있으면 즉시 에러를 던져서 DriverService의 트랜잭션을 롤백시킵니다.
        return circuitBreaker.run(apiCall);
    }

}