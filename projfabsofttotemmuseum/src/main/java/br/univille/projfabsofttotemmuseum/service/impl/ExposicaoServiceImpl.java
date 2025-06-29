package br.univille.projfabsofttotemmuseum.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.univille.projfabsofttotemmuseum.entity.Exposicao;
import br.univille.projfabsofttotemmuseum.repository.ExposicaoRepository;
import br.univille.projfabsofttotemmuseum.service.ExposicaoService;

@Service
public class ExposicaoServiceImpl implements ExposicaoService {

    @Autowired
    private ExposicaoRepository repository;

    @Override
    public Exposicao save(Exposicao exposicao) {
        repository.save(exposicao);
        return exposicao;
    }

    @Override
    public List<Exposicao> getAllExposicoes() {
        return repository.findAll();
    }

    @Override
    public Exposicao getExposicaoById(Long id) {
        Optional<Exposicao> exposicao = repository.findById(id);
        return exposicao.orElse(null);
    }

    @Override
    public Exposicao delete(Long id) {
        Optional<Exposicao> exposicao = repository.findById(id);
        if (exposicao.isPresent()) {
            repository.delete(exposicao.get());
            return exposicao.get();
        }
        return null;
    }
}
