package com.trainer.usuarios.config;

import com.trainer.usuarios.model.Usuario;
import com.trainer.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args){
        if (usuarioRepository.count() > 0) {
            log.info(">>> DataInitializer: la BD ya tiene datos, se omite la carga inicial.");
            return;
        }
        log.info(">>> DataInitializer: BD vacía, insertando datos...");

        Usuario user1 = new Usuario(
                null,
                "primer_nombre",
                "",
                "primer_apellido",
                "segundo_apellido",
                LocalDate.of(2000, 1, 1),
                null,
                null,
                null,
                1L,
                null
        );

        usuarioRepository.save(user1);

        Usuario user2 = new Usuario(
                null,
                "juan",
                "juanes",
                "peres",
                "paredes",
                LocalDate.of(2000, 6, 6),
                null,
                null,
                null,
                2L,
                null
        );

        usuarioRepository.save(user2);
    }
}
