package br.univille.projfabsofttotemmuseum.controller;

import br.univille.projfabsofttotemmuseum.entity.Usuario;
import br.univille.projfabsofttotemmuseum.entity.Checkup;
import br.univille.projfabsofttotemmuseum.service.AvaliacaoService;
import br.univille.projfabsofttotemmuseum.service.CheckupService;
import br.univille.projfabsofttotemmuseum.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Autowired
    private CheckupService checkupService;

    @Autowired
    private UsuarioService usuarioService;

    private Map<String, LocalDateTime> getPeriodDates(String period) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        switch (period) {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            case "all":
            default:
                startDate = LocalDateTime.MIN;
                break;
        }
        Map<String, LocalDateTime> dates = new HashMap<>();
        dates.put("start", startDate);
        dates.put("end", endDate);
        return dates;
    }

    @GetMapping("/reports/evaluations")
    public ResponseEntity<?> getEvaluationReports(@RequestParam(defaultValue = "all") String period,
                                                  @RequestParam(required = false) String startDate,
                                                  @RequestParam(required = false) String endDate) {
        LocalDateTime start, end;
        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Formato de data inválido");
            }
        } else {
            Map<String, LocalDateTime> dates = getPeriodDates(period);
            start = dates.get("start");
            end = dates.get("end");
        }
        double avgRating = avaliacaoService.getAverageRatingByPeriod(start, end);
        long totalRatings = avaliacaoService.getTotalRatingsByPeriod(start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", avgRating);
        response.put("totalCount", totalRatings);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reports/checkins")
    public ResponseEntity<?> getCheckinReports(@RequestParam(defaultValue = "all") String period,
                                               @RequestParam(required = false) String startDate,
                                               @RequestParam(required = false) String endDate) {
        LocalDateTime start, end;
        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body("Formato de data inválido");
            }
        } else {
            Map<String, LocalDateTime> dates = getPeriodDates(period);
            start = dates.get("start");
            end = dates.get("end");
        }
        long totalCheckins = checkupService.getTotalCheckupsByPeriod(start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("count", totalCheckins);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/reports/checkins-by-gender")
    public ResponseEntity<Map<String, Long>> getCheckinsByGender(@RequestParam(defaultValue = "month") String period,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate) {
        LocalDateTime start, end;
        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            Map<String, LocalDateTime> dates = getPeriodDates(period);
            start = dates.get("start");
            end = dates.get("end");
        }
        List<Checkup> checkups = checkupService.getAllCheckups().stream()
            .filter(c -> c.getDataHora() != null &&
                        !c.getDataHora().isBefore(start) &&
                        !c.getDataHora().isAfter(end))
            .collect(Collectors.toList());
        Map<String, Long> result = checkups.stream()
            .collect(Collectors.groupingBy(
                c -> c.getUsuario() != null && c.getUsuario().getGenero() != null && !c.getUsuario().getGenero().isEmpty() ? c.getUsuario().getGenero() : "Não informado",
                Collectors.counting()
            ));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/checkins-by-state")
    public ResponseEntity<Map<String, Long>> getCheckinsByState(@RequestParam(defaultValue = "month") String period,
                                                                @RequestParam(required = false) String startDate,
                                                                @RequestParam(required = false) String endDate) {
        LocalDateTime start, end;
        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            Map<String, LocalDateTime> dates = getPeriodDates(period);
            start = dates.get("start");
            end = dates.get("end");
        }
        List<Checkup> checkups = checkupService.getAllCheckups().stream()
            .filter(c -> c.getDataHora() != null &&
                        !c.getDataHora().isBefore(start) &&
                        !c.getDataHora().isAfter(end))
            .collect(Collectors.toList());
        Map<String, Long> result = checkups.stream()
            .collect(Collectors.groupingBy(
                c -> c.getUsuario() != null && c.getUsuario().getEstado() != null && !c.getUsuario().getEstado().isEmpty() ? c.getUsuario().getEstado() : "Não informado",
                Collectors.counting()
            ));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/checkins-by-age-group")
    public ResponseEntity<Map<String, Long>> getCheckinsByAgeGroup(@RequestParam(defaultValue = "month") String period,
                                                                  @RequestParam(required = false) String startDate,
                                                                  @RequestParam(required = false) String endDate) {
        LocalDateTime start, end;
        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            Map<String, LocalDateTime> dates = getPeriodDates(period);
            start = dates.get("start");
            end = dates.get("end");
        }
        List<Checkup> checkups = checkupService.getAllCheckups().stream()
            .filter(c -> c.getDataHora() != null &&
                        !c.getDataHora().isBefore(start) &&
                        !c.getDataHora().isAfter(end))
            .collect(Collectors.toList());
        Map<String, Long> result = checkups.stream()
            .collect(Collectors.groupingBy(
                c -> {
                    Integer idade = c.getUsuario() != null ? c.getUsuario().getIdade() : null;
                    if (idade == null) return "Não informado";
                    if (idade < 12) return "Criança";
                    if (idade < 60) return "Adulto";
                    return "Idoso";
                },
                Collectors.counting()
            ));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/reports/checkins-by-city")
    public ResponseEntity<Map<String, Long>> getCheckinsByCity(@RequestParam(defaultValue = "month") String period,
                                                               @RequestParam(required = false) String startDate,
                                                               @RequestParam(required = false) String endDate) {
        LocalDateTime start, end;
        if (startDate != null && endDate != null) {
            try {
                start = LocalDateTime.parse(startDate);
                end = LocalDateTime.parse(endDate);
            } catch (DateTimeParseException e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            Map<String, LocalDateTime> dates = getPeriodDates(period);
            start = dates.get("start");
            end = dates.get("end");
        }
        List<Checkup> checkups = checkupService.getAllCheckups().stream()
            .filter(c -> c.getDataHora() != null &&
                        !c.getDataHora().isBefore(start) &&
                        !c.getDataHora().isAfter(end))
            .collect(Collectors.toList());
        Map<String, Long> result = checkups.stream()
            .collect(Collectors.groupingBy(
                c -> {
                    String cidade = c.getUsuario() != null ? c.getUsuario().getCidade() : null;
                    String estado = c.getUsuario() != null ? c.getUsuario().getEstado() : null;
                    if (cidade == null || cidade.isEmpty()) return "Não informado";
                    if (estado != null && !estado.isEmpty()) return cidade + " (" + estado + ")";
                    return cidade;
                },
                Collectors.counting()
            ));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Usuario>> getAllUsers(@RequestParam(required = false) String startDate,
                                                    @RequestParam(required = false) String endDate) {
        if (startDate != null && endDate != null) {
            try {
                LocalDateTime start = LocalDateTime.parse(startDate);
                LocalDateTime end = LocalDateTime.parse(endDate);
                List<Usuario> users = usuarioService.getUsuariosComCheckinNoPeriodo(start, end);
                return new ResponseEntity<>(users, HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            List<Usuario> users = usuarioService.getAllUsuarios();
            return new ResponseEntity<>(users, HttpStatus.OK);
        }
    }

    @GetMapping("/export-powerbi")
    public ResponseEntity<?> exportDataForPowerBI(@RequestParam(defaultValue = "all") String period) {
        System.out.println("Simulando exportação de dados para Power BI para o período: " + period);
        return new ResponseEntity<>(Map.of("message", "Dados simuladamente exportados para Power BI para o período " + period), HttpStatus.OK);
    }
}
