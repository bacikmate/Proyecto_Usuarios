package com.trainer.usuarios.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    @NotBlank(message = "Primer nombre es obligatorio.")
    private String pnombre;
    @NotNull(message = "Segundo nombre no es obligatorio pero no puede ser nulo.")
    private String snombre;
    @NotBlank(message = "Primer apellido es obligatorio.")
    private String appaterno;
    @NotBlank(message = "Segundo apellido es obligatorio.")
    private String apmaterno;
    @NotNull
    private LocalDate fechaNacimiento;

    @NotBlank(message = "Correo es obligatorio")
    @Email(message = "Correo debe estar en formato de correo.")
    private String email;
    @NotBlank(message = "Contraseña es obligatoria.")
    @Size(min = 8, message = "Contraseña debe tener almenos 8 caracteres")
    private String contrasena;
    @NotNull
    private Long tipoCuentaId;

    private Long progresoId;
    private Long coachId;
    private Long planEjercicioId;
    private Long rutinaId;
    private Long planNutricionalId;
}
