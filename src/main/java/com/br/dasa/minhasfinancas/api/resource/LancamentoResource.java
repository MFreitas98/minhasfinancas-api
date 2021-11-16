package com.br.dasa.minhasfinancas.api.resource;

import com.br.dasa.minhasfinancas.api.dto.AtualizaStatusDto;
import com.br.dasa.minhasfinancas.api.dto.LancamentoDTO;
import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Lancamento;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.enums.StatusLancamento;
import com.br.dasa.minhasfinancas.model.enums.TipoLancamento;
import com.br.dasa.minhasfinancas.model.services.LancamentoService;
import com.br.dasa.minhasfinancas.model.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

    private final LancamentoService lancamentoService;
    private final UsuarioService usuarioService;


    @GetMapping
    public ResponseEntity buscar(
            @RequestParam(value ="descricao", required = false) String descricao,
            @RequestParam(value = "mes",required = false) Integer mes,
            @RequestParam(value = "ano",required = false) Integer ano,
            @RequestParam("usuario") Long idUsuario
                  ) {
           Lancamento lancamerntoFiltro = new Lancamento();
           lancamerntoFiltro.setDescricao(descricao);
           lancamerntoFiltro.setMes(mes);
           lancamerntoFiltro.setAno(ano);

        Optional<Usuario> usuario = usuarioService.obterPorId(idUsuario);
           if(!usuario.isPresent()) {
               return  ResponseEntity.badRequest().body("Nao foi possivel realizar a consulta. Usuario nao encontrado para o id informado.");
           }else {
               lancamerntoFiltro.setUsuario(usuario.get());
           }
        List<Lancamento> lancamentos = lancamentoService.buscar(lancamerntoFiltro);
               return ResponseEntity.ok(lancamentos);
    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody LancamentoDTO dto) {
         try {
             Lancamento entidade = converter(dto);
             entidade =  lancamentoService.salvar(entidade);
             return new ResponseEntity(entidade, HttpStatus.CREATED);
         } catch (RegraNegocioException e) {
             return  ResponseEntity.badRequest().body(e.getMessage());

         }

    }
    @PutMapping("{id}")
    public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
       return  lancamentoService.obterPorId(id).map( entity -> {
           try {
               Lancamento lancamento = converter(dto);
               lancamento.setId(entity.getId());
               lancamentoService.atualizar(lancamento);
               return ResponseEntity.ok(lancamento);
           } catch (RegraNegocioException e) {
               return ResponseEntity.badRequest().body(e.getMessage());
           }
       }).orElseGet( () ->
               new ResponseEntity("Lancamento nao encontrado na base de Dados." , HttpStatus.BAD_REQUEST) );
    }

    @PutMapping("/{id}/atualiza-status")
     public ResponseEntity atualizarStatus(@PathVariable Long id, @RequestBody AtualizaStatusDto dto) {
        return  lancamentoService.obterPorId(id).map(entity -> {
            StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
            if (statusSelecionado == null) {
                return ResponseEntity.badRequest().body("Nao foi possivel atualizar o status do lancamento, envie um status valido.");
            }
            try {
                entity.setStatus(statusSelecionado);
                lancamentoService.atualizar(entity);
                return  ResponseEntity.ok(entity);
            } catch (RegraNegocioException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet( () ->
                new ResponseEntity("Lancamento nao encontrado na base de Dados." , HttpStatus.BAD_REQUEST) );

     }

    @DeleteMapping("/{id}")
    public ResponseEntity deletar(@PathVariable ("id") Long id) {
        return lancamentoService.obterPorId(id).map( entidade-> {
                lancamentoService.deletar(entidade);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }).orElseGet( () -> new ResponseEntity("Lancamento nao encontrado na base de Dados." , HttpStatus.BAD_REQUEST) );

}


    private Lancamento converter(LancamentoDTO dto){
        Lancamento lancamento = new Lancamento();
          lancamento.setId(dto.getId());
          lancamento.setDescricao(dto.getDescricao());
          lancamento.setAno(dto.getAno());
          lancamento.setMes(dto.getMes());
          lancamento.setValor(dto.getValor());

         Usuario usuario = usuarioService.obterPorId(dto.getUsuario())
                  .orElseThrow( () -> new RegraNegocioException("Usuario nao encontrado para o id imformado."));
          lancamento.setUsuario(usuario);
          if(dto.getTipo() != null) {
              lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
          }
          if(dto.getStatus() != null) {
              lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
          }
        return  lancamento;
    }



}
