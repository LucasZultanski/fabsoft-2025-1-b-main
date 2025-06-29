package br.univille.projfabsofttotemmuseum.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.univille.projfabsofttotemmuseum.entity.Evento;
import br.univille.projfabsofttotemmuseum.service.EventoService;

@RestController
@RequestMapping("/api/v1/eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping
    public ResponseEntity<List<Evento>> getEventos() {
        var listaEventos = eventoService.getAllEventos();
        return new ResponseEntity<>(listaEventos, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Evento> postEvento(@RequestBody Evento evento) {
        if (evento == null) {
            return ResponseEntity.badRequest().build();
        }
        if (evento.getId() == 0) {
            eventoService.save(evento);
            return new ResponseEntity<>(evento, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> putEvento(@PathVariable long id, @RequestBody Evento evento) {
        if (id <= 0 || evento == null) {
            return ResponseEntity.badRequest().build();
        }
        var eventoAntigo = eventoService.getEventoById(id);
        if (eventoAntigo == null) {
            return ResponseEntity.notFound().build();
        }
        eventoAntigo.setNome(evento.getNome());
        eventoAntigo.setDataHora(evento.getDataHora());
        eventoAntigo.setLocal(evento.getLocal());
        eventoAntigo.setUsuariosNotificados(evento.getUsuariosNotificados());

        eventoService.save(eventoAntigo);
        return new ResponseEntity<>(eventoAntigo, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Evento> deleteEvento(@PathVariable long id) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        var eventoExcluido = eventoService.getEventoById(id);
        if (eventoExcluido == null) {
            return ResponseEntity.notFound().build();
        }
        eventoService.delete(id);

        return new ResponseEntity<>(eventoExcluido, HttpStatus.OK);
    }
}
