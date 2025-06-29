package br.univille.projfabsofttotemmuseum.service;

import java.util.List;

import br.univille.projfabsofttotemmuseum.entity.Exposicao;

public interface ExposicaoService {
    Exposicao save(Exposicao exposicao);
    List<Exposicao> getAllExposicoes();
    Exposicao getExposicaoById(Long id);
    Exposicao delete(Long id);
}
