package com.github.senyast4745.controller;

import com.github.senyast4745.db.SensorRepository;
import com.github.senyast4745.db.UserRepository;
import com.github.senyast4745.model.ExceptionModel;
import com.github.senyast4745.model.Sensor;
import com.github.senyast4745.model.SensorsArrayModel;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.http.ResponseEntity.ok;


@RestController
public class MainController {

    private static final double DISTANCE = 0.667; // 1 degree is 111,1 km, 4 minutes to double, 1 minutes is 1,851 km

    private SensorRepository repository;
    private ArrayDeque<Sensor> queueLast;
    private Gson gson = new Gson();

    @Autowired
    public MainController(SensorRepository repository) {
        this.repository = repository;
        queueLast = new ArrayDeque<>(10);
    }

    @RequestMapping("/")
    @ResponseBody
    public String welcome() {
        return "Welcome.";
    }


    @RequestMapping(value = "/sensor", method = RequestMethod.POST)
    @PreAuthorize("hasAuthority('SENSOR')")
    public @ResponseBody
    ResponseEntity add(@RequestBody SensorForm data) {
        if (Math.abs(data.getCoord1()) <= 90 && Math.abs(data.getCoord2()) <= 180 && data.getTemp() > 0 && data.getTemp() < 100) {
            Sensor tmp = new Sensor(new double[]{data.getCoord1(), data.getCoord2()}, data.getTemp(), data.getCityName());
            queueLast.addLast(tmp);
            if (queueLast.size() > 10) {
                queueLast.removeFirst();
            }
            return ok(repository.save(tmp));
        } else return ResponseEntity.badRequest().body(gson.toJson(new ExceptionModel(400, "Bad Request",
                "Bad Request with: " + gson.toJson(data), "/sensor")));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity showAll() {
        if (repository.count() <= 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(gson.toJson(new ExceptionModel(HttpStatus.CONFLICT.value(), "Conflict", "Repository of sensors is empty", "/sensor")));
        }
        if ((repository.count() > 10 && queueLast.size() != 10) ||
                (repository.count() <= 10 && queueLast.size() < repository.count())) {
            for (Sensor sensor : repository.findAll()) {
                queueLast.addLast(sensor);
                if (queueLast.size() > 10) {
                    queueLast.removeFirst();
                }
            }
        }
        return ok(new SensorsArrayModel(queueLast.toArray()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/findCity", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity readByCity(@RequestParam double coord1, double coord2) {
        if (repository.count() <= 0) {
            return ResponseEntity.badRequest().body(gson.toJson(new ExceptionModel(400, "Bad Request",
                    "Sensor repository is empty", "/admin/findCity")));
        }

        //longitude variation
        double stepV = DISTANCE;

        //latitude variation (1 degree = cos(longitude) * 111,1) -> variation = DISTANCE/|cos(longitude)|
        //111,1 km - 1 degree of longitude
        double stepH = (DISTANCE / Math.abs(Math.cos(coord1 * Math.PI / 180)));

        final String[] resultCity = new String[1];
        repository.findAll().forEach((s) -> {
            if (s.getCoord1() > coord1 - stepH
                    && s.getCoord1() < coord1 + stepH
                    && s.getCoord2() > coord2 - stepV
                    && s.getCoord2() < coord2 + stepV) {
                resultCity[0] = s.getCity();
            }

        });

        Iterable<Sensor> iter = repository.findByCity(resultCity[0]);

        if (iter.iterator().hasNext()) {
            ArrayDeque<Sensor> tmp = new ArrayDeque<>();
            iter.forEach((s) -> {
                tmp.addLast(s);
                if (tmp.size() > 10) {
                    tmp.removeFirst();
                }
            });
            return ok(new SensorsArrayModel(tmp.toArray()));
        } else {
            return ResponseEntity.badRequest().body(gson.toJson(new ExceptionModel(400, "Bad Request",
                    "Sensor with this coordinates " + coord1 + " " + coord2 + " not exists",
                    "/admin/findCity")));
        }
    }


}
