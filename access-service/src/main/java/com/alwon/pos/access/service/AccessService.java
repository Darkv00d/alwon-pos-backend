package com.alwon.pos.access.service;

import com.alwon.pos.access.model.AccessLog;
import com.alwon.pos.access.model.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccessService {

    private static final Map<String, ClientType> CLIENT_TYPES = new HashMap<>();

    static {
        CLIENT_TYPES.put("FACIAL", createClientType("FACIAL", "Cliente Facial",
                "Cliente identificado por reconocimiento facial con ID permanente", "#60a917", true));
        CLIENT_TYPES.put("PIN", createClientType("PIN", "Cliente PIN",
                "Cliente temporal identificado con PIN, datos eliminados tras pago", "#f0a30a", true));
        CLIENT_TYPES.put("NO_ID", createClientType("NO_ID", "No Identificado",
                "Cliente sin identificación, requiere evidencia visual", "#e51400", false));
    }

    private static ClientType createClientType(String code, String name, String desc, String color, boolean reqId) {
        ClientType ct = new ClientType();
        ct.setTypeCode(code);
        ct.setTypeName(name);
        ct.setDescription(desc);
        ct.setColorHex(color);
        ct.setRequiresIdentification(reqId);
        return ct;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> validateAccess(String clientType, String customerId) {
        log.info("Validating access for client type: {} and customer: {}", clientType, customerId);

        Map<String, Object> response = new HashMap<>();
        ClientType type = CLIENT_TYPES.get(clientType);

        if (type == null) {
            response.put("valid", false);
            response.put("message", "Invalid client type");
            return response;
        }

        response.put("valid", true);
        response.put("clientType", type);
        response.put("requiresIdentification", type.getRequiresIdentification());

        if (type.getRequiresIdentification() && (customerId == null || customerId.isEmpty())) {
            response.put("warning", "Identification required for this client type");
        }

        log.info("Access validated successfully for type: {}", clientType);
        return response;
    }

    @Transactional(readOnly = true)
    public Map<String, ClientType> getAllClientTypes() {
        return CLIENT_TYPES;
    }

    @Transactional(readOnly = true)
    public ClientType getClientType(String typeCode) {
        ClientType type = CLIENT_TYPES.get(typeCode);
        if (type == null) {
            throw new RuntimeException("Client type not found: " + typeCode);
        }
        return type;
    }
}
