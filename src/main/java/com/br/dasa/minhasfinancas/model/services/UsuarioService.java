package com.br.dasa.minhasfinancas.model.services;

import com.br.dasa.minhasfinancas.model.entity.Usuario;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UsuarioService {

      Usuario autenticar(String email, String senha);

      Usuario salvarUsuario(Usuario usuario);

      void validarEmail(String email);

      Optional<Usuario> obterPorId(Long id);


}
