package com.mainTCP;

import com.core.PackageObject;
import com.servidorTCP.ServerThread;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

public class MainDer extends JComponent{

    private final static int ANCHO = 512;
    private final static int ALTO = 384;
    private final static int DIAMETRO = 20;
    private float x;
    private float vx;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private static boolean escuchando = true;

    PackageObject objectData;


    private String ip;
    private int port;

    public MainDer(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;

        objectData = new PackageObject();
        objectData.setPelotaActiva(false);


        Thread thread = new Thread(new PregutaPorPelota(ip,port));
        thread.start();
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

    /*public void enviarPosiciones(String posiciones){
        try {
            objectOutputStream.writeObject(posiciones);
        } catch (IOException e) {
            System.out.println("lol");
        }
    }*/

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
        if(!escuchando) {

            x += vx * dt;
            if (vx > 0 && x + DIAMETRO >= ANCHO) {
                vx = -vx;
            } else if (vx < 0 && x + (DIAMETRO * 2) <= 0) {

                x = 0;
                vx = 200;


                Thread thread = new Thread(new PasarPelota(ip, port));
                thread.start();

            }
        }
    }

    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame("Derecha");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        int port = 5555;
        String ip = "127.0.0.1";

        MainDer demo1 = new MainDer(ip,port);
        jf.getContentPane().add(demo1);
        jf.pack();
        jf.setVisible(true);
        demo1.cicloPrincipalJuego();
    }





    private class PasarPelota implements Runnable{

        String ip;
        int port;

        public PasarPelota(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            Socket socket = null;
            try {


                socket = new Socket(ip, port);
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());


                objectData.setOperacion(ServerThread.PASAR_PELOTA);
                objectOutputStream.writeObject(objectData);

                objectInputStream = new ObjectInputStream(socket.getInputStream());
                objectData = (PackageObject) objectInputStream.readObject();

                if(objectData.getStatus() == 3){
                    System.out.println("la pelota ya fue pasada");
                }

            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }finally {
                if(socket != null){
                    try {
                        socket.close();
                    }catch (IOException ex){
                        System.out.println("Error Cerrando el Socket");
                    }
                }
            }

            Thread thread = new Thread(new PregutaPorPelota(ip,port));
            thread.start();
        }
    }

    private class PregutaPorPelota implements Runnable{
        String ip;
        int port;

        public PregutaPorPelota(String ip, int port){
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            System.out.println("Asking for the ball");
            Socket socket = null;
            while (true) {
                try {

                    escuchando = true;
                    socket = new Socket(ip, port);
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

                    System.out.println("Writing....");
                    objectData.setOperacion(ServerThread.PREGUNTA_SI_TIENEN_PELOTA);
                    objectOutputStream.writeObject(objectData);
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    System.out.println("Reading....");
                    objectData = (PackageObject) objectInputStream.readObject();

                    if (objectData.getStatus() == 1) {
                        break;
                    }
                    System.out.println("No Tengo la pelota");
                    Thread.sleep(500);
                }catch(ClassNotFoundException | IOException | InterruptedException e){
                    System.out.println(e.getMessage());
                }finally{
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException ex) {
                            System.out.println("Error Cerrando el Socket");
                        }
                    }
                }
            }


            System.out.println("Tengo la pelota");
            //hacer que pinte la pelota
            escuchando = false;
        }
    }
}