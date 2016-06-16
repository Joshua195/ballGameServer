package com.servidorTCP;

import com.core.PackageObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Servidor {
    Vector<PackageObject> clientes;
    public Servidor(int port){
        clientes = new Vector<>();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Servidor Activo...");
            System.out.println("Esperando Conexion...");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Conexion establecida " + socket.getInetAddress().toString());
                Thread thread = new ServerThread(socket,clientes);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) throws IOException {
        int port = 5555;
        Servidor servidor = new Servidor(port);
    }
}
