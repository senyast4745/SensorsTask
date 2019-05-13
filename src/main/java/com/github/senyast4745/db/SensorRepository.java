package com.github.senyast4745.db;

import com.github.senyast4745.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SensorRepository extends CrudRepository<Sensor, Long> {
    Iterable<Sensor> findByCity(String city);
}
