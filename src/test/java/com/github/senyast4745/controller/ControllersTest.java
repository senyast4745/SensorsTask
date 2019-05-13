package com.github.senyast4745.controller;

import com.github.senyast4745.db.DuplicateDataException;
import com.github.senyast4745.db.SensorRepository;
import com.github.senyast4745.db.UserRepository;
import com.github.senyast4745.model.ExceptionModel;
import com.github.senyast4745.model.Role;
import com.github.senyast4745.model.Sensor;
import com.github.senyast4745.model.User;
import com.github.senyast4745.testModel.JsonSensorModel;
import com.github.senyast4745.testModel.JsonSensorsArrayModel;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ControllersTest {


    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private WebApplicationContext context;

    private static String HEADER_AUTH_NAME = "Authorization";

    private static String TOKEN_NAME = "Bearer ";

    private Gson gson = new Gson();

    private Random random = new Random();

    private static final Logger LOGGER = LoggerFactory.getLogger(ControllersTest.class);


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();


        sensorRepository.deleteAll();

        Sensor data1 = new Sensor(new double[]{10.1, 10.1}, 10, "A");  //for Test
        Sensor data2 = new Sensor(new double[]{10.2, 10.2}, 11, "A");  //for Test
        Sensor data3 = new Sensor(new double[]{10.3, 10.3}, 12, "A");  //for Test
        Sensor data4 = new Sensor(new double[]{11.1, 11.1}, 13, "B");  //for Test
        Sensor data5 = new Sensor(new double[]{11.2, 11.2}, 14, "B");  //for Test
        Sensor data6 = new Sensor(new double[]{11.3, 11.3}, 15, "B");  //for Test
        Sensor data7 = new Sensor(new double[]{12.1, 12.1}, 16, "C");  //for Test
        Sensor data8 = new Sensor(new double[]{12.2, 12.2}, 17, "C");  //for Test
        Sensor data9 = new Sensor(new double[]{12.3, 12.3}, 18, "C");  //for Test
        Sensor data10 = new Sensor(new double[]{13.1, 13.1}, 19, "D");  //for Test
        Sensor data11 = new Sensor(new double[]{13.2, 13.2}, 20, "D");  //for Test
        Sensor data12 = new Sensor(new double[]{13.3, 13.3}, 21, "D");  //for Test
        Sensor data13 = new Sensor(new double[]{14.1, 14.1}, 22, "E");  //for Test
        Sensor data14 = new Sensor(new double[]{14.2, 14.2}, 23, "E");  //for Test
        Sensor data15 = new Sensor(new double[]{14.3, 14.3}, 24, "E");  //for Test
        Sensor data16 = new Sensor(new double[]{15.1, 15.1}, 25, "F");  //for Test
        Sensor data17 = new Sensor(new double[]{15.2, 15.2}, 26, "F");  //for Test
        Sensor data18 = new Sensor(new double[]{15.3, 15.3}, 27, "F");  //for Test
        //for Test

        List<Sensor> sensors = Arrays.asList(data1, data2, data3, data4, data5, data6, data7, data8,
                data9, data10, data11, data12, data13, data14, data15, data16,
                data17, data18);
        this.sensorRepository.saveAll(sensors);

        userRepository.deleteAll();

        User admin = new User("admin", passwordEncoder.encode("admin"), Role.ADMIN);
        User sensor = new User("sensor", passwordEncoder.encode("sensor"), Role.SENSOR);

        List<User> users = Arrays.asList(admin, sensor);

        this.userRepository.checkAndSaveUsers(users);


    }

    @Test
    public void existentUserCanGetTokenAndAuthentication() {
        String token = null;
        try {
            token = getTokenByLogin("admin", "admin");
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }
        assertEquals("admin", (userRepository.findByUsername("admin").get().getUsername()));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin").
                header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                .contentType("application/json");
        try {
            MvcResult result = mockMvc.perform(builder)
                    .andExpect(status().isOk()).andReturn();
            String data = result.getResponse().getContentAsString();
            LOGGER.info(() -> data);
            JsonSensorsArrayModel jsonSensorModel = gson.fromJson(data, JsonSensorsArrayModel.class);
            JsonSensorModel[] sensors = jsonSensorModel.getJsonSensorModels();
            assertEquals(10, sensors.length);
            double count = 0.3;
            for (int i = 0; i < sensors.length; i++) {
                assertEquals(12 + count, sensors[i].getCoord1());
                assertEquals(12 + count, sensors[i].getCoord2());
                assertEquals(18 + i, sensors[i].getTemperature());
                assertEquals(('C' + (int)count) , sensors[i].getCity().toCharArray()[0]);
                if (i % 3 == 0) {
                    count += 0.8;
                } else {
                    count += 0.1;
                }
                count= Math.round(count*10)/10D;
            }
        } catch (Throwable t) {
            fail(t);
            t.printStackTrace();
        }

        String json = gson.toJson(new SensorForm(10, 10, 10, "Saint-Petersburg"));
        try {
            MvcResult result = mockMvc.perform(buildPostRequestWithContent(json))
                    .andExpect(status().isOk()).andReturn();

            String data = result.getResponse().getContentAsString();
            JsonSensorModel jsonSensorModel = gson.fromJson(data, JsonSensorModel.class);
            assertEquals(10, jsonSensorModel.getCoord1());
            assertEquals(10, jsonSensorModel.getCoord2());
            assertEquals(10, jsonSensorModel.getTemperature());
            assertEquals("Saint-Petersburg", jsonSensorModel.getCity());
        } catch (Throwable t) {
            fail(t);
            t.printStackTrace();
        }

        this.userRepository.checkAndSaveUser(new User("adm", passwordEncoder.encode("adm"), Role.ADMIN));
        assertEquals("adm", (userRepository.findByUsername("adm").get().getUsername()));
        try {
            token = getTokenByLogin("adm", "adm");
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }


        builder = MockMvcRequestBuilders.get("/admin").
                header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                .contentType("application/json");
        try {
            mockMvc.perform(builder)
                    .andExpect(status().isOk());
        } catch (Throwable t) {
            fail(t);
            t.printStackTrace();
        }
    }

    @Test
    public void checkingCorrectDataAdding(){
        try {
            String json = gson.toJson(new SensorForm(16.1, 16.1, 28, "G"));
            mockMvc.perform(buildPostRequestWithContent(json))
                    .andExpect(status().isOk());
            json = gson.toJson(new SensorForm(16.2, 16.2, 29, "G"));
            mockMvc.perform(buildPostRequestWithContent(json))
                    .andExpect(status().isOk());
            json = gson.toJson(new SensorForm(16.3, 16.3, 30, "G"));
            mockMvc.perform(buildPostRequestWithContent(json))
                    .andExpect(status().isOk());

        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }

        String token = null;
        try {
            token = getTokenByLogin("admin", "admin");
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }
        assertEquals("admin", (userRepository.findByUsername("admin").get().getUsername()));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin").
                header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                .contentType("application/json");
        try {
            MvcResult result = mockMvc.perform(builder)
                    .andExpect(status().isOk()).andReturn();
            String data = result.getResponse().getContentAsString();
            LOGGER.info(() -> data);
            JsonSensorsArrayModel jsonSensorModel = gson.fromJson(data, JsonSensorsArrayModel.class);
            JsonSensorModel[] sensors = jsonSensorModel.getJsonSensorModels();
            assertEquals(10, sensors.length);
            double count = 0.3;
            for (int i = 0; i < sensors.length; i++) {
                assertEquals(13 + count, sensors[i].getCoord1());
                assertEquals(13 + count, sensors[i].getCoord2());
                assertEquals(21 + i, sensors[i].getTemperature());
                assertEquals(( 'D' + (int)count), sensors[i].getCity().toCharArray()[0]);
                if (i % 3 == 0) {
                    count += 0.8;
                } else {
                    count += 0.1;
                }
                count= Math.round(count*10)/10D;
            }
        } catch (Throwable t) {
            fail(t);
            t.printStackTrace();
        }

        try {
            token = getTokenByLogin("admin", "admin");
            builder = MockMvcRequestBuilders.get("/admin/findCity?coord1=15.8&coord2=16.1").
                    header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                    .contentType("application/json");
            MvcResult result = mockMvc.perform(builder)
                    .andExpect(status().isOk()).andReturn();
            String data = result.getResponse().getContentAsString();
            JsonSensorsArrayModel jsonSensorModel = gson.fromJson(data, JsonSensorsArrayModel.class);
            JsonSensorModel[] sensors = jsonSensorModel.getJsonSensorModels();
            assertEquals(3, sensors.length);
            double count = 0.1;
            for (int i = 0; i < sensors.length; i++) {
                assertEquals(16 + count, sensors[i].getCoord1());
                assertEquals(16 + count, sensors[i].getCoord2());
                assertEquals(28 + i, sensors[i].getTemperature());
                assertEquals("G", sensors[i].getCity());
                if (i != 0 && i % 3 == 0) {
                    count += 0.8;
                } else {
                    count += 0.1;
                }
            }
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }


    }

    @Test
    public void checkingRoot() {
        try {
            mockMvc.perform(get("/")).andExpect(status().isOk())
                    .andExpect(jsonPath("$", is("Welcome.")));
        } catch (Throwable e) {
            e.printStackTrace();
            fail(e);
        }
    }


    @Test
    public void checkingExistingReadByCity() {

        try {
            String token = getTokenByLogin("admin", "admin");
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/findCity?coord1=10.3&coord2=10.3").
                    header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                    .contentType("application/json");
            MvcResult result = mockMvc.perform(builder)
                    .andExpect(status().isOk()).andReturn();

            String data = result.getResponse().getContentAsString();
            JsonSensorsArrayModel jsonSensorModel = gson.fromJson(data, JsonSensorsArrayModel.class);
            JsonSensorModel[] sensors = jsonSensorModel.getJsonSensorModels();
            assertEquals(3, sensors.length);
            double count = 0.1;
            for (int i = 0; i < sensors.length; i++) {
                assertEquals(10 + count, sensors[i].getCoord1());
                assertEquals(10 + count, sensors[i].getCoord2());
                assertEquals(10 + i, sensors[i].getTemperature());
                assertEquals(( 'A' + (int)count), sensors[i].getCity().toCharArray()[0]);
                if (i != 0 && i % 3 == 0) {
                    count += 0.8;
                } else {
                    count += 0.1;
                }
            }
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }

        try {
            String token = getTokenByLogin("admin", "admin");
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/findCity?coord1=10.5&coord2=10.5").
                    header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                    .contentType("application/json");
            MvcResult result = mockMvc.perform(builder)
                    .andExpect(status().isOk()).andReturn();

            String data = result.getResponse().getContentAsString();
            JsonSensorsArrayModel jsonSensorModel = gson.fromJson(data, JsonSensorsArrayModel.class);
            JsonSensorModel[] sensors = jsonSensorModel.getJsonSensorModels();
            assertEquals(3, sensors.length);
            double count = 0.1;
            for (int i = 0; i < sensors.length; i++) {
                assertEquals(11 + count, sensors[i].getCoord1());
                assertEquals(11 + count, sensors[i].getCoord2());
                assertEquals(13 + i, sensors[i].getTemperature());
                assertEquals(( 'B' + (int)count), sensors[i].getCity().toCharArray()[0]);
                if (i != 0 && i % 3 == 0) {
                    count += 0.8;
                } else {
                    count += 0.1;
                }
            }
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }
    }

    @Test
    public void checkingNonExistingCity(){
        try {
            String token = getTokenByLogin("admin", "admin");
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/admin/findCity?coord1=100&coord2=100").
                    header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                    .contentType("application/json");
            mockMvc.perform(builder)
                    .andExpect(status().isBadRequest()).andReturn();
        } catch (Exception e){
            fail(e);
            e.printStackTrace();
        }

    }

    @Test
    public void checkingAccessDenied() {
        String token = null;
        try {
            token = getTokenByLogin("sensor", "sensor");
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }
        String finalToken = token;
        try {
            mockMvc.perform(get("/admin").header(HEADER_AUTH_NAME, TOKEN_NAME + finalToken)).andExpect(status().isForbidden());
            mockMvc.perform(get("/admin")).andExpect(status().isForbidden());

            mockMvc.perform(get("/admin/readByCity").header(HEADER_AUTH_NAME, TOKEN_NAME + finalToken)).andExpect(status().isForbidden());
            mockMvc.perform(get("/admin/readByCity")).andExpect(status().isForbidden());
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }


        try {
            token = getTokenByLogin("admin", "admin");
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }

        String json = gson.toJson(new SensorForm(10, 10, 10, "spb"));
        String finalJson = json;
        String finalToken1 = token;
        try {
            mockMvc.perform(post("/sensor").header(HEADER_AUTH_NAME, TOKEN_NAME + finalToken1)
                    .content(finalJson).contentType("application/json")).andExpect(status().isForbidden());
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }


        json = gson.toJson(new SensorForm(10, 10, 10, "spb"));
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.post("/sensor").contentType("application/json").content(json);
        try {
            mockMvc.perform(builder).andExpect(status().isForbidden());
        } catch (Exception e) {
            fail(e);
            e.printStackTrace();
        }
    }

    @Test
    public void checkingIncorrectData() {
        try {
            String json = gson.toJson(new SensorForm(10, 10, -10, "spb"));
            mockMvc.perform(buildPostRequestWithContent(json)).andExpect(status().isBadRequest());
            json = gson.toJson(new SensorForm(93, 10, 10, "spb"));
            mockMvc.perform(buildPostRequestWithContent(json)).andExpect(status().isBadRequest());
            json = gson.toJson(new SensorForm(87, 190, 10, "spb"));
            mockMvc.perform(buildPostRequestWithContent(json)).andExpect(status().isBadRequest());
            json = gson.toJson(new SensorForm(10, 10, Integer.MAX_VALUE + 1, "spb"));
            mockMvc.perform(buildPostRequestWithContent(json)).andExpect(status().isBadRequest());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }


    @Test
    public void multiThreadingAddSensors() {
        sensorRepository.deleteAll();


        MyRunnable r1 = new MyRunnable(12.3456, 14.5623,
                random.nextInt(50) + random.nextFloat(), "Saint-Petersburg");
        MyRunnable r2 = new MyRunnable(15.3456, 22.5623,
                random.nextInt(20) + random.nextFloat(), "Kirov");

        userRepository.checkAndSaveUser(new User(r1.getCoord1() + "", passwordEncoder.encode(r1.getCoord2() + ""), Role.SENSOR));
        userRepository.checkAndSaveUser(new User(r2.getCoord1() + "", passwordEncoder.encode(r2.getCoord2() + ""), Role.SENSOR));

        r1.run();
        r2.run();

        int countK = 0;
        int countS = 0;
        long id = -1;
        assertEquals(200L, sensorRepository.count());
        for (Sensor s : sensorRepository.findAll()
        ) {
            switch (s.getCity()) {
                case "Kirov":
                    countK++;
                    assertEquals(15.3456, s.getCoord1());
                    assertEquals(22.5623, s.getCoord2());
                    assertTrue(s.getTemperature() < 100);
                    assertTrue(s.getTemperature() > 0);
                    assertTrue(id < s.getId());
                    id = s.getId();
                    break;
                case "Saint-Petersburg":
                    countS++;
                    assertEquals(12.3456, s.getCoord1());
                    assertEquals(14.5623, s.getCoord2());
                    assertTrue(s.getTemperature() < 100);
                    assertTrue(s.getTemperature() > 0);
                    assertTrue(id < s.getId());
                    id = s.getId();
                    break;
                default:
                    fail();

            }

        }
        assertEquals(100, countK);
        assertEquals(100, countS);
    }

    @Test
    public void checkingRandomSensors() {
        sensorRepository.deleteAll();
        userRepository.deleteAll();
        //List<MyRunnable> runnables = new ArrayList<>();
        long id = -1;
        int count = 20;
        for (int i = 0; i < 20; i++) {
            MyRunnable tmp = new MyRunnable();
            //runnables.add(tmp);
            try {
                userRepository.checkAndSaveUser(new User(tmp.getCoord1() + "", passwordEncoder.encode(tmp.getCoord2() + ""), Role.SENSOR));
                LOGGER.info(() -> "user added");
            } catch (DuplicateDataException e) {
                count--;
            }
            tmp.run();
        }

        assertEquals(count, userRepository.count());
        for (Sensor s : sensorRepository.findAll()
        ) {
            switch (s.getCity()) {
                case "A":
                case "B":
                case "C":
                case "D":
                case "E":
                case "F":
                case "G":
                case "AA":
                case "BB":
                case "CC":
                case "DD":
                case "EE":
                    assertTrue(Math.abs(s.getCoord1()) <= 90);
                    assertTrue(Math.abs(s.getCoord2()) <= 180);
                    assertTrue(s.getTemperature() < 100);
                    assertTrue(s.getTemperature() > 0);
                    assertTrue(id < s.getId());
                    id = s.getId();
                    break;

                default:
                    fail();

            }

        }
    }

    @Test
    public void checkingValidationNotOk() {
        try {
            mockMvc.perform(post("/login?usr=admin&pass=admin"));
            mockMvc.perform(post("/login?usr=sensor&pass=sensor"));
        } catch (Throwable t) {
            fail(t);
        }
        try {
            mockMvc.perform(post("/login?usr=admi&pass=admin")).andExpect(status().isForbidden());
            mockMvc.perform(post("/login?usr=admin&pass=admi")).andExpect(status().isForbidden());
            mockMvc.perform(post("/login?usr=sensor&pass=senso")).andExpect(status().isForbidden());
            mockMvc.perform(post("/login?usr=sen&pass=sensor")).andExpect(status().isForbidden());
            mockMvc.perform(post("/login?usr=hello&pass=world")).andExpect(status().isForbidden());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void checkingIncorrectMethod() {
        try {
            mockMvc.perform(get("/login?usr=admin&pass=admin")).andExpect(status().isMethodNotAllowed());
            mockMvc.perform(put("/login?usr=admin&pass=admin")).andExpect(status().isMethodNotAllowed());
            mockMvc.perform(delete("/login?usr=admin&pass=admin")).andExpect(status().isMethodNotAllowed());
            mockMvc.perform(patch("/login?usr=admin&pass=admin")).andExpect(status().isMethodNotAllowed());
            mockMvc.perform(post("/admin")).andExpect(status().isForbidden());
            mockMvc.perform(delete("/admin")).andExpect(status().isForbidden());
            mockMvc.perform(put("/admin")).andExpect(status().isForbidden());
            mockMvc.perform(patch("/admin")).andExpect(status().isForbidden());
            mockMvc.perform(put("/admin/readByCity")).andExpect(status().isForbidden());
            mockMvc.perform(delete("/admin/readByCity")).andExpect(status().isForbidden());
            mockMvc.perform(patch("/admin/readByCity")).andExpect(status().isForbidden());
            mockMvc.perform(post("/admin/readByCity")).andExpect(status().isForbidden());
            mockMvc.perform(get("/admin/hello")).andExpect(status().isForbidden());
            mockMvc.perform(get("/sensor")).andExpect(status().isForbidden());
            mockMvc.perform(put("/sensor")).andExpect(status().isForbidden());
            mockMvc.perform(delete("/sensor")).andExpect(status().isForbidden());
            mockMvc.perform(patch("/sensor")).andExpect(status().isForbidden());
            mockMvc.perform(post("/login")).andExpect(status().isBadRequest());
            mockMvc.perform(post("/login?usr=a")).andExpect(status().isBadRequest());
            mockMvc.perform(post("/login?pass=a")).andExpect(status().isBadRequest());
            String token = getTokenByLogin("admin", "admin");
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/notExist").
                    header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                    .contentType("application/json");
            mockMvc.perform(builder)
                    .andExpect(status().isNotFound());
            builder = MockMvcRequestBuilders.get("/admin/notExist").
                    header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                    .contentType("application/json");
            mockMvc.perform(builder)
                    .andExpect(status().isNotFound());


        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }

    }


    @Test
    @WithAnonymousUser
    public void checkingLoginAdminOk() {
        try {
            mockMvc.perform(post("/login?usr=admin&pass=admin"))

                    .andExpect(status().isOk()).andExpect(jsonPath("$.username", is("admin")));
            mockMvc.perform(post("/login?usr=sensor&pass=sensor"))

                    .andExpect(status().isOk()).andExpect(jsonPath("$.username", is("sensor")));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }

    private static String[] city = {"A", "B", "C", "D", "E", "F", "G", "AA", "BB", "CC", "DD", "EE"};

    private class MyRunnable implements Runnable {

        private double coord1;

        private double coord2;
        private float temperature;
        private String city;
        private int sleepTime;
        private int iteration;

        MyRunnable() {
            coord1 = random.nextInt(60) + random.nextDouble();
            coord2 = -180.5 + random.nextDouble();
            //coord2 = random.nextInt(120) + random.nextDouble();
            temperature = random.nextInt(85) + random.nextFloat();
            city = ControllersTest.city[random.nextInt(12)];
            sleepTime = 20 + random.nextInt(50);
            iteration = 50 + random.nextInt(30);
        }

        MyRunnable(double coord1, double coord2, float temperature, String city) {
            this.coord1 = coord1;
            this.coord2 = coord2;
            this.temperature = temperature;
            this.city = city;
            sleepTime = 100;
            iteration = 100;
        }

        @Override
        public void run() {

            for (int i = 0; i < iteration; i++) {
                try {
                    String json = gson.toJson(new SensorForm(coord1, coord2,
                            temperature, city));
                    MockHttpServletRequestBuilder builder = buildPostRequestWithContent(coord1 + "", coord2 + "", json);
                    LOGGER.info(() -> "First coordinate: " + this.coord1 + ", second coordinate: " + this.coord2 + ", temperature: " + this.temperature + ", city: " + this.city);
                    if (Math.abs(coord1) < 90 && Math.abs(coord2) < 180 && temperature < 100 && temperature > 0) {
                        MvcResult result = mockMvc.perform(builder)
                                .andExpect(status().isOk()).andReturn();

                        String data = result.getResponse().getContentAsString();
                        JsonSensorModel jsonSensorModel = gson.fromJson(data, JsonSensorModel.class);
                        assertEquals(coord1, jsonSensorModel.getCoord1());
                        assertEquals(coord2, jsonSensorModel.getCoord2());
                        assertEquals(temperature, jsonSensorModel.getTemperature());
                        assertEquals(city, jsonSensorModel.getCity());
                    } else {
                        mockMvc.perform(builder)
                                .andExpect(status().isBadRequest());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    fail(e);
                }
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

        double getCoord1() {
            return coord1;
        }

        double getCoord2() {
            return coord2;
        }


    }

    private String makeToken(String username, MvcResult result) throws UnsupportedEncodingException {
        String response = result.getResponse().getContentAsString();
        final String logResponse = response;
        response = response.replace("{\"username\":" + "\"" + username + "\"" + ",\"token\":\"", "");
        final String token = response.replace("\"}", "");
        LOGGER.info(() -> "response: " + logResponse + ", token: " + token);
        return token;
    }


    public String getTokenByLogin(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/login?usr=" + username + "&pass=" + password))
                .andExpect(status().isOk()).andExpect(jsonPath("$.username", is(username))).andReturn();
        return makeToken(username, result);
    }

    public MockHttpServletRequestBuilder buildPostRequestWithContent(String username, String password,
                                                                     String json) throws Exception {
        String token = getTokenByLogin(username, password);
        return MockMvcRequestBuilders.post("/sensor")
                .header(HEADER_AUTH_NAME, TOKEN_NAME + token)
                .contentType("application/json").content(json);
    }

    private MockHttpServletRequestBuilder buildPostRequestWithContent(String json) throws Exception {
        return buildPostRequestWithContent("sensor", "sensor", json);
    }
}