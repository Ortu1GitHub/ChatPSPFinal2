package CHAT;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MarcoCliente mimarco = new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {

    public MarcoCliente() {
        setBounds(600, 300, 280, 350);
        LaminaMarcoCliente milamina = new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        addWindowListener(new ClienteOnLine(milamina));
    }
}

class LaminaMarcoCliente extends JPanel implements ActionListener, Runnable {

    // Agrega un al para almacenar el historial de mensajes
    private ArrayList<String> historialMensajes = new ArrayList<>();

    public LaminaMarcoCliente() {
        nickUsuario = JOptionPane.showInputDialog("Indique su nick...");
        lblNickCadena = new JLabel("NICK :");
        add(lblNickCadena);
        lblNick = new JLabel();
        lblNick.setText(nickUsuario);
        add(lblNick);
        JLabel texto = new JLabel("ONLINE: ");
        add(texto);
        cbOnline = new JComboBox();
        add(cbOnline);
        taCliente = new JTextArea(12, 20);
        add(taCliente);
        tfTexto = new JTextField(20);
        add(tfTexto);
        btnEnviar = new JButton("Enviar");
        btnEnviar.addActionListener(this);
        add(btnEnviar);

        Thread hiloCliente = new Thread(this);
        hiloCliente.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Agrega el mensaje al historial
        String mensaje = tfTexto.getText();
        historialMensajes.add("Yo: " + mensaje);

        taCliente.append("\n" + "Yo: " + mensaje);

        if (e.getSource() == this.btnEnviar) {

            try {
                Socket socketCliente = new Socket("192.168.18.3", 9999);
                PaqueteEnvio datos = new PaqueteEnvio();
                datos.setNick(lblNick.getText().trim());
                datos.setIP(lisaIPsMenu.get(cbOnline.getSelectedItem().toString()));
                datos.setMensaje(tfTexto.getText().trim());

                ObjectOutputStream flujoSalida = new ObjectOutputStream((socketCliente.getOutputStream()));

                flujoSalida.writeObject(datos);
                socketCliente.close();

                // Limpia el JTextField después de enviar el mensaje
                tfTexto.setText("");

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                throw new RuntimeException(ex);
            }

        }
    }

    public JTextField tfTexto;
    public JComboBox cbOnline;
    public JLabel lblNick, lblNickCadena;
    public Map<String, String> lisaIPsMenu = new HashMap<>();
    public JButton btnEnviar;
    public JTextArea taCliente;

    public String nickUsuario;

    @Override
    public void run() {
        try {
            ServerSocket socketEscucha = new ServerSocket(9090);
            Socket socketClienteRecibe;
            PaqueteEnvio paqueteRecibido;
            while (true) {
                socketClienteRecibe = socketEscucha.accept();
                ObjectInputStream flujoEntrada = new ObjectInputStream(socketClienteRecibe.getInputStream());
                paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();

                if (paqueteRecibido.getMensaje().equals("ONLINE")||paqueteRecibido.getMensaje().equals("OFFLINE")) {
                    //Se carga la lista de IPs online en el combobox por usuario
                    lisaIPsMenu = paqueteRecibido.getListaIPS();
                    //Se borra el contenido del combobox antes de recargarlo para evitar duplicidades
                    cbOnline.removeAllItems();
                    //Se añade cada usuario online al menu desplegable...
                    for (Map.Entry<String, String> s : lisaIPsMenu.entrySet()) {
                        cbOnline.addItem(s.getKey());
                    }
                } else {
                    taCliente.append("\n" + paqueteRecibido.getNick() + " : " + paqueteRecibido.getMensaje());
                    String mensajeRecibido = paqueteRecibido.getNick() + " : " + paqueteRecibido.getMensaje();
                    //Se actualiza el historial de mensajes del cliente
                    historialMensajes.add(mensajeRecibido);
                }

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}