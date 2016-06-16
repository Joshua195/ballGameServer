package servidorUDP;

import com.mainUDP.Constantes;
import com.mainUDP.Datos;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Servidor {
    public static void main(String[] args) throws IOException {
        Map<Integer,Datos> clientes = new HashMap<>();
        DatagramSocket socket = new DatagramSocket(Constantes.PUERTO_DEL_SERVIDOR);
        System.out.println("Servidor activo.");
        while(true){
            DatagramPacket recibido = new DatagramPacket(new byte[1024], 1024);
            System.out.println("Esperando...");
            socket.receive(recibido);
            ServerThread serverThread = new ServerThread(recibido,socket,clientes);
            serverThread.start();
            //HiloServer hiloServer = new HiloServer(recibido,s,pelotas);
            //hiloServer.start();
        }
    }
}
