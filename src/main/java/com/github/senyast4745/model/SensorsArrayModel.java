package com.github.senyast4745.model;

public class SensorsArrayModel {
    private Object [] sensors;

    public SensorsArrayModel(Object[] sensors) {
        this.sensors = sensors;
    }

    public Object[] getSensors() {
        return sensors;
    }

    public void setSensors(Object[] sensors) {
        this.sensors = sensors;
    }
}
