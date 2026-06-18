package com.trainer.usuarios.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String pnombre;
    private String snombre;
    private String appaterno;
    private String apmaterno;
    private LocalDate fechaNacimiento;
    private Long progresoId;
    private Long coachId;
    private Long planEjercicioId;
    private Long rutinaId;
    private Long planNutricionalId;
}
