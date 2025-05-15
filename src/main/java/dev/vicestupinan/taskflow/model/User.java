package dev.vicestupinan.taskflow.model;

import org.hibernate.validator.constraints.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @UUID
    private java.util.UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    private String password;
    private String name;
    @Enumerated(EnumType.STRING)
    private Role role;
}
