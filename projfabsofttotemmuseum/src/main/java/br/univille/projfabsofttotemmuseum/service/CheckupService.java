package br.univille.projfabsofttotemmuseum.service;

import java.time.LocalDateTime;
import java.util.List;

import br.univille.projfabsofttotemmuseum.dto.CheckinRequestDTO;
import br.univille.projfabsofttotemmuseum.entity.Checkup;

public interface CheckupService {
    Checkup save(Checkup checkup);
    List<Checkup> getAllCheckups();
    Checkup getCheckupById(Long id);
    Checkup delete(Long id);
    
    // Novo método para salvar checkup a partir do DTO
    Checkup saveCheckupFromRequest(CheckinRequestDTO request);

    // Método para obter total de check-ups por período (ainda como placeholder)
    long getTotalCheckupsByPeriod(LocalDateTime startDate, LocalDateTime endDate);
}
