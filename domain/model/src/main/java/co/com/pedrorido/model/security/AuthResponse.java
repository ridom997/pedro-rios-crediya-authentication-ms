package co.com.pedrorido.model.security;

import co.com.pedrorido.model.role.Role;
import reactor.core.publisher.Mono;

import java.util.Set;

public record AuthResponse(String accessToken, Set<String> roles) {}
