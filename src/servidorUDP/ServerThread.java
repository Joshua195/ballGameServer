package servidorUDP;

import com.mainUDP.Datos;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

public class ServerThread extends Thread{
    DatagramPacket datagramPacket;
    DatagramSocket datagramSocket;
    private static Vector<DatagramPacket> clientes;
    Map<Integer, Datos> clientesGlobales;


    public ServerThread(DatagramPacket datagramPacket,DatagramSocket datagramSocket, Map<Integer,Datos> clientesGlobales){
        this.clientesGlobales = clientesGlobales;
        this.datagramPacket = datagramPacket;
        this.datagramSocket = datagramSocket;
    }

    public void run() {
        System.out.println("A llegado una peticion...");
        Datos datosUser = (Datos) bytesParceObj(datagramPacket.getData());
        Datos datosExistentes = clientesGlobales.get(datosUser.getPuerto());

        if (datosExistentes == null){
            clientesGlobales.put(datosUser.getPuerto(),datosUser);
        }else {
            datosExistentes.setX(datosUser.getX());
        }

        ArrayList<Datos> datosEnviar = new ArrayList<>(clientesGlobales.values());
        for (int i = 0; i > datosEnviar.size(); i++){
            if (datosEnviar.get(i).equals(datosUser)){
                datosEnviar.remove(datosEnviar.get(i));
                break;
            }
        }

        byte datos[] = objParceBytes(datosEnviar);
        DatagramPacket packet = new DatagramPacket(datos,datos.length,datagramPacket.getAddress(),datagramPacket.getPort());
        try {
            datagramSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Peticion Servida");
    }

    public Object bytesParceObj(byte[] bytes){
        Object object = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream;
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    public byte[] objParceBytes(Object object){
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
            bytes =  byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }
}
