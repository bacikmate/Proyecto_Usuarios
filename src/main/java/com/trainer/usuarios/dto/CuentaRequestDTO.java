package com.trainer.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaRequestDTO {
    @NotNull(message = "El id del dueño no puede ser nulo.")
    private Long duenoId;
    @NotNull(message = "El tipo de cuenta es obligatorio")
    private Long tipoCuentaId;
    @NotBlank(message = "El correo no puede estar vacio")
    @Email(message = "Formato debe ser de correo.")
    private String correoCuenta;
    @NotBlank(message = "La contraseña no puede estar vacia")
    private String contraCuenta;
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaCreacion;
}
