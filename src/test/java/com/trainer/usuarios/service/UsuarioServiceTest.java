package com.trainer.usuarios.service;

import com.trainer.usuarios.dto.*;
import com.trainer.usuarios.model.Usuario;
import com.trainer.usuarios.repository.UsuarioRepository;
import com.trainer.usuarios.webclient.CuentaClient;
import com.trainer.usuarios.webclient.RutinaClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CuentaClient cuentaClient;

    @Mock
    private RutinaClient rutinaClient;

    @InjectMocks
    private UsuarioService usuarioService;

    private final String TOKEN = "Bearer fake-token";

    private WebClientResponseException webClientError(int statusCode) {
        return WebClientResponseException.create(statusCode, "Error", null, null, null);
    }

    // ============================================================
    //  findAll()
    // ============================================================

    @Test
    void findAll_retornaListaDeUsuariosMapeados() {
        Usuario usuario1 = new Usuario(1L, "Juan", "", "Perez", "Soto",
                LocalDate.of(2000, 1, 1), null, null, null, 1L, null);
        Usuario usuario2 = new Usuario(2L, "Maria", "", "Gonzalez", "Rojas",
                LocalDate.of(1998, 5, 5), null, null, null, 2L, null);

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario1, usuario2));

        List<UsuarioResponseDTO> resultado = usuarioService.findAll();

        assertEquals(2, resultado.size());
        assertEquals("Juan", resultado.get(0).getPnombre());
        assertEquals("Maria", resultado.get(1).getPnombre());
    }

    @Test
    void findAll_cuandoNoHayUsuarios_retornaListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        List<UsuarioResponseDTO> resultado = usuarioService.findAll();

        assertTrue(resultado.isEmpty());
    }

    // ============================================================
    //  findById()
    // ============================================================

    @Test
    void findById_cuandoExiste_retornaOptionalConElDTO() {
        Usuario usuario = new Usuario(1L, "Juan", "", "Perez", "Soto",
                LocalDate.of(2000, 1, 1), null, null, null, 1L, null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioResponseDTO> resultado = usuarioService.findById(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getPnombre());
    }

    @Test
    void findById_cuandoNoExiste_retornaOptionalVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.findById(99L);

        assertTrue(resultado.isEmpty());
    }

    // ============================================================
    //  save() — ESCENARIO 1:
    // ============================================================

    @Test
    void save_cuandoTodoFunciona_creaUsuarioConRutinaYCuenta() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setPnombre("Juan");
        dto.setSnombre("");
        dto.setAppaterno("Perez");
        dto.setApmaterno("Soto");
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setEmail("juan@gmail.com");
        dto.setContrasena("12345678");
        dto.setTipoCuentaId(2L);

        RutinaResponseDTO rutinaCreada = new RutinaResponseDTO(50L, "Rutina vacia por defecto.", List.of());
        when(rutinaClient.crearRutina(any(RutinaRequestDTO.class), eq(TOKEN)))
                .thenReturn(rutinaCreada);

        Usuario usuarioGuardado = new Usuario(1L, "Juan", "", "Perez", "Soto",
                LocalDate.of(2000, 1, 1), null, null, null, 50L, null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        CuentaResponseDTO cuentaCreada = new CuentaResponseDTO(1L, "juan@gmail.com", "hash", "USER");
        when(cuentaClient.crearCuenta(any(CuentaRequestDTO.class))).thenReturn(cuentaCreada);

        UsuarioResponseDTO resultado = usuarioService.save(dto, TOKEN);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan", resultado.getPnombre());
        assertEquals(50L, resultado.getRutinaId());

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(cuentaClient, times(1)).crearCuenta(any(CuentaRequestDTO.class));
        verify(usuarioRepository, never()).deleteById(any());
    }

    // ============================================================
    //  save() — ESCENARIO 2: falla creacion de rutina
    // ============================================================

    @Test
    void save_cuandoFallaCreacionDeRutina_lanzaExcepcionYNuncaGuardaUsuario() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setPnombre("Juan");
        dto.setEmail("juan@gmail.com");
        dto.setContrasena("12345678");
        dto.setTipoCuentaId(2L);
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setAppaterno("Perez");
        dto.setApmaterno("Soto");

        when(rutinaClient.crearRutina(any(RutinaRequestDTO.class), eq(TOKEN)))
                .thenThrow(webClientError(500));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.save(dto, TOKEN)
        );
        assertTrue(exception.getMessage().contains("rutina"));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(cuentaClient, never()).crearCuenta(any(CuentaRequestDTO.class));
    }

    // ============================================================
    //  save() — ESCENARIO 3: falla creacion de cuenta
    // ============================================================

    @Test
    void save_cuandoFallaCreacionDeCuenta_haceRollbackDelUsuarioCreado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setPnombre("Juan");
        dto.setEmail("juan@gmail.com");
        dto.setContrasena("12345678");
        dto.setTipoCuentaId(2L);
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setAppaterno("Perez");
        dto.setApmaterno("Soto");

        RutinaResponseDTO rutinaCreada = new RutinaResponseDTO(50L, "Rutina vacia por defecto.", List.of());
        when(rutinaClient.crearRutina(any(RutinaRequestDTO.class), eq(TOKEN)))
                .thenReturn(rutinaCreada);

        Usuario usuarioGuardado = new Usuario(1L, "Juan", "", "Perez", "Soto",
                LocalDate.of(2000, 1, 1), null, null, null, 50L, null);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        when(cuentaClient.crearCuenta(any(CuentaRequestDTO.class)))
                .thenThrow(webClientError(400));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> usuarioService.save(dto, TOKEN)
        );
        assertTrue(exception.getMessage().contains("cuenta"));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    // ============================================================
    //  update()
    // ============================================================

    @Test
    void update_cuandoUsuarioExiste_actualizaYRetornaDTO() {
        Usuario usuarioExistente = new Usuario(1L, "Juan", "", "Perez", "Soto",
                LocalDate.of(2000, 1, 1), null, null, null, 1L, null);

        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setPnombre("Juan Actualizado");
        dto.setSnombre("");
        dto.setAppaterno("Perez");
        dto.setApmaterno("Soto");
        dto.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        dto.setRutinaId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Optional<UsuarioResponseDTO> resultado = usuarioService.update(1L, dto);

        assertTrue(resultado.isPresent());
        assertEquals("Juan Actualizado", resultado.get().getPnombre());
    }

    @Test
    void update_cuandoUsuarioNoExiste_retornaOptionalVacio() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioResponseDTO> resultado = usuarioService.update(99L, dto);

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ============================================================
    //  deleteById()
    // ============================================================

    @Test
    void deleteById_llamaAlRepositoryConElIdCorrecto() {
        usuarioService.deleteById(3L);

        verify(usuarioRepository, times(1)).deleteById(3L);
    }
}