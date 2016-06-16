package com.core;

import java.io.Serializable;

public class PackageObject implements Serializable{

    private int indetificador;
    private boolean pelotaActiva;
    private String operacion;
    private int status;


    public int getIndetificador() {
        return indetificador;
    }

    public void setIndetificador(int indetificador) {
        this.indetificador = indetificador;
    }



    public boolean isPelotaActiva() {
        return pelotaActiva;
    }

    public void setPelotaActiva(boolean pelotaActiva) {
        this.pelotaActiva = pelotaActiva;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }



    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
