package co.com.pedrorido.api.dto;

import java.util.Map;
import java.util.Set;

public record AuthResponse(
        Set<String> roles,
        Map<String, Object> data
) {}