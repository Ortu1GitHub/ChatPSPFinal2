package CHAT;

import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Servidor {

    public static void main(String[] args) {

        MarcoServidor mimarco = new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}

class MarcoServidor extends JFrame implements Runnable {

    public MarcoServidor() {
        setBounds(1200, 300, 280, 350);
        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());
        taTexto = new JTextArea();
        milamina.add(taTexto, BorderLayout.CENTER);
        add(milamina);
        setVisible(true);
        Thread hiloServidor = new Thread(this);
        hiloServidor.start();
    }

    public JTextArea taTexto;

    @Override
    public void run() {
        //Se abre el puerto 9999 y se pone a la escucha

        try {
            ServerSocket socketServidor = new ServerSocket(9999);
            String nick, IP = null, mensaje;
            Map<String, String> listaIPS = new HashMap<>();
            PaqueteEnvio datosRecibidos;

            while (true) {
                //Socket para recibir de N clientes
                Socket miSocketServidor = socketServidor.accept();

                ObjectInputStream flujoEntrada = new ObjectInputStream(miSocketServidor.getInputStream());
                datosRecibidos = (PaqueteEnvio) flujoEntrada.readObject();
                nick = datosRecibidos.getNick();
                IP = datosRecibidos.getIP();
                mensaje = datosRecibidos.getMensaje();

                // Codigo para detectar clientes ONLINE y almacenarlas sus usuarios e ips...
                if (datosRecibidos.getMensaje().equals("ONLINE")) {
                    taTexto.append("\n" + nick + " " + mensaje + " con IP " + IP + "...");
                    listaIPS.put(nick, IP);
                    datosRecibidos.setListaIPS(listaIPS);

                    //Se envia la lista de IPs a todos los clientes...
                    for (Map.Entry<String, String> s : listaIPS.entrySet()) {
                        Socket enviaListaIps = new Socket(s.getValue(), 9090);
                        ObjectOutputStream paqueteSalidaListaIPs = new ObjectOutputStream(enviaListaIps.getOutputStream());
                        paqueteSalidaListaIPs.writeObject(datosRecibidos);
                        paqueteSalidaListaIPs.close();
                        enviaListaIps.close();
                    }

                } else {
                    taTexto.append("\n" + nick + " :" + mensaje + " para " + IP);
                    //Socket para enviar al cliente con Ip anterior
                    Socket enviaCliente = new Socket(IP, 9090);
                    ObjectOutputStream paqueteSalida = new ObjectOutputStream(enviaCliente.getOutputStream());
                    paqueteSalida.writeObject(datosRecibidos);

                    paqueteSalida.close();
                    enviaCliente.close();
                    miSocketServidor.close();
                }

            }

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}