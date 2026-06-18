package com.trainer.usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaResponseDTO {

   private Long id;
   private String usuario;
   private String contrasena;
   private String rol;

}
