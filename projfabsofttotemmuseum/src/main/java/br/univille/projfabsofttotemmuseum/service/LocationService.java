package br.univille.projfabsofttotemmuseum.service;

import br.univille.projfabsofttotemmuseum.dto.IbgeCityDTO;
import br.univille.projfabsofttotemmuseum.dto.IbgeStateDTO;
import java.util.List;

public interface LocationService {
    List<IbgeStateDTO> getStates();
    List<IbgeCityDTO> getCitiesByState(String uf);
}
