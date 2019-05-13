package com.github.senyast4745.controller;

import com.github.senyast4745.db.UserRepository;
import com.github.senyast4745.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/login")
public class AuthController {

    private final
    AuthenticationManager authenticationManager;

    private final
    JwtTokenProvider jwtTokenProvider;

    private final
    UserRepository users;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository users) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.users = users;
    }

    @RequestMapping(method = RequestMethod.POST)
        public ResponseEntity signin(@RequestParam String usr, @RequestParam String pass) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(usr, pass));
            String token = jwtTokenProvider.createToken(usr, this.users.findByUsername(usr).orElseThrow(()
                    -> new UsernameNotFoundException("Username " + usr + "not found")).getRole().name());

            Map<Object, Object> model = new HashMap<>();
            model.put("username", usr);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }
}
