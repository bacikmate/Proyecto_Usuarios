package com.trainer.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RutinaResponseDTO {
    private Long id;
    private String descripcion;
    private List<String> ejercicios;
}
