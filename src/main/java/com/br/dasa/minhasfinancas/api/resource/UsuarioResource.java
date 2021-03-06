package com.br.dasa.minhasfinancas.api.resource;


import com.br.dasa.minhasfinancas.api.dto.UsuarioDTO;
import com.br.dasa.minhasfinancas.exception.ErroAutenticacao;
import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.services.LancamentoService;
import com.br.dasa.minhasfinancas.model.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {


       private final UsuarioService usuarioService;
       private final LancamentoService lancamentoService;


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
       @GetMapping("{id}/saldo")
       public  ResponseEntity obterSaldo(@PathVariable("id") Long id) {
           Optional<Usuario> usuario = usuarioService.obterPorId(id);

           if(!usuario.isPresent()) {
               new ResponseEntity(HttpStatus.NOT_FOUND);
           }

           BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
           return ResponseEntity.ok(saldo);
       }

}
