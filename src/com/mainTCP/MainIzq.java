package com.mainTCP;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.*;

public class MainIzq extends JComponent implements Runnable{

    private final static int ANCHO = 512;
    private final static int ALTO = 384;
    private final static int DIAMETRO = 20;
    private float x;
    private float vx;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private static boolean escuchando = false;
    Thread thread;

    public MainIzq(Socket socket) throws IOException {
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        thread = new Thread(this);
        thread.start();
        setPreferredSize(new Dimension(ANCHO, ALTO));
        x = 10;
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

    public void enviarPosiciones(String posiciones) throws IOException {
        objectOutputStream.writeObject(posiciones);
        objectOutputStream.flush();
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
        if (vx > 0 && x >= ANCHO + DIAMETRO) {
            float pos = x - ANCHO;
            System.out.println(pos);
            String posiciones = String.valueOf(pos);
            enviarPosiciones(posiciones);
            escuchando = true;
            vx = -vx;
        }else if (vx < 0 && x + (DIAMETRO*2) <= 0){
            vx = -vx;
        }
    }

    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame("Izquierda");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setResizable(false);
        int port = 5555;
        String ip = "127.0.0.1";
        Socket socket = new Socket(ip,port);
        System.out.println("si paso");
        MainIzq demo1 = new MainIzq(socket);
        jf.getContentPane().add(demo1);
        jf.pack();
        jf.setVisible(true);
        demo1.cicloPrincipalJuego();
    }

    @Override
    public void run() {
        try {
            while (true) {
                while (escuchando) {
                    Object object = objectInputStream.readObject();
                    if (object instanceof String) {
                        String posicion = (String) object;
                        x = Float.parseFloat(posicion);
                        escuchando = false;
                    }
                }
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}