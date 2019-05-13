package com.github.senyast4745.testModel;

import com.google.gson.annotations.SerializedName;

public class JsonSensorsArrayModel {

    @SerializedName("sensors")
    private JsonSensorModel [] jsonSensorModels;

    public JsonSensorModel[] getJsonSensorModels() {
        return jsonSensorModels;
    }
}
