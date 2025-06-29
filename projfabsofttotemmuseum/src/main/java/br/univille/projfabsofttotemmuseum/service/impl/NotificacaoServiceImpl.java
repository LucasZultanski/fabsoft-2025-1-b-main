package br.univille.projfabsofttotemmuseum.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.univille.projfabsofttotemmuseum.entity.Notificacao;
import br.univille.projfabsofttotemmuseum.repository.NotificacaoRepository;
import br.univille.projfabsofttotemmuseum.service.NotificacaoService;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    @Autowired
    private NotificacaoRepository repository;

    @Override
    public Notificacao save(Notificacao notificacao) {
        repository.save(notificacao);
        return notificacao;
    }

    @Override
    public List<Notificacao> getAllNotificacoes() {
        return repository.findAll();
    }

    @Override
    public Notificacao getNotificacaoById(Long id) {
        Optional<Notificacao> notificacao = repository.findById(id);
        return notificacao.orElse(null);
    }

    @Override
    public Notificacao delete(Long id) {
        Optional<Notificacao> notificacao = repository.findById(id);
        if (notificacao.isPresent()) {
            repository.delete(notificacao.get());
            return notificacao.get();
        }
        return null;
    }
}
