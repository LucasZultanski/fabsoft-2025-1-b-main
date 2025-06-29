package br.univille.projfabsofttotemmuseum.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.univille.projfabsofttotemmuseum.dto.AvaliacaoRequestDTO;
import br.univille.projfabsofttotemmuseum.entity.Avaliacao;
import br.univille.projfabsofttotemmuseum.service.AvaliacaoService;

@RestController
@RequestMapping("/api/v1/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<List<Avaliacao>> getAvaliacoes() {
        var listaAvaliacoes = avaliacaoService.getAllAvaliacoes();
        return new ResponseEntity<>(listaAvaliacoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> getAvaliacaoById(@PathVariable Long id) {
        var avaliacao = avaliacaoService.getAvaliacaoById(id);
        if (avaliacao == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(avaliacao, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> postAvaliacao(@RequestBody AvaliacaoRequestDTO request) {
        if (request.getNota() < 0 || request.getNota() > 10) {
            return new ResponseEntity<>(Map.of("message", "A nota deve ser entre 0 e 10."), HttpStatus.BAD_REQUEST);
        }
        if (request.getUsuarioIdentifier() == null || request.getUsuarioIdentifier().trim().isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Identificador do usuário é obrigatório para avaliação."), HttpStatus.BAD_REQUEST);
        }
        if (request.getDataHora() == null || request.getDataHora().trim().isEmpty()) {
            return new ResponseEntity<>(Map.of("message", "Data e hora da avaliação são obrigatórias."), HttpStatus.BAD_REQUEST);
        }

        try {
            LocalDateTime dataHora = LocalDateTime.parse(request.getDataHora());
            Avaliacao novaAvaliacao = avaliacaoService.saveAvaliacaoFromRequest(request.getNota(), request.getUsuarioIdentifier(), dataHora);
            return new ResponseEntity<>(novaAvaliacao, HttpStatus.CREATED);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>(Map.of("message", "Formato de data e hora inválido. Use ISO 8601."), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Erro ao enviar avaliação: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> putAvaliacao(@PathVariable Long id, @RequestBody Avaliacao avaliacao) {
        if (id <= 0 || avaliacao == null || avaliacao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var avaliacaoExistente = avaliacaoService.getAvaliacaoById(id);
        if (avaliacaoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        avaliacaoExistente.setNota(avaliacao.getNota());
        avaliacaoExistente.setDataHora(avaliacao.getDataHora());
        avaliacaoExistente.setUsuario(avaliacao.getUsuario());

        var avaliacaoAtualizada = avaliacaoService.save(avaliacaoExistente);
        return new ResponseEntity<>(avaliacaoAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Avaliacao> deleteAvaliacao(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var avaliacao = avaliacaoService.getAvaliacaoById(id);
        if (avaliacao == null) {
            return ResponseEntity.notFound().build();
        }
        avaliacaoService.delete(id);
        return new ResponseEntity<>(avaliacao, HttpStatus.OK);
    }
}
