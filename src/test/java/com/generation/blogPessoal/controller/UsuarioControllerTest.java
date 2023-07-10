package com.generation.blogPessoal.controller;



import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogPessoal.model.Usuario;
import com.generation.blogPessoal.repository.UsuarioRepository;
import com.generation.blogPessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L,"Root","root@root.com", "rootroot"," "));
	}
	@Test
	@DisplayName("Casdastrar um Usuario")
	public void deveCriarUmUsuario() {
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Israel Bernardo", "israel_bernardo@email.com.br", "123456","-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuario/cadastrar", HttpMethod.POST, corpoRequisicao ,Usuario.class);
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	
	@Test
	@DisplayName("Não deve permitir duplicação de Usuario")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,"Marcos Paulo","marcos_paulo@email.com.br","marcos123","-"));
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"Marcos Paulo","marcos_paulo@email.com.br","marcos123","-"));
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuario/cadastrar", HttpMethod.POST, corpoRequisicao ,Usuario.class);
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuario")
	public void deveAtualizarUmUsuario() {
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,"bernardo max","bernardo_max@email.com.br","max123","-"));
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
				"bernardo max israel", "bernardo_maxisrael@email.com.br", "max123","-");
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuario/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	@Test
	@DisplayName("Listar todos os Usuarios")
	public void deveMostrarTodosUsuarios() {
		usuarioService.cadastrarUsuario(new Usuario(0L,"Maria flores","maria_flores@email.com.br","maria123","-"));
		usuarioService.cadastrarUsuario(new Usuario(0L,"Gaby Mel","gaby_mel@email.com.br","gaby123","-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com"," rootroot")
				.exchange("/usuario/all", HttpMethod.GET,null, String.class);
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
}
