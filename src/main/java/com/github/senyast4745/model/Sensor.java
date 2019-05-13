package com.github.senyast4745.model;

import javax.persistence.*;

@Entity
@Table(name = "data")
public class Sensor {

    @Id
    @Column(name = "data_id", unique = true, nullable = false)
    @GeneratedValue
    private long id;

    @Column(name = "coor1", nullable = false)
    private double coord1;

    @Column(name = "coor2", nullable = false)
    private double coord2;

    @Column(name = "temp", nullable = false)
    private float temperature;

    @Column(name = "city", nullable = false)
    private String city;

    public Sensor(){
        super();
    }

    public Sensor( double[] coords, float temperature, String city) {

        this.coord1 = coords[0];
        coord2 = coords[1];
        this.temperature = temperature;
        this.city = city;

    }

    public long getId() {
        return id;
    }

    public double getCoord1() {
        return coord1;
    }

    public void setCoord1(double coord1) {
        this.coord1 = coord1;
    }

    public double getCoord2() {
        return coord2;
    }

    public void setCoord2(double coord2) {
        this.coord2 = coord2;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
