package com.br.dasa.minhasfinancas.service;

import com.br.dasa.minhasfinancas.exception.ErroAutenticacao;
import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.repository.UsuarioRepository;
import com.br.dasa.minhasfinancas.model.services.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl usuarioService;

    @MockBean
    UsuarioRepository  usuarioRepository;


    @Test(expected = Test.None.class)
    public void deveSalvarUmUsuario() {
        // cenario
          Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
          Usuario usuario = Usuario.builder()
                  .id(1l)
                  .nome("nome")
                  .email("email@email.com")
                  .senha("senha")
                  .build();
          Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
        // acao

        Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());

        // verificacao
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
    }

    @Test(expected = RegraNegocioException.class)
    public void naoDeveSalvarUmUsuarioComEmailCadastrado() {
        // cenario
         String email = "email@email.com";
         Usuario usuario = Usuario.builder().email(email).build();
         Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail(email);

         // acao
         usuarioService.salvarUsuario(usuario);

        // verificacao
        Mockito.verify(usuarioRepository,Mockito.never()).save(usuario);

    }

    @Test(expected = Test.None.class)
    public void deveAutenticarUmUsuarioComSucesso() {
        // cenario
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).build();
        Mockito.when(usuarioRepository.findByEmail(email) ).thenReturn(Optional.of(usuario));

        //acao
        Usuario result = usuarioService.autenticar(email, senha);

        // verificacao
        Assertions.assertThat(result).isNotNull();
    }




    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
        // cenario
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        // acao
         Throwable exception = Assertions.catchThrowable( () -> usuarioService.autenticar("email@email.com", "senha:"));

         // verificacao
        Assertions.assertThat(exception)
                .isInstanceOf(ErroAutenticacao.class)
                .hasMessage("Usuario nao encontrado para o email informado.");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {
        // cenario
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        // acao
        Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("email@email.com", "123"));
        Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha Invalida.");

    }


    @Test(expected = Test.None.class)
    public void deveValidarEmail(){
        // cenario
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        // acao
        usuarioService.validarEmail("email@email.com");
    }

    @Test(expected = RegraNegocioException.class)
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
        // cenario
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //acao
        usuarioService.validarEmail("email@email.com");
    }

}
