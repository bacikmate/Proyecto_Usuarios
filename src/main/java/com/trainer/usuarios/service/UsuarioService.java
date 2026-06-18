package com.trainer.usuarios.service;

import com.trainer.usuarios.dto.*;
import com.trainer.usuarios.model.Usuario;
import com.trainer.usuarios.repository.UsuarioRepository;
import com.trainer.usuarios.webclient.CuentaClient;
import com.trainer.usuarios.webclient.RutinaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final CuentaClient cuentaClient;
    private final RutinaClient rutinaClient;

    public UsuarioResponseDTO mapToDTO(Usuario usuario){
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getPnombre(),
                usuario.getSnombre(),
                usuario.getAppaterno(),
                usuario.getApmaterno(),
                usuario.getFechaNacimiento(),
                usuario.getCoachId(),
                usuario.getProgresoID(),
                usuario.getPlanEjercicioId(),
                usuario.getRutinaId(),
                usuario.getPlanNutricionalId());
    }

    public List<UsuarioResponseDTO> findAll(){
        return usuarioRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    public Optional<UsuarioResponseDTO> findById(Long id){
        return usuarioRepository.findById(id).map(this::mapToDTO);
    }

    //AHORA ESTA PASANDO EL TOKEN
    public UsuarioResponseDTO save(UsuarioRequestDTO dto, String token){
        Usuario user = new Usuario(
                null,
                dto.getPnombre(),
                dto.getSnombre(),
                dto.getAppaterno(),
                dto.getApmaterno(),
                dto.getFechaNacimiento(),
                dto.getProgresoId(),
                dto.getCoachId(),
                dto.getPlanEjercicioId(),
                null,
                dto.getPlanNutricionalId());
        RutinaResponseDTO rutinaDTO;
        try{
            rutinaDTO = rutinaClient.crearRutina(new RutinaRequestDTO(
                    "Rutina vacia por defecto.",
                    List.<Long>of()
            ), token);
            user.setRutinaId(rutinaDTO.getId());
        } catch (WebClientResponseException e){
            throw new RuntimeException("Error al crear rutina base, abortando creacion de usuario.");
        }

        UsuarioResponseDTO userDTO = mapToDTO(usuarioRepository.save(user));
        try {
            cuentaClient.crearCuenta(new CuentaRequestDTO(
                    userDTO.getId(),
                    dto.getTipoCuentaId(),
                    dto.getEmail(),
                    dto.getContrasena(),
                    LocalDate.now()
            ));

        } catch (WebClientResponseException e){
            deleteById(userDTO.getId());
            throw new RuntimeException("Error al crear cuenta, abortando creacion de usuario.");
        }
        return userDTO;
    }

    public Optional<UsuarioResponseDTO> update(Long id, UsuarioRequestDTO dto){
        return usuarioRepository.findById(id).map( user -> {
            user.setPnombre(dto.getPnombre());
            user.setSnombre(dto.getSnombre());
            user.setAppaterno(dto.getAppaterno());
            user.setApmaterno(dto.getApmaterno());
            user.setFechaNacimiento(dto.getFechaNacimiento());
            user.setProgresoID(dto.getProgresoId());
            user.setCoachId(dto.getCoachId());
            user.setPlanEjercicioId(dto.getPlanEjercicioId());
            user.setRutinaId(dto.getRutinaId());
            user.setPlanNutricionalId(dto.getPlanNutricionalId());
            return mapToDTO(usuarioRepository.save(user));
        });
    }

    public void deleteById(Long id){
        usuarioRepository.deleteById(id);
    }

}
