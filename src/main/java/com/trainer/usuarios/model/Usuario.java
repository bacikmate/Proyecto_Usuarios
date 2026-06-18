package com.trainer.usuarios.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String pnombre;
    @Column(nullable = true)
    private String snombre;
    @Column(nullable = false)
    private String appaterno;
    @Column(nullable = false)
    private String apmaterno;
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = true)
    private Long progresoID;
    @Column(nullable = true)
    private Long coachId;
    @Column(nullable = true)
    private Long planEjercicioId;
    @Column(nullable = false)
    private Long rutinaId;
    @Column(nullable = true)
    private Long planNutricionalId;



}
