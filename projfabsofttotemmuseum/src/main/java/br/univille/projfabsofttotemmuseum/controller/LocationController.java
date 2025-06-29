package br.univille.projfabsofttotemmuseum.controller;

import br.univille.projfabsofttotemmuseum.dto.IbgeCityDTO;
import br.univille.projfabsofttotemmuseum.dto.IbgeStateDTO;
import br.univille.projfabsofttotemmuseum.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    @Autowired
    private LocationService locationService;

    @GetMapping("/states")
    public ResponseEntity<List<IbgeStateDTO>> getStates() {
        List<IbgeStateDTO> states = locationService.getStates();
        return new ResponseEntity<>(states, HttpStatus.OK);
    }

    @GetMapping("/states/{uf}/cities")
    public ResponseEntity<List<IbgeCityDTO>> getCitiesByState(@PathVariable String uf) {
        List<IbgeCityDTO> cities = locationService.getCitiesByState(uf);
        return new ResponseEntity<>(cities, HttpStatus.OK);
    }
}
