package com.br.dasa.minhasfinancas.api.resource;


import com.br.dasa.minhasfinancas.api.dto.UsuarioDTO;
import com.br.dasa.minhasfinancas.exception.ErroAutenticacao;
import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

        @Autowired
       private UsuarioService usuarioService;

        public UsuarioResource(UsuarioService usuarioService) {
            this.usuarioService = usuarioService;
        }

       @PostMapping
       public ResponseEntity salvar(@RequestBody UsuarioDTO dto) {
           Usuario usuario = Usuario.builder()
                   .nome(dto.getNome())
                   .email(dto.getEmail())
                   .senha(dto.getSenha())
                   .build();

           try {
               Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
               return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
           } catch (RegraNegocioException e) {
               return  ResponseEntity.badRequest().body(e.getMessage());
           }
       }

        @PostMapping("/autenticar")
       public ResponseEntity autenticar(@RequestBody UsuarioDTO dto) {
          try {
              Usuario usuarioAutenticado = usuarioService.autenticar(dto.getEmail(), dto.getSenha());
              return ResponseEntity.ok(usuarioAutenticado);
          } catch (ErroAutenticacao e) {
              return ResponseEntity.badRequest().body(e.getMessage());

          }
       }

}
