package br.univille.projfabsofttotemmuseum.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.univille.projfabsofttotemmuseum.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método para encontrar um usuário por email
    Usuario findByEmail(String email);

    // Método para encontrar um usuário por telefone (retorna Optional para melhor tratamento de nulos)
    Optional<Usuario> findByTelefone(String telefone);

    // Método para encontrar todos os usuários que aceitaram notificações por e-mail
    List<Usuario> findByNotificaEmailTrue();

    // Método para encontrar todos os usuários que aceitaram notificações por WhatsApp
    List<Usuario> findByNotificaWhatsappTrue();
}
