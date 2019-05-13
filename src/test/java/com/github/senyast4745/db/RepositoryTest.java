package com.github.senyast4745.db;


import com.github.senyast4745.config.TableJpaConfig;
import com.github.senyast4745.model.Role;
import com.github.senyast4745.model.Sensor;
import com.github.senyast4745.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {TableJpaConfig.class},
        loader = AnnotationConfigContextLoader.class)
@Transactional
public class RepositoryTest {

    @Resource
    private UserRepository userRepository;

    @Resource
    SensorRepository sensorRepository;


    private User expected;


    @Before
    public void addData() {
        userRepository.deleteAll();
        sensorRepository.deleteAll();
        expected = new User("admin", "admin", Role.ADMIN);
        User admin2 = new User("admin2", "admin", Role.ADMIN);
        User sensor = new User("sensor", "sensor", Role.ADMIN);
        User sensor2 = new User("sensor2", "sensor", Role.ADMIN);

        List<User> users = Arrays.asList(expected, admin2, sensor, sensor2);
        userRepository.checkAndSaveUsers(users);


        Sensor data1 = new Sensor(new double[]{10, 10}, 10, "Saint-Petersburg");  //for Test
        Sensor data2 = new Sensor(new double[]{20, 20}, 20, "Saint-Petersburg");  //for Test
        Sensor data3 = new Sensor(new double[]{30, 30}, 30, "Kirov");  //for Test

        List<Sensor> sensors = Arrays.asList(data1, data2, data3);

        this.sensorRepository.saveAll(sensors);
    }

    @After
    public void clean() {
        userRepository.deleteAll();
        sensorRepository.deleteAll();
    }

    @Test
    public void givenUserwhenSavethenGetOk() {

        User actual = userRepository.findByUsername("admin").get();
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(Role.ADMIN, actual.getRole());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getAuthorities(), actual.getAuthorities());

        actual = userRepository.findByUsername("admin2").get();
        assertEquals("admin2", actual.getUsername());
    }

    @Test
    public void checkingClean(){
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());
    }

    @Test
    public void checkingAddingUserAfterDelete(){
        userRepository.deleteAll();
        assertEquals(0, userRepository.count());

        userRepository.checkAndSaveUser(expected);
        assertEquals(1, userRepository.count());
        assertEquals(expected.getUsername(), userRepository.findByUsername("admin").get().getUsername());
        assertEquals(expected.getPassword(), userRepository.findByUsername("admin").get().getPassword());
        assertEquals(expected.getRole(), userRepository.findByUsername("admin").get().getRole());
    }

    @Test
    public void checkingExistingUser(){
        try {
            userRepository.checkAndSaveUser(new User("a", "a", Role.ADMIN));
        } catch (Throwable e){
            fail(e);
        }

        assertThrows(DuplicateDataException.class, () -> userRepository.checkAndSaveUser(new User("admin", "a", Role.ADMIN)));
        User admin2 = new User("admin2", "admin", Role.ADMIN);
        User sensor = new User("sensor", "sensor", Role.ADMIN);
        User sensor2 = new User("sensor2", "sensor", Role.ADMIN);

        List<User> users = Arrays.asList(admin2, sensor, sensor2);
        assertThrows(DuplicateDataException.class, () -> userRepository.checkAndSaveUsers(users));
    }

    @Test
    public void testingSensorsData(){
        assertEquals(3, sensorRepository.count());
        sensorRepository.deleteAll();
        Sensor sensor = new Sensor(new double[]{20,30}, 40, "Saint-Petersburg");

        try {
            sensorRepository.save(sensor);
        } catch (Throwable e){
            fail(e);
        }
        assertEquals(1, sensorRepository.count());
        assertEquals(sensor, sensorRepository.findAll().iterator().next());
    }
}
