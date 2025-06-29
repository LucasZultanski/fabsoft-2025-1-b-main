package br.univille.projfabsofttotemmuseum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.univille.projfabsofttotemmuseum.entity.Exposicao;
import br.univille.projfabsofttotemmuseum.service.ExposicaoService;

@RestController
@RequestMapping("/api/v1/exposicoes")
public class ExposicaoController {

    @Autowired
    private ExposicaoService exposicaoService;

    @GetMapping
    public ResponseEntity<List<Exposicao>> getExposicoes() {
        var listaExposicoes = exposicaoService.getAllExposicoes();
        return new ResponseEntity<>(listaExposicoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exposicao> getExposicaoById(@PathVariable Long id) {
        var exposicao = exposicaoService.getExposicaoById(id);
        if (exposicao == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(exposicao, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Exposicao> postExposicao(@RequestBody Exposicao exposicao) {
        if (exposicao == null || exposicao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var novaExposicao = exposicaoService.save(exposicao);
        return new ResponseEntity<>(novaExposicao, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Exposicao> putExposicao(@PathVariable Long id, @RequestBody Exposicao exposicao) {
        if (id <= 0 || exposicao == null || exposicao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var exposicaoExistente = exposicaoService.getExposicaoById(id);
        if (exposicaoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        exposicaoExistente.setNome(exposicao.getNome());
        exposicaoExistente.setNomeArtista(exposicao.getNomeArtista());
        exposicaoExistente.setDescricao(exposicao.getDescricao());
        exposicaoExistente.setInicioExposicao(exposicao.getInicioExposicao());
        exposicaoExistente.setFimExposicao(exposicao.getFimExposicao());
        exposicaoExistente.setUsuario(exposicao.getUsuario());

        var exposicaoAtualizada = exposicaoService.save(exposicaoExistente);
        return new ResponseEntity<>(exposicaoAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Exposicao> deleteExposicao(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var exposicao = exposicaoService.getExposicaoById(id);
        if (exposicao == null) {
            return ResponseEntity.notFound().build();
        }
        exposicaoService.delete(id);
        return new ResponseEntity<>(exposicao, HttpStatus.OK);
    }
}
