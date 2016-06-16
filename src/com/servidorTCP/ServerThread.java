package com.servidorTCP;

import com.core.PackageObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

public class ServerThread extends Thread{
    private Socket socket;
    private Vector<PackageObject> clientes;


    private static int clientCounter = 0;

    public static String PREGUNTA_SI_TIENEN_PELOTA = "PREGUNTA_SI_TIENEN_PELOTA";
    public static String PASAR_PELOTA = "PASAR_PELOTA";


    public synchronized int getNextClientCounter(){
        return ++clientCounter;
    }



    public ServerThread(Socket socket, Vector<PackageObject> clientes){
        this.socket = socket;
        this.clientes = clientes;
    }

    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            PackageObject packageObject = (PackageObject)objectInputStream.readObject();
            PackageObject oldInformation = null;
            //detecta cliente y obtien informacion del cliente
            if(packageObject.getIndetificador() == 0){
                packageObject.setIndetificador(getNextClientCounter());
                packageObject.setPelotaActiva(false);
                clientes.add(packageObject);
                oldInformation = packageObject;
            }else {
                oldInformation = getClientInfo(packageObject.getIndetificador());
            }

            if(PREGUNTA_SI_TIENEN_PELOTA.equals(packageObject.getOperacion())) {
                //verificar si es el unico cliente
                if(clientes.size()==1) {
                    packageObject.setPelotaActiva(true);
                    packageObject.setStatus(1);
                }else if(clientes.size() > 1 && !oldInformation.isPelotaActiva()) {
                    //no es el unico cliente y la variable de pelataActiva is false
                    packageObject.setPelotaActiva(false);
                    packageObject.setStatus(2);
                }else if(clientes.size() > 1 && oldInformation.isPelotaActiva()) {
                    //no es el unico cliente y la variable de pelataActiva is true
                    packageObject.setPelotaActiva(true);
                    packageObject.setStatus(1);
                }

            }else if(PASAR_PELOTA.equals(packageObject.getOperacion())) {
                //voy a pasar la bola
                pasarPelota(packageObject);
                //regreso que que se completo el traspaso

            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(packageObject);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private PackageObject getClientInfo(int identificador){
        for(PackageObject object : clientes){
            if(object.getIndetificador() == identificador){
                return object;
            }
        }
        return null;
    }

    private void pasarPelota(PackageObject actualDeunoPelota){
        int actualDeunoPelotaIndice = 0;
        int nextDuenoIndice = 0;
        for(int indice = 0; indice < clientes.size(); indice++){
            if(clientes.get(indice).getIndetificador() == actualDeunoPelota.getIndetificador()){
                actualDeunoPelotaIndice = indice;
                clientes.get(indice).setPelotaActiva(false);
                actualDeunoPelota.setPelotaActiva(false);
                break;
            }
        }

        if(actualDeunoPelotaIndice+1 >= clientes.size()){
            nextDuenoIndice = 0;
        }else{
            nextDuenoIndice = actualDeunoPelotaIndice + 1;
        }
        clientes.get(nextDuenoIndice).setPelotaActiva(true);
    }
}
