package com.alwon.pos.access.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client_types", schema = "access")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "type_code", unique = true, nullable = false, length = 20)
    private String typeCode;

    @Column(name = "type_name", nullable = false, length = 50)
    private String typeName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;

    @Column(name = "requires_identification", nullable = false)
    private Boolean requiresIdentification = false;
}
