package com.br.dasa.minhasfinancas.api.resource;

import com.br.dasa.minhasfinancas.api.dto.UsuarioDTO;
import com.br.dasa.minhasfinancas.exception.ErroAutenticacao;
import com.br.dasa.minhasfinancas.exception.RegraNegocioException;
import com.br.dasa.minhasfinancas.model.entity.Usuario;
import com.br.dasa.minhasfinancas.model.services.LancamentoService;
import com.br.dasa.minhasfinancas.model.services.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc

public class UsuarioResourceTest {

    static  final String API = "/api/usuarios";
    static final MediaType JSON = MediaType.APPLICATION_JSON;


    @Autowired
    MockMvc mvc;

    @MockBean
    UsuarioService service;

    @MockBean
    LancamentoService lancamentoService;

    @Test
    public void deveAutenticarUmUsuario() throws Exception {
        // cenario
        String email = "usuario@email.com";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); // representa o json q sera enviado
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build(); // retornado quando chamar o metodo de autenticar do servico

        Mockito.when(service.autenticar(email,senha)).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao e verificacao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                                                .post(API.concat("/autenticar"))
                                                .accept(JSON)
                                                .contentType(JSON)
                                                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk() )
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) )
                ;


    }
    @Test
    public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
        // cenario
        String email = "usuario@email.com";
        String senha = "123";

        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); // representa o json q sera enviado


        Mockito.when(service.autenticar(email,senha)).thenThrow(ErroAutenticacao.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao e verificacao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/autenticar"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest() );
        ;


    }

    @Test
    public void deveCriarUmUsuario() throws Exception {
        // cenario
        String email = "usuario@email.com";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); // representa o json q sera enviado
        Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build(); // retornado quando chamar o metodo de autenticar do servico

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);

        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao e verificacao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated() )
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
                .andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) )
        ;


    }
    @Test
    public void deveRetornarBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception {
        // cenario
        String email = "usuario@email.com";
        String senha = "123";
        UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build(); // representa o json q sera enviado

        Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);

        String json = new ObjectMapper().writeValueAsString(dto);

        // execucao e verificacao

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API)
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest() )

        ;

    }

}
