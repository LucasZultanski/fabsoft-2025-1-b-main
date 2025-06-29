package br.univille.projfabsofttotemmuseum.service.impl;

import br.univille.projfabsofttotemmuseum.dto.IbgeCityDTO;
import br.univille.projfabsofttotemmuseum.dto.IbgeStateDTO;
import br.univille.projfabsofttotemmuseum.service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {

    private final RestTemplate restTemplate;
    private static final String IBGE_API_BASE_URL = "https://servicodados.ibge.gov.br/api/v1/localidades";

    public LocationServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<IbgeStateDTO> getStates() {
        String url = IBGE_API_BASE_URL + "/estados?orderBy=nome";
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<IbgeStateDTO>>() {})
                .getBody();
    }

    @Override
    public List<IbgeCityDTO> getCitiesByState(String uf) {
        String url = IBGE_API_BASE_URL + "/estados/" + uf + "/municipios?orderBy=nome";
        return restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<IbgeCityDTO>>() {})
                .getBody();
    }
}
