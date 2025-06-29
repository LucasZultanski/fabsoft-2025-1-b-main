package br.univille.projfabsofttotemmuseum.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.univille.projfabsofttotemmuseum.dto.CheckinRequestDTO;
import br.univille.projfabsofttotemmuseum.entity.Checkup;
import br.univille.projfabsofttotemmuseum.entity.Usuario;
import br.univille.projfabsofttotemmuseum.repository.CheckupRepository;
import br.univille.projfabsofttotemmuseum.repository.UsuarioRepository; // Importe o repositório de usuário
import br.univille.projfabsofttotemmuseum.service.CheckupService;

@Service
public class CheckupServiceImpl implements CheckupService {

    @Autowired
    private CheckupRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Injetar o repositório de usuário

    @Override
    public Checkup save(Checkup checkup) {
        // Antes de salvar o checkup, precisamos garantir que o usuário associado existe.
        // O frontend envia um 'usuarioIdentifier' (email ou telefone).
        Usuario usuario = null;
        if (checkup.getUsuario() != null) {
            if (checkup.getUsuario().getEmail() != null && !checkup.getUsuario().getEmail().isEmpty()) {
                usuario = usuarioRepository.findByEmail(checkup.getUsuario().getEmail());
            } else if (checkup.getUsuario().getTelefone() != null && !checkup.getUsuario().getTelefone().isEmpty()) {
                usuario = usuarioRepository.findByTelefone(checkup.getUsuario().getTelefone()).orElse(null);
            }
        }

        // Se o usuário não existir, criamos um novo usuário com os dados fornecidos.
        if (usuario == null && checkup.getUsuario() != null) {
            usuario = new Usuario();
            if (checkup.getUsuario().getEmail() != null && !checkup.getUsuario().getEmail().isEmpty()) {
                usuario.setEmail(checkup.getUsuario().getEmail());
            }
            if (checkup.getUsuario().getTelefone() != null && !checkup.getUsuario().getTelefone().isEmpty()) {
                usuario.setTelefone(checkup.getUsuario().getTelefone());
            }
            usuario.setNome(checkup.getUsuario().getNome());
            usuario.setIdade(checkup.getUsuario().getIdade());
            usuario.setGenero(checkup.getUsuario().getGenero());
            usuario.setEstado(checkup.getUsuario().getEstado());
            usuario.setCidade(checkup.getUsuario().getCidade());
            usuarioRepository.save(usuario); // Salva o novo usuário
        } else if (usuario != null && checkup.getUsuario() != null) {
            // Atualiza os dados do usuário existente, se fornecidos
            if (checkup.getUsuario().getNome() != null && !checkup.getUsuario().getNome().isEmpty()) {
                usuario.setNome(checkup.getUsuario().getNome());
            }
            if (checkup.getUsuario().getIdade() != null) {
                usuario.setIdade(checkup.getUsuario().getIdade());
            }
            if (checkup.getUsuario().getGenero() != null && !checkup.getUsuario().getGenero().isEmpty()) {
                usuario.setGenero(checkup.getUsuario().getGenero());
            }
            if (checkup.getUsuario().getEstado() != null && !checkup.getUsuario().getEstado().isEmpty()) {
                usuario.setEstado(checkup.getUsuario().getEstado());
            }
            if (checkup.getUsuario().getCidade() != null && !checkup.getUsuario().getCidade().isEmpty()) {
                usuario.setCidade(checkup.getUsuario().getCidade());
            }
            usuarioRepository.save(usuario); // Atualiza o usuário existente
        }
        checkup.setUsuario(usuario); // Associa o usuário ao checkup

        // Define a data e hora atual caso não esteja definida no objeto checkup
        // ou parseia a string ISO que vem do frontend.
        if (checkup.getDataHora() == null) {
            checkup.setDataHora(LocalDateTime.now());
        } else if (checkup.getDataHora().toString().contains("T") && checkup.getDataHora().toString().endsWith("Z")) {
            // Tenta parsear a string ISO de forma mais robusta se contiver 'T' e 'Z'
            // O Jackson já deveria lidar com ISO, mas esta é uma camada de segurança.
            try {
                // Remove o 'Z' para LocalDateTime, pois ele não lida diretamente com offsets
                // A data já vem no formato 'YYYY-MM-DDTHH:mm:ss.SSSZ' do frontend
                String isoDateTime = checkup.getDataHora().toString();
                if (isoDateTime.endsWith("Z")) {
                    isoDateTime = isoDateTime.substring(0, isoDateTime.length() - 1);
                }
                checkup.setDataHora(LocalDateTime.parse(isoDateTime));
            } catch (Exception e) {
                System.err.println("Erro ao parsear LocalDateTime com 'Z': " + e.getMessage());
                // Fallback para LocalDateTime.now() ou log de erro
                checkup.setDataHora(LocalDateTime.now());
            }
        }
        
        return repository.save(checkup);
    }

    @Override
    public List<Checkup> getAllCheckups() {
        return repository.findAll();
    }

    @Override
    public Checkup getCheckupById(Long id) {
        Optional<Checkup> checkup = repository.findById(id);
        return checkup.orElse(null);
    }

    @Override
    public Checkup delete(Long id) {
        Optional<Checkup> checkup = repository.findById(id);
        if (checkup.isPresent()) {
            repository.delete(checkup.get());
            return checkup.get();
        }
        return null;
    }

    @Override
    public Checkup saveCheckupFromRequest(CheckinRequestDTO request) {
        // Identifica o usuário pelo identificador (email ou telefone)
        Usuario usuario = null;
        String identifier = request.getUsuarioIdentifier();
        if (identifier != null && !identifier.isEmpty()) {
            if (identifier.contains("@")) {
                usuario = usuarioRepository.findByEmail(identifier);
            } else {
                usuario = usuarioRepository.findByTelefone(identifier).orElse(null);
            }
        }

        // Se não existir, cria novo usuário
        if (usuario == null) {
            usuario = new Usuario();
            if (identifier != null && identifier.contains("@")) {
                usuario.setEmail(identifier);
            } else {
                usuario.setTelefone(identifier);
            }
        }

        // Atualiza dados do usuário com informações do DTO
        if (request.getNome() != null) usuario.setNome(request.getNome());
        if (request.getGenero() != null) usuario.setGenero(request.getGenero());
        if (request.getIdade() != null) usuario.setIdade(request.getIdade());
        if (request.getEstado() != null) usuario.setEstado(request.getEstado());
        if (request.getCidade() != null) usuario.setCidade(request.getCidade());
        if (usuario.getRole() == null) usuario.setRole("ROLE_VISITOR");
        // Salva preferências de notificação
        usuario.setNotificacoesExposicoes(request.isNotificacoesExposicoes());
        usuario.setNotificacoesEventos(request.isNotificacoesEventos());
        usuarioRepository.save(usuario);

        // Cria o checkup
        Checkup checkup = new Checkup();
        checkup.setUsuario(usuario);
        checkup.setLocal(request.getLocal());
        // Sempre usa a data/hora do servidor
        checkup.setDataHora(LocalDateTime.now());
        return repository.save(checkup);
    }

    @Override
    public long getTotalCheckupsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        List<Checkup> checkups = repository.findByDataHoraBetween(startDate, endDate);
        System.out.println("Consultando check-ins entre " + startDate + " e " + endDate + ": " + checkups.size());
        return checkups.size();
    }
}
