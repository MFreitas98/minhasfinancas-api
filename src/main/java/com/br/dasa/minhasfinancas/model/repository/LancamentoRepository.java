package com.br.dasa.minhasfinancas.model.repository;

import com.br.dasa.minhasfinancas.model.entity.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LancamentoRepository extends JpaRepository<Lancamento,Long> {
}
