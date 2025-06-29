package br.univille.projfabsofttotemmuseum.service;

import java.util.List;

import br.univille.projfabsofttotemmuseum.entity.Evento;

public interface EventoService {
    Evento save(Evento evento);
    List<Evento> getAllEventos();
    Evento getEventoById(Long id);
    Evento delete(Long id);
}
