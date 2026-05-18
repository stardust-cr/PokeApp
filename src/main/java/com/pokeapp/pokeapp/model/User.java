package com.pokeapp.pokeapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_usuario", unique = true)
    private String username;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "contrasena_hash")
    private String password;

    @Lob
    @Column(name = "avatar")
    private byte[] profileImage;

    @Column(name = "rol")
    private String rol = "USER";  // USER o ADMIN

    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public byte[] getProfileImage() { return profileImage; }
    public void setProfileImage(byte[] profileImage) { this.profileImage = profileImage; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public boolean isAdmin() { return "ADMIN".equals(rol); }
}