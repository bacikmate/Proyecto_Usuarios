package com.trainer.usuarios.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutinaRequestDTO {
    @NotNull(message = "Descripcion puede estar vacio pero no puede ser nulo.")
    private String descripcion;
    @NotNull(message = "Lista de ejercicios puede estar vacia pero no debe ser nula.")
    private List<Long> ejercicios_id;
}
