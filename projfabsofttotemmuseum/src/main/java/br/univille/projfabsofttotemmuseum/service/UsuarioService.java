package br.univille.projfabsofttotemmuseum.service;

import java.util.List;

import br.univille.projfabsofttotemmuseum.entity.Usuario;

public interface UsuarioService {
    Usuario save(Usuario usuario);
    List<Usuario> getAllUsuarios();
    Usuario getUsuarioById(Long id);
    Usuario delete(Long id);
    Usuario findOrCreateUserByIdentifier(String identifier);
    Usuario saveOrUpdateUsuarioForNotifications(Usuario usuario);
}
