package com.br.dasa.minhasfinancas.model.services.impl;

import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Lancamento;
import com.br.dasa.minhasfinancas.model.enums.StatusLancamento;
import com.br.dasa.minhasfinancas.model.repository.LancamentoRepository;
import com.br.dasa.minhasfinancas.model.services.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class LancamentoServiceImpl implements LancamentoService {


    private LancamentoRepository lancamentoRepository;

    @Autowired
     public LancamentoServiceImpl(LancamentoRepository lancamentoRepository){
         this.lancamentoRepository = lancamentoRepository;
     }

    @Override
    @Transactional
    public Lancamento salvar(Lancamento lancamento) {
        validar(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public Lancamento atualizar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validar(lancamento);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletar(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        lancamentoRepository.delete(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return lancamentoRepository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
          lancamento.setStatus(status);
          atualizar(lancamento);
    }

    @Override
    public void validar(Lancamento lancamento) {
         if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
             throw new RegraNegocioException("Informe uma Descricao valida.");
         }

         if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
             throw  new RegraNegocioException("Informe um Mes valido.");
         }

         if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
             throw  new RegraNegocioException("Informe um Ano valido.");
         }

         if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
             throw new RegraNegocioException("Informe um Usuario.");
         }
         if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
             throw  new RegraNegocioException("Informe um valor valido.");
         }
         if(lancamento.getTipo() == null) {
             throw new RegraNegocioException("Informe um tipo de Lancamento.");
         }

    }
}
