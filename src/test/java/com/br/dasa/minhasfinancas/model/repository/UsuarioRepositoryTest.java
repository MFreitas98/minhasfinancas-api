package com.br.dasa.minhasfinancas.model.repository;

import com.br.dasa.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

     @Autowired
     UsuarioRepository usuarioRepository;

     @Autowired
     TestEntityManager entityManager;

     @Test
     public void deveVerificarAExistenciaDeUmEmail() {
         // cenario
         Usuario usuario = criarUsuario();
         entityManager.persist(usuario);

         // acao/execucao
        boolean result = usuarioRepository.existsByEmail("usuario@email.com");

         // verificacao

         Assertions.assertThat(result).isTrue();
     }

     @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
         // cenario


         // acao/execucao
       boolean result = usuarioRepository.existsByEmail("usuario@email.com");


         //verificacao

         Assertions.assertThat(result).isFalse();


     }

     @Test
      public void devePersistirUmUsuarioNaBaseDeDados() {
         // cenario
         Usuario usuario = criarUsuario();

         // acao
           Usuario usuarioSalvo =  usuarioRepository.save(usuario);

         // verificacao

         Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
     }

     @Test
     public void deveBuscarUmUsuarioPorEmail() {
         // cenario
         Usuario usuario = criarUsuario();
          entityManager.persist(usuario);
         // acao

         // verificacao
         Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

         Assertions.assertThat(result.isPresent()).isTrue();

     }
    @Test
    public void deveRetornarVazioAOBuscarUmUsuarioPorEmailQuandoNaoExisteNaBase() {
        // cenario
        // acao
        // verificacao
        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        Assertions.assertThat(result.isPresent()).isFalse();

    }

      public static Usuario criarUsuario() {
         return Usuario.builder()
                 .nome("usuario")
                 .email("usuario@email.com")
                 .senha("senha")
                 .build();
      }

}
