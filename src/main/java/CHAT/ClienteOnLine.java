package CHAT;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClienteOnLine extends WindowAdapter {
    LaminaMarcoCliente m;

    public ClienteOnLine(LaminaMarcoCliente l) {
        m = l;
    }

    @Override
    public void windowOpened(WindowEvent e) {
        super.windowOpened(e);

        //Envio de paquete ONLINE...

        try {
            Socket miSocketOnLine = new Socket("192.168.18.3", 9999);
            //Se obtiene la ip actual del cliente y se envia al servidor en PaquetEnvio datosOnLine
            InetAddress ia = miSocketOnLine.getInetAddress();
            String ipRemota = ia.getLocalHost().getHostAddress();
            PaqueteEnvio datosOnLine = new PaqueteEnvio();
            datosOnLine.setNick(m.lblNick.getText());
            datosOnLine.setIP(ipRemota);
            datosOnLine.setMensaje("ONLINE");
            ObjectOutputStream paqueteOnLine = new ObjectOutputStream(miSocketOnLine.getOutputStream());
            paqueteOnLine.writeObject(datosOnLine);
            miSocketOnLine.close();
        } catch (Exception excep) {
            excep.printStackTrace();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        super.windowOpened(e);

        //Envio de paquete OFFLINE...

        try {
            Socket miSocketOnLine = new Socket("192.168.18.3", 9999);
            //Se obtiene la ip actual del cliente y se envia al servidor en PaquetEnvio datosOnLine
            InetAddress ia = miSocketOnLine.getInetAddress();
            String ipRemota = ia.getLocalHost().getHostAddress();
            PaqueteEnvio datosOnLine = new PaqueteEnvio();
            datosOnLine.setNick(m.lblNick.getText());
            datosOnLine.setIP(ipRemota);
            datosOnLine.setMensaje("OFFLINE");
            ObjectOutputStream paqueteOnLine = new ObjectOutputStream(miSocketOnLine.getOutputStream());
            paqueteOnLine.writeObject(datosOnLine);
            miSocketOnLine.close();
        } catch (Exception excep) {
            excep.printStackTrace();
        }
    }

}
