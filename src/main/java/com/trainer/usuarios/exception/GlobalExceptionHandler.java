package com.trainer.usuarios.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidateErrors(MethodArgumentNotValidException ex){
        Map<String,String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /*@ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleUniqueErrors(DataIntegrityViolationException ex){
        Throwable root = ex.getCause();
        Map<String,String> errors = new LinkedHashMap<>();
        while (root != null) {
            if (root instanceof org.hibernate.exception.ConstraintViolationException constraintEx) {
                String constraintName = constraintEx.getConstraintName();
                if("uk_nombreTipoEjercicio".equalsIgnoreCase(constraintName)){
                    errors.put("restriccion", "nombreTipoEjercicio debe ser unico.");
                }
                break;
            }
            root = root.getCause();
        }
        if(errors.isEmpty()){
            errors.put("error", "Hubo un error al manejar excepciones de restricciones.");
        }
        return ResponseEntity.badRequest().body(errors);
    }*/

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntimeException( RuntimeException ex){
        Map<String,String> error = new LinkedHashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
