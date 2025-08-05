// src/main/java/com/example/ecommerceJAVA/entity/Usuario.java
package com.example.ecommerceJAVA.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String usuario; // Nombre de usuario para login

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String dni; // DNI del usuario

    @Column(unique = true, nullable = false)
    private String mail;

    private String telefono; // Puede ser nullable

    @Column(nullable = false)
    private String contrasena; // Considera encriptar esto en un entorno real (ej. Spring Security)

    // Si quieres un constructor sin ID para la creación
    public Usuario(String usuario, String nombre, String apellido, String dni, String mail, String telefono, String contrasena) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.mail = mail;
        this.telefono = telefono;
        this.contrasena = contrasena;
    }
}