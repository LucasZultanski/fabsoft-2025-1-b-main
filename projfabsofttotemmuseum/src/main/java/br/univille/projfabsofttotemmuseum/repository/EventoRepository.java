package br.univille.projfabsofttotemmuseum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.univille.projfabsofttotemmuseum.entity.Evento;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
}
