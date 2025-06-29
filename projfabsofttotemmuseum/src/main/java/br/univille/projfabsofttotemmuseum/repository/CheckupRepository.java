package br.univille.projfabsofttotemmuseum.repository;

import br.univille.projfabsofttotemmuseum.entity.Checkup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CheckupRepository extends JpaRepository<Checkup, Long> {
    List<Checkup> findByDataHoraBetween(LocalDateTime start, LocalDateTime end);
}
