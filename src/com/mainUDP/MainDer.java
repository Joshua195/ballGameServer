package com.mainUDP;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

public class MainDer extends JComponent implements Runnable{

    private final static int ANCHO = 512;
    private final static int ALTO = 384;
    private final static int DIAMETRO = 20;
    private float x;
    private float vx;
    private static volatile boolean escuchando = false;
    DatagramSocket socket;
    DatagramPacket packet;
    Datos datos;

    public MainDer(Datos datos) throws IOException {
        this.datos = datos;
        setPreferredSize(new Dimension(ANCHO, ALTO));
        x = 0;
        vx = 200;
    }

    @Override
    public void paint(Graphics g) {
        if (escuchando){
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, ANCHO, ALTO);
        }else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, ANCHO, ALTO);
            g.setColor(Color.RED);
            g.fillOval(Math.round(x), ALTO / 2,DIAMETRO, DIAMETRO);
        }
    }

    public void enviarPosiciones(String posiciones){

    }

    public void cicloPrincipalJuego() throws Exception {
        long tiempoViejo = System.nanoTime();
        while (true) {
            long tiempoNuevo = System.nanoTime();
            float dt = (tiempoNuevo - tiempoViejo) / 1000000000f;
            tiempoViejo = tiempoNuevo;
            fisica(dt);
            dibuja();
        }
    }

    private void dibuja() throws Exception {
        SwingUtilities.invokeAndWait(() -> paintImmediately(0, 0, ANCHO, ALTO));
    }

    private void fisica(float dt) throws IOException {
        x += vx * dt;
        if (vx > 0 && x + DIAMETRO >= ANCHO) {
            vx = -vx;
        }else if (vx < 0 && x + (DIAMETRO*2) <= 0){
            escuchando = true;
            //vx = -vx;
        }
    }

    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame("Derecha");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        int port = 5555;
        String ip = "127.0.0.1";
        Datos datos = new Datos(port);
        MainDer demo1 = new MainDer(datos);
        jf.getContentPane().add(demo1);
        jf.pack();
        jf.setVisible(true);
        demo1.cicloPrincipalJuego();
    }

    @Override
    public void run() {

    }

    public void envia(Datos posicion) throws IOException {
        InetAddress destination = null;
        byte[] datosEnviar;
        datosEnviar = objParceBytes(posicion);
        socket = new DatagramSocket(datos.getPuerto(), InetAddress.getByName(Constantes.HOST_DEL_CLIENTE));
        try {
            destination = InetAddress.getByName(Constantes.HOST_DEL_SERVIDOR);
        } catch (UnknownHostException uhe) {
            System.out.println("Host no encontrado: " + uhe);
        }
        packet = new DatagramPacket(datosEnviar, datosEnviar.length, destination, Constantes.PUERTO_DEL_SERVIDOR);
        socket.send(packet);
        System.out.println("Datos enviado.");
    }

    /*public void respuesta(){
        byte datosRecibir[] = new byte[1024];
        packet = new DatagramPacket(datosRecibir, datosRecibir.length);
        socket.receive(packet);
        ArrayList<Pelota> pelotasRespuesta;
        pelotasRespuesta = (ArrayList<Pelota>) bytesParceObj(packet.getData());
        pelotas = pelotasRespuesta;
        socket.close();
    }*/

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