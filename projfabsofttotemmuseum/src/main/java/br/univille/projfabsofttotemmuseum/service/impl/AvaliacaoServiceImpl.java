package br.univille.projfabsofttotemmuseum.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.univille.projfabsofttotemmuseum.entity.Avaliacao;
import br.univille.projfabsofttotemmuseum.entity.Usuario;
import br.univille.projfabsofttotemmuseum.repository.AvaliacaoRepository;
import br.univille.projfabsofttotemmuseum.service.AvaliacaoService;
import br.univille.projfabsofttotemmuseum.service.UsuarioService;

@Service
public class AvaliacaoServiceImpl implements AvaliacaoService {

    @Autowired
    private AvaliacaoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public Avaliacao save(Avaliacao avaliacao) {
        if (avaliacao.getDataHora() == null) {
            avaliacao.setDataHora(LocalDateTime.now());
        }
        return repository.save(avaliacao);
    }

    @Override
    public Avaliacao saveAvaliacaoFromRequest(int nota, String usuarioIdentifier, LocalDateTime dataHora) {
        Usuario usuario = usuarioService.findOrCreateUserByIdentifier(usuarioIdentifier);

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(nota);
        avaliacao.setDataHora(dataHora);
        avaliacao.setUsuario(usuario);

        return repository.save(avaliacao);
    }

    @Override
    public List<Avaliacao> getAllAvaliacoes() {
        return repository.findAll();
    }

    @Override
    public Avaliacao getAvaliacaoById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Avaliacao delete(Long id) {
        Optional<Avaliacao> avaliacao = repository.findById(id);
        if (avaliacao.isPresent()) {
            repository.delete(avaliacao.get());
            return avaliacao.get();
        }
        return null;
    }

    @Override
    public double getAverageRatingByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Avaliacao> avaliacoes = repository.findByDataHoraBetween(startDate, endDate);
        OptionalDouble average = avaliacoes.stream()
                                            .mapToInt(Avaliacao::getNota)
                                            .average();
        return average.orElse(0.0);
    }

    @Override
    public long getTotalRatingsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByDataHoraBetween(startDate, endDate).size();
    }
}
