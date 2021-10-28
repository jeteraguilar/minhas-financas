package com.jet.minhasfinancas.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jet.minhasfinancas.exception.RegraNegocioException;
import com.jet.minhasfinancas.model.entity.Usuario;
import com.jet.minhasfinancas.model.repository.UsuarioRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@Autowired
	UsuarioService service;
	
	@Autowired
	UsuarioRepository repository;
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		
		//cenário
		repository.deleteAll();
		
		//ação
		service.validarEmail("email@email.com");
	}
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//cenário
		Usuario usuario = Usuario.builder().nome("usuario").email("email@email.com").build();
		repository.save(usuario);
		
		//ação
		service.validarEmail("email@email.com");
	}
}
