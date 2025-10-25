package ru.asteises.authservice.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.asteises.authservice.model.SecurityUser;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class MyController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal SecurityUser principal) {
        return Map.of(
                "email", principal.getUsername(),
                "authorities", principal.getAuthorities().stream().map(Object::toString).toList()
        );
    }

    @GetMapping("/")
    public Map<String, Object> ping() {
        return Map.of("status", "ok");
    }
}
