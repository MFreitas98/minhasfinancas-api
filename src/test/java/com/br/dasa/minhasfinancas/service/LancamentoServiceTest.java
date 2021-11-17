package com.br.dasa.minhasfinancas.service;


import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Lancamento;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.enums.StatusLancamento;
import com.br.dasa.minhasfinancas.model.enums.TipoLancamento;
import com.br.dasa.minhasfinancas.model.repository.LancamentoRepository;
import com.br.dasa.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.br.dasa.minhasfinancas.model.services.impl.LancamentoServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {


    @SpyBean
    LancamentoServiceImpl lancamentoService;


    @MockBean
    LancamentoRepository lancamentoRepository;


    @Test
    public void deveSalvarUmLancamento() {
        //cenario
        Lancamento lancamentoSalvar = LancamentoRepositoryTest.criaLancamento();
        Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criaLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(lancamentoRepository.save(lancamentoSalvar)).thenReturn(lancamentoSalvo);

        //execucao

        Lancamento lancamento = lancamentoService.salvar(lancamentoSalvar);

        // verificacao
        Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarUmLancamentoQuandoHouverErroValidacao() {
        //cenario
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.criaLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoASalvar);

        //execucao

        Assertions.catchThrowableOfType( () -> lancamentoService.salvar(lancamentoASalvar), RegraNegocioException.class );

        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);

    }
    @Test
    public void deveAtualizarUmLancamento() {
        //cenario
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criaLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);


        Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        //execucao

        lancamentoService.atualizar(lancamentoSalvo);

        // verificacao
         Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
    }

     @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        //cenario
         Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();

         // execucao e verificacao
         Assertions.catchThrowableOfType( () -> lancamentoService.atualizar(lancamento), NullPointerException.class );
         Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamento);
     }

     @Test
    public void deveDeletarUmLancamento() {
        // cenario
         Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
         lancamento.setId(1l);

         // execucao
         lancamentoService.deletar(lancamento);

         // verificacao
         Mockito.verify(lancamentoRepository).delete(lancamento);


     }

     @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
         // cenario
         Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();


         // execucao
         Assertions.catchThrowableOfType( () -> lancamentoService.deletar(lancamento), NullPointerException.class );

         // verificacao
         Mockito.verify(lancamentoRepository,Mockito.never() ).delete(lancamento);

     }

     @Test
    public void deveFiltrarLancamentos() {
        //cenario
         Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
         lancamento.setId(1l);
         List<Lancamento> lista = Arrays.asList(lancamento);
         Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(lista);

         // execucao
         List<Lancamento> resultado = lancamentoService.buscar(lancamento);

         //verificacacoes
         Assertions.assertThat(resultado)
                 .isNotEmpty()
                 .hasSize(1)
                 .contains(lancamento);

     }

    @Test
    public void deveAtualiarOStatusDeUmLancamento() {
        //cenario
        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);
        // execucao
        lancamentoService.atualizarStatus(lancamento, novoStatus);

        //verificacao
        Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        Mockito.verify(lancamentoService).atualizar(lancamento);

    }

    @Test
    public void deveObterUmLancamentoPorID() {
        //cenario
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(id);

        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));

        //execucao
        Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

        //verificacao
        Assertions.assertThat(resultado.isPresent()).isTrue();

    }
    @Test
    public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
        //cenario
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.criaLancamento();
        lancamento.setId(id);

        Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Lancamento> resultado = lancamentoService.obterPorId(id);

        //verificacao
        Assertions.assertThat(resultado.isPresent()).isFalse();

    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        //cenario
        Lancamento lancamento = new Lancamento();

        Throwable erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descricao valida.");

        lancamento.setDescricao("");

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descricao valida.");

        lancamento.setDescricao("Salario");

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mes valido.");

        lancamento.setMes(-1);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mes valido.");

        lancamento.setMes(13);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mes valido.");

        lancamento.setMes(1);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");

        lancamento.setAno(200);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano valido.");

        lancamento.setAno(2021);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario.");

        lancamento.setUsuario(new Usuario());

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuario.");

        lancamento.getUsuario().setId(1l);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor valido.");

        lancamento.setValor(BigDecimal.ZERO);

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor valido.");

        lancamento.setValor(BigDecimal.valueOf(1));

        erro = Assertions.catchThrowable( () -> lancamentoService.validar(lancamento) );
        Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lancamento.");



    }
}


