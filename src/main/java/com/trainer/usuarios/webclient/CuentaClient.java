package com.trainer.usuarios.webclient;

import com.trainer.usuarios.dto.CuentaRequestDTO;
import com.trainer.usuarios.dto.CuentaResponseDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class CuentaClient {

    private final WebClient webClient;

    private final String url = "http://localhost:8081/api/v1/";

    public CuentaClient(){
        this.webClient = WebClient.builder().build();
    }

    public CuentaResponseDTO getCuenta(Long id){
        return webClient.get()
                .uri(url + "cuentas/{id}", id)
                .retrieve()
                .bodyToMono(CuentaResponseDTO.class)
                .block();
    }

    public CuentaResponseDTO crearCuenta (CuentaRequestDTO dto){
        return webClient.post()
                .uri(url + "cuentas")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(CuentaResponseDTO.class)
                .block();
    }

}
