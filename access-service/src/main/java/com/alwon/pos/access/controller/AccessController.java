package com.alwon.pos.access.controller;

import com.alwon.pos.access.model.ClientType;
import com.alwon.pos.access.service.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/access")
@RequiredArgsConstructor
@Tag(name = "Access Control", description = "Access control and client type validation")
public class AccessController {

    private final AccessService accessService;

    @GetMapping("/validate")
    @Operation(summary = "Validate client access")
    public ResponseEntity<Map<String, Object>> validateAccess(
            @RequestParam String clientType,
            @RequestParam(required = false) String customerId) {
        return ResponseEntity.ok(accessService.validateAccess(clientType, customerId));
    }

    @GetMapping("/client-types")
    @Operation(summary = "Get all client types")
    public ResponseEntity<Map<String, ClientType>> getAllClientTypes() {
        return ResponseEntity.ok(accessService.getAllClientTypes());
    }

    @GetMapping("/client-types/{typeCode}")
    @Operation(summary = "Get client type by code")
    public ResponseEntity<ClientType> getClientType(@PathVariable String typeCode) {
        return ResponseEntity.ok(accessService.getClientType(typeCode));
    }
}
