package br.univille.projfabsofttotemmuseum.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.MethodArgumentNotValidException;

import br.univille.projfabsofttotemmuseum.entity.Checkup;
import br.univille.projfabsofttotemmuseum.dto.CheckinRequestDTO; // Importar o DTO
import br.univille.projfabsofttotemmuseum.service.CheckupService;

@RestController
@RequestMapping("/api/v1/checkups")
public class CheckupController {

    @Autowired
    private CheckupService checkupService;

    @GetMapping
    public ResponseEntity<List<Checkup>> getCheckups() {
        var listaCheckups = checkupService.getAllCheckups();
        return new ResponseEntity<>(listaCheckups, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Checkup> getCheckupById(@PathVariable Long id) {
        var checkup = checkupService.getCheckupById(id);
        if (checkup == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(checkup, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postCheckup(@Valid @RequestBody CheckinRequestDTO checkinRequestDTO) {
        // Validações básicas do DTO agora são feitas pelo Bean Validation
        var novoCheckup = checkupService.saveCheckupFromRequest(checkinRequestDTO);
        return new ResponseEntity<>(novoCheckup, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Checkup> putCheckup(@PathVariable Long id, @RequestBody Checkup checkup) {
        if (id <= 0 || checkup == null || checkup.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var checkupExistente = checkupService.getCheckupById(id);
        if (checkupExistente == null) {
            return ResponseEntity.notFound().build();
        }
        checkupExistente.setDataHora(checkup.getDataHora());
        checkupExistente.setLocal(checkup.getLocal());
        checkupExistente.setUsuario(checkup.getUsuario()); // Mantém o usuário existente, se aplicável

        var checkupAtualizado = checkupService.save(checkupExistente); // Usa o save original
        return new ResponseEntity<>(checkupAtualizado, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Checkup> deleteCheckup(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var checkup = checkupService.getCheckupById(id);
        if (checkup == null) {
            return ResponseEntity.notFound().build();
        }
        checkupService.delete(id);
        return new ResponseEntity<>(checkup, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            sb.append(error.getDefaultMessage()).append("\n");
        });
        return ResponseEntity.badRequest().body(Map.of("message", sb.toString().trim()));
    }
}
