package br.univille.projfabsofttotemmuseum.service;

import java.util.List;

import br.univille.projfabsofttotemmuseum.entity.Notificacao;

public interface NotificacaoService {
    Notificacao save(Notificacao notificacao);
    List<Notificacao> getAllNotificacoes();
    Notificacao getNotificacaoById(Long id);
    Notificacao delete(Long id);
}
