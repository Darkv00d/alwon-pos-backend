package com.alwon.pos.camera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@SpringBootApplication
public class CameraServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CameraServiceApplication.class, args);
    }
}

@RestController
@RequestMapping("/camera")
class CameraController {
    @PostMapping("/facial-recognition")
    Map<String, Object> recognizeFace(@RequestBody Map<String, String> request) {
        return Map.of("customerId", "MOCK-123", "customerName", "Cliente Demo", "photoUrl", "/mock/photo.jpg");
    }

    @GetMapping("/evidence/{sessionId}")
    Map<String, Object> getEvidence(@PathVariable String sessionId) {
        return Map.of("photos", java.util.List.of("/evidence/photo1.jpg"), "videos",
                java.util.List.of("/evidence/video1.mp4"));
    }
}
