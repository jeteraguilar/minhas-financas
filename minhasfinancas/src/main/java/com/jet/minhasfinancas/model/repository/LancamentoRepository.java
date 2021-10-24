package com.jet.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jet.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
