package com.br.dasa.minhasfinancas.model.services.impl;

import com.br.dasa.minhasfinancas.exception.ErroAutenticacao;
import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.repository.UsuarioRepository;
import com.br.dasa.minhasfinancas.model.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
       Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

       if(!usuario.isPresent()) {
           throw  new ErroAutenticacao("Usuario nao encontrado para o email informado.");
       }
       if(!usuario.get().getSenha().equals(senha)) {
           throw  new ErroAutenticacao("Senha Invalida.");
       }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
         validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe =  usuarioRepository.existsByEmail(email);
        if(existe) {
          throw new RegraNegocioException("Ja existe um usuario cadastrado com este email");

        }
    }
}
