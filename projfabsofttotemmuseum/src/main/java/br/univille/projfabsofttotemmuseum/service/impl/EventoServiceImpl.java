package br.univille.projfabsofttotemmuseum.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.univille.projfabsofttotemmuseum.entity.Evento;
import br.univille.projfabsofttotemmuseum.repository.EventoRepository;
import br.univille.projfabsofttotemmuseum.service.EventoService;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private EventoRepository repository;

    @Override
    public Evento save(Evento evento) {
        repository.save(evento);
        return evento;
    }

    @Override
    public List<Evento> getAllEventos() {
        return repository.findAll();
    }

    @Override
    public Evento getEventoById(Long id) {
        Optional<Evento> evento = repository.findById(id);
        return evento.orElse(null);
    }

    @Override
    public Evento delete(Long id) {
        Optional<Evento> evento = repository.findById(id);
        if (evento.isPresent()) {
            repository.delete(evento.get());
            return evento.get();
        }
        return null;
    }
}
