package com.jet.minhasfinancas.service;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.jet.minhasfinancas.exception.ErroAutenticacao;
import com.jet.minhasfinancas.exception.RegraNegocioException;
import com.jet.minhasfinancas.model.entity.Usuario;
import com.jet.minhasfinancas.model.repository.UsuarioRepository;
import com.jet.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl service;	
	
	@MockBean
	UsuarioRepository repository;	
	
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
					.id(1l)
					.nome("nome")
					.email("email@email.com")
					.senha("senha").build();
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUmUsuarioComEmailJaCardastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email("email@email.com").build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail("email@email.com");
		
		//acao
		service.salvarUsuario(usuario);
		
		//verificacao
		Mockito.verify( repository, Mockito.never() ).save(usuario);
		
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenário
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
		Mockito.when( repository.findByEmail(email) ).thenReturn(Optional.of(usuario));
		
		//acao
		Usuario result = service.autenticar(email,  senha);
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQunadoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		
		//cenário				
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//acao
		Throwable exception = Assertions.catchThrowable( () ->service.autenticar("email@email.com", "senha") );
		
		//verificacao
		Assertions.assertThat(exception)
				.isInstanceOf(ErroAutenticacao.class)
				.hasMessage("Usuário não encontrado para o email informado");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when( repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com", "123") );
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida");
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		
		//cenário				
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//ação
		service.validarEmail("email@email.com");
	}
	@Test(expected = RegraNegocioException.class)
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		service.validarEmail("email@email.com");
	}
}
