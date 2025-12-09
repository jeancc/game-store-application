package com.dynacode.store.common;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public class BaseEntity {

    /**
     * Declaramos como string porque keycloak almacena los ID de los usuarios
     * como strings, y como la clase User es la principal de la que salen muchas
     * relaciones, mantenemos todos los ID de las tablas como string para
     * adaptarnos mejor a keycloak
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;


    //auditing attributes
//    private String createdBy;
//    private String lastModifiedBy;
//    private String createdDate;
//    private String lastModifiedDate;
}
