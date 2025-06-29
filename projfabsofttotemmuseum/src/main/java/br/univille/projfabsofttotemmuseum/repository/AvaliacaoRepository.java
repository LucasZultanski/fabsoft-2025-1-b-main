package br.univille.projfabsofttotemmuseum.repository;

import br.univille.projfabsofttotemmuseum.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    List<Avaliacao> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);
}
