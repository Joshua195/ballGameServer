package com.mainUDP;


public class Datos {
    private Integer puerto;
    private Float x;

    public Datos(Integer puerto){
        this.puerto = puerto;
        this.x = 0f;
    }

    public Integer getPuerto() {
        return puerto;
    }

    public void setPuerto(Integer puerto) {
        this.puerto = puerto;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }
}
