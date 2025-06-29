package br.univille.projfabsofttotemmuseum.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.univille.projfabsofttotemmuseum.entity.Usuario;
import br.univille.projfabsofttotemmuseum.repository.UsuarioRepository;
import br.univille.projfabsofttotemmuseum.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    // Padrão regex para validação de email
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");

    @Override
    public Usuario save(Usuario usuario) {
        return repository.save(usuario);
    }

    @Override
    public List<Usuario> getAllUsuarios() {
        return repository.findAll();
    }

    @Override
    public Usuario getUsuarioById(Long id) {
        Optional<Usuario> usuario = repository.findById(id);
        return usuario.orElse(null);
    }

    @Override
    public Usuario delete(Long id) {
        Optional<Usuario> usuario = repository.findById(id);
        if (usuario.isPresent()) {
            repository.delete(usuario.get());
            return usuario.get();
        }
        return null;
    }

    @Override
    public Usuario findOrCreateUserByIdentifier(String identifier) {
        Optional<Usuario> usuarioOpt = Optional.empty();

        // Tenta encontrar por email primeiro
        if (EMAIL_PATTERN.matcher(identifier).matches()) {
            usuarioOpt = Optional.ofNullable(repository.findByEmail(identifier));
        }

        // Se não encontrou por email e o identificador não é um email, tenta por telefone
        if (usuarioOpt.isEmpty()) {
            // Um regex mais simples para telefone, ou você pode ajustar conforme a necessidade
            // Este regex tenta cobrir formatos como (DD) XXXXX-XXXX ou XXXXXXXXXXX
            if (identifier.matches("^\\(?\\d{2}\\)?\\s?\\d{4,5}[\\- ]?\\d{4}$") || identifier.matches("^\\d{10,11}$")) {
                usuarioOpt = repository.findByTelefone(identifier);
            }
        }

        // Se o usuário não foi encontrado, cria um novo
        if (usuarioOpt.isEmpty()) {
            Usuario novoUsuario = new Usuario();
            if (EMAIL_PATTERN.matcher(identifier).matches()) {
                novoUsuario.setEmail(identifier);
                novoUsuario.setTelefone(null); // Garante que o telefone seja nulo se for um email
            } else {
                // Para números de telefone sem email, gera um email temporário único
                String tempEmail = "temp_" + System.currentTimeMillis() + "@museum.com";
                int counter = 0;
                // Garante unicidade do email temporário
                while(repository.findByEmail(tempEmail) != null) { // findByEmail retorna null se não encontrar
                    tempEmail = "temp_" + System.currentTimeMillis() + "_" + (++counter) + "@museum.com";
                }
                novoUsuario.setEmail(tempEmail);
                novoUsuario.setTelefone(identifier);
            }
            // Define valores padrão para as notificações e role para o novo usuário
            novoUsuario.setNotificacoesExposicoes(false);
            novoUsuario.setNotificacoesEventos(false);
            novoUsuario.setNotificacoesNovidades(false);
            novoUsuario.setRole("ROLE_VISITOR"); // Definindo role padrão para novos usuários
            return repository.save(novoUsuario);
        }
        return usuarioOpt.get();
    }

    @Override
    public Usuario saveOrUpdateUsuarioForNotifications(Usuario usuario) {
        // Encontra o usuário existente pelo email, que é um campo único
        Usuario existingUser = repository.findByEmail(usuario.getEmail());

        if (existingUser != null) { // Se o usuário com este email já existe
            // Atualiza apenas os campos relevantes para notificações e telefone
            existingUser.setTelefone(usuario.getTelefone());
            existingUser.setNotificacoesExposicoes(usuario.isNotificacoesExposicoes());
            existingUser.setNotificacoesEventos(usuario.isNotificacoesEventos());
            existingUser.setNotificacoesNovidades(usuario.isNotificacoesNovidades());
            // Outros campos como nome, idade, genero, estado, cidade não são atualizados
            // por esta função, pois são esperados do check-in ou de um perfil completo.
            return repository.save(existingUser);
        } else {
            // Se o usuário não existe, cria um novo
            // Garante que o email é obrigatório aqui
            if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email é obrigatório para criar um novo usuário para notificações.");
            }
            usuario.setRole("ROLE_VISITOR"); // Define o papel padrão
            return repository.save(usuario);
        }
    }
}
