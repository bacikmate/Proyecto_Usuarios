package com.trainer.usuarios.controller;

import com.trainer.usuarios.dto.UsuarioRequestDTO;
import com.trainer.usuarios.dto.UsuarioResponseDTO;
import com.trainer.usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Usuarios", description = "API REST para gestión de datos de usuarios")
@SecurityRequirement(name = "Bearer Token")
@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Operation(summary = "Obtener todos los usuarios", description = "Solo accesible por ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getAll() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAll();
        if (usuarios.isEmpty()) {
            log.warn("No se ha encontrado ningun usuario.");
        } else {
            log.info("Se han encontado {} usuarios.", usuarios.size());
        }
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Accesible por ADMIN y USER")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> getById(@PathVariable Long id) {
        Optional<UsuarioResponseDTO> usuario = usuarioService.findById(id);
        if (usuario.isPresent()) {
            log.info("Se ha encontrado el usuario con id {}", id);
            return ResponseEntity.ok(usuario.get());
        } else {
            log.error("No se ha encontrado el usuario con id {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear usuario", description = "Endpoint público. Crea el usuario junto a su rutina base y cuenta asociada")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o error al crear rutina/cuenta")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> save(@Valid @RequestBody UsuarioRequestDTO dto, HttpServletRequest request) {
        UsuarioResponseDTO usuario = usuarioService.save(dto, request.getHeader("Authorization"));
        log.info("Se ha creado el usuario con el id {}", usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @Operation(summary = "Actualizar usuario", description = "Accesible por ADMIN y USER")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        Optional<UsuarioResponseDTO> usuario = usuarioService.update(id, dto);
        if (usuario.isPresent()) {
            log.info("Se ha modificado el usuario con id {}", id);
            return ResponseEntity.ok(usuario.get());
        } else {
            log.error("No se ha encontrado el usuario con id {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Solo accesible por ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "Sin permisos")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (usuarioService.findById(id).isEmpty()) {
            log.error("No se ha encontrado el usuario con id {} al tratar de eliminar.", id);
            return ResponseEntity.notFound().build();
        }
        usuarioService.deleteById(id);
        log.info("Se ha eliminado el usuario con id {}", id);
        return ResponseEntity.noContent().build();
    }
}