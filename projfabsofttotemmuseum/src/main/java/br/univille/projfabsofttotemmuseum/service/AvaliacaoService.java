package br.univille.projfabsofttotemmuseum.service;

import java.time.LocalDateTime;
import java.util.List;

import br.univille.projfabsofttotemmuseum.entity.Avaliacao;

public interface AvaliacaoService {
    Avaliacao save(Avaliacao avaliacao);
    List<Avaliacao> getAllAvaliacoes();
    Avaliacao getAvaliacaoById(Long id);
    Avaliacao delete(Long id);
    Avaliacao saveAvaliacaoFromRequest(int nota, String usuarioIdentifier, LocalDateTime dataHora);
    double getAverageRatingByPeriod(LocalDateTime startDate, LocalDateTime endDate);
    long getTotalRatingsByPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
