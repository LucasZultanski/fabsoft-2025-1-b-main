package br.univille.projfabsofttotemmuseum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.univille.projfabsofttotemmuseum.entity.Exposicao;

@Repository
public interface ExposicaoRepository extends JpaRepository<Exposicao, Long> {
}
