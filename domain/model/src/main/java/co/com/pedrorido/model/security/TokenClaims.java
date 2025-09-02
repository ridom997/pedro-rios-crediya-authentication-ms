package co.com.pedrorido.model.security;

import co.com.pedrorido.model.role.Role;

import java.time.Instant;
import java.util.Set;

public record TokenClaims(String subject, Set<Role> roles, Instant exp, Instant iat, String issuer) {}