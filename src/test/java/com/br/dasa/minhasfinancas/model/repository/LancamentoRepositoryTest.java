package com.br.dasa.minhasfinancas.model.repository;


import com.br.dasa.minhasfinancas.model.entity.Lancamento;
import com.br.dasa.minhasfinancas.model.enums.StatusLancamento;
import com.br.dasa.minhasfinancas.model.enums.TipoLancamento;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveSalvarUmLancamento() {
        Lancamento lancamento = criaLancamento();


        lancamento = lancamentoRepository.save(lancamento);

          Assertions.assertThat(lancamento.getId()).isNotNull();


    }

    @Test
    public void deveDeletarUmLancamento() {
        Lancamento lancamento = criarEPersistirUmLancamento();


        lancamento = entityManager.find(Lancamento.class, lancamento.getId());

        lancamentoRepository.delete(lancamento);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamentoInexistente).isNull();
    }


    @Test
    public void deveAtualizarUmLancamento() {
        Lancamento lancamento = criarEPersistirUmLancamento();

        lancamento.setAno(2018);
        lancamento.setDescricao("Teste Atualizar");
        lancamento.setStatus(StatusLancamento.CANCELADO);

        lancamentoRepository.save(lancamento);

        Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2018);
        Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Tese Atualizar");
        Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);
    }

    @Test
    public void deveBuscarUmLancamentoPorId() {
        Lancamento lancamento = criarEPersistirUmLancamento();

        Optional<Lancamento> lancamentoEncontrado = lancamentoRepository.findById(lancamento.getId());

        Assertions.assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }


    public static Lancamento criaLancamento() {
        Lancamento lancamento = Lancamento.builder()
                .ano(2021)
                .mes(1)
                .descricao("lancamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE).
                dataCadastro(LocalDate.now())
                .build();
        return lancamento;
    }


    private Lancamento criarEPersistirUmLancamento() {
        Lancamento lancamento = criaLancamento();
        entityManager.persist(lancamento);
        return lancamento;
    }
}
