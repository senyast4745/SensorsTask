package com.github.senyast4745.db;

import com.github.senyast4745.model.Role;
import com.github.senyast4745.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DbInit implements CommandLineRunner {
    private UserRepository userRepository;
    private SensorRepository sensorRepository;
    private PasswordEncoder passwordEncoder;

    public DbInit(UserRepository userRepository, PasswordEncoder passwordEncoder, SensorRepository sensorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sensorRepository = sensorRepository;
    }


    @Override
    public void run(String... args) {

        this.userRepository.deleteAll();
        this.sensorRepository.deleteAll();

        User admin = new User("admin",passwordEncoder.encode("admin"),Role.ADMIN);
        User sensor = new User("sensor",passwordEncoder.encode("sensor"), Role.SENSOR);

        List<User> users = Arrays.asList(admin, sensor);

        this.userRepository.checkAndSaveUsers(users);

    }
}
