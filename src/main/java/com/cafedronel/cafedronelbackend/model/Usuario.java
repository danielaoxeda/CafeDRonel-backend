package com.cafedronel.cafedronelbackend.model;

import jakarta.persistence.*;
import java.util.List;
import lombok.Data;   

@Entity
@Table(name = "usuario")
@Data   
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;
    private String correo;
    private String contrasena;
    private String telefono;
    private String direccion;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Pedido> pedidos;
}
