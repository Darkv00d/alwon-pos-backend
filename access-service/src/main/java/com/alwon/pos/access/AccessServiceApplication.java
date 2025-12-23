package com.alwon.pos.access;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
public class AccessServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccessServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/access")
class AccessController {
    @GetMapping("/client-types")
    List<Map<String, Object>> getClientTypes() {
        return List.of(
                Map.of("type", "FACIAL", "name", "Cliente Facial", "color", "#60a917"),
                Map.of("type", "PIN", "name", "Cliente PIN", "color", "#f0a30a"),
                Map.of("type", "NO_ID", "name", "No Identificado", "color", "#e51400"));
    }
}
