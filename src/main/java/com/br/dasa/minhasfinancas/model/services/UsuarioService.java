package com.br.dasa.minhasfinancas.model.services;

import com.br.dasa.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {

      Usuario autenticar(String email, String senha);

      Usuario salvarUsuario(Usuario usuario);

      void validarEmail(String email);


}
