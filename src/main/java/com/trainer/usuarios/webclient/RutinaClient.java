package com.trainer.usuarios.webclient;

import com.trainer.usuarios.dto.CuentaRequestDTO;
import com.trainer.usuarios.dto.CuentaResponseDTO;
import com.trainer.usuarios.dto.RutinaRequestDTO;
import com.trainer.usuarios.dto.RutinaResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RutinaClient {
    private final WebClient webClient;

    private final String url = "http://localhost:8086/api/v1/rutinas";

    public RutinaClient(){
        this.webClient = WebClient.builder().build();
    }

    //FALTO PASAR EL TOKEN PARA PODER LLAMAR LOS A RUTINA POR TEMAS DE SPRING SECURITY ESTO NO ESTA EN EL REPO String token
    public RutinaResponseDTO crearRutina (RutinaRequestDTO dto, String token){
        return webClient.post()
                .uri(url)
                .header("Authorization", token)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(RutinaResponseDTO.class)
                .block();
    }
}
