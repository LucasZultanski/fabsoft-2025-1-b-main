package br.univille.projfabsofttotemmuseum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.univille.projfabsofttotemmuseum.entity.Notificacao;
import br.univille.projfabsofttotemmuseum.service.NotificacaoService;

@RestController
@RequestMapping("/api/v1/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping
    public ResponseEntity<List<Notificacao>> getNotificacoes() {
        var listaNotificacoes = notificacaoService.getAllNotificacoes();
        return new ResponseEntity<>(listaNotificacoes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> getNotificacaoById(@PathVariable Long id) {
        var notificacao = notificacaoService.getNotificacaoById(id);
        if (notificacao == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(notificacao, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Notificacao> postNotificacao(@RequestBody Notificacao notificacao) {
        if (notificacao == null || notificacao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var novaNotificacao = notificacaoService.save(notificacao);
        return new ResponseEntity<>(novaNotificacao, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notificacao> putNotificacao(@PathVariable Long id, @RequestBody Notificacao notificacao) {
        if (id <= 0 || notificacao == null || notificacao.getUsuario() == null) {
            return ResponseEntity.badRequest().build();
        }
        var notificacaoExistente = notificacaoService.getNotificacaoById(id);
        if (notificacaoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        notificacaoExistente.setDataHora(notificacao.getDataHora());
        notificacaoExistente.setDescricao(notificacao.getDescricao());
        notificacaoExistente.setTipo(notificacao.getTipo());
        notificacaoExistente.setUsuario(notificacao.getUsuario());
        notificacaoExistente.setEvento(notificacao.getEvento());
        notificacaoExistente.setExposicao(notificacao.getExposicao());

        var notificacaoAtualizada = notificacaoService.save(notificacaoExistente);
        return new ResponseEntity<>(notificacaoAtualizada, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Notificacao> deleteNotificacao(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        var notificacao = notificacaoService.getNotificacaoById(id);
        if (notificacao == null) {
            return ResponseEntity.notFound().build();
        }
        notificacaoService.delete(id);
        return new ResponseEntity<>(notificacao, HttpStatus.OK);
    }
}
