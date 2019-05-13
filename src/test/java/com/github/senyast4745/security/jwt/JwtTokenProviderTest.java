package com.github.senyast4745.security.jwt;

import com.github.senyast4745.model.Role;
import com.github.senyast4745.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtTokenProviderTest {
    private MockMvc mockMvc;

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProviderTest.class);

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void createToken() {
        assertNotNull(jwtTokenProvider.createToken("sensor", Role.SENSOR.name()));
    }

    @Test
    public void getAuthentication() {
        String token = null;
        try {
            token = getTokenByLogin("sensor", "sensor");
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }
        assertNotNull(jwtTokenProvider.getAuthentication(token));
        assertEquals("sensor", ((User)jwtTokenProvider.getAuthentication(token).getPrincipal()).getUsername());
        assertEquals("", jwtTokenProvider.getAuthentication(token).getCredentials());
        assertTrue(passwordEncoder.matches("sensor", ((User) jwtTokenProvider.getAuthentication(token).getPrincipal()).getPassword()));
        assertEquals(new SimpleGrantedAuthority(Role.SENSOR.name()), jwtTokenProvider.getAuthentication(token).getAuthorities().iterator().next());

    }

    @Test
    public void resolveOkToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + " ");
        assertNotNull(jwtTokenProvider.resolveToken(request));

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.addHeader("Authorization", "Bearer " + "");
        assertNotNull(jwtTokenProvider.resolveToken(request));

        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.addHeader("Authorization", "Bearer " + "asd ");
        assertNotNull(jwtTokenProvider.resolveToken(request));
    }

    @Test
    public void resolveNotOkToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorizati", "Bearer " + "");
        assertNull(jwtTokenProvider.resolveToken(request));

        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.addHeader("Authorization", "Bearer");
        assertNull(jwtTokenProvider.resolveToken(request));

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        request2.addHeader("Authorization", "Bearer" + "as");
        assertNull(jwtTokenProvider.resolveToken(request));
    }

    @Test
    public void nonValidateExpiredToken() {
        try {
            String token = jwtTokenProvider.createToken("user", Role.SENSOR.name());
            jwtTokenProvider.validateToken(token);
        } catch (Throwable t){
            fail(t);
        }
        Runnable r = () -> {
            Field field = null;
            long time = 0;
            try {
                field = jwtTokenProvider.getClass().getDeclaredField("validityInMilliseconds");
                field.setAccessible(true);
                time = (long) field.get(jwtTokenProvider);
                field.set(jwtTokenProvider, (long)1);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
                e.printStackTrace();
            }
            String token = jwtTokenProvider.createToken("user", Role.SENSOR.name());
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            assertThrows(InvalidJwtAuthenticationException.class, () -> jwtTokenProvider.validateToken(token));
            if(time != 0){
                try {
                    field.set(jwtTokenProvider, time);
                    Field finalField = field;
                    LOGGER.info(() -> {
                        try {
                            return  "Reflection field validityInMilliseconds :" + finalField.get(jwtTokenProvider);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        fail();
                        return "Fail";
                    });
                } catch (IllegalAccessException e) {
                    fail(e);
                    e.printStackTrace();
                }
            }

        };
        r.run();
        assertThrows(InvalidJwtAuthenticationException.class,()->  jwtTokenProvider.validateToken("BLEesd7OuuKvOzYHQAkdEbRndsPWAD4gAlXv"));
        assertThrows(InvalidJwtAuthenticationException.class,()->  jwtTokenProvider.validateToken(null));
        assertThrows(InvalidJwtAuthenticationException.class,()->  jwtTokenProvider.validateToken(" "));


    }


    @Test
    public void validateOkToken(){
        String token = jwtTokenProvider.createToken("user", Role.SENSOR.name());
        assertTrue(jwtTokenProvider.validateToken(token));

        token = jwtTokenProvider.createToken("admin", Role.ADMIN.name());
        assertTrue(jwtTokenProvider.validateToken(token));
    }



    public String getTokenByLogin(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login?usr=" + username + "&pass=" + password)).andReturn();
        return makeToken(username, result);
    }

    private String makeToken(String username, MvcResult result) throws UnsupportedEncodingException {
        String response = result.getResponse().getContentAsString();
        response = response.replace("{\"username\":" + "\"" + username + "\"" + ",\"token\":\"", "");
        return response.replace("\"}", "");
    }
}