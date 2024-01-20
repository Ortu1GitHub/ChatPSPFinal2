package CHAT;


import java.io.Serializable;
import java.util.Map;

public class PaqueteEnvio implements Serializable {
    public static final long serialVersionUID = 1L;
    public String nick, IP, mensaje;

    public Map<String, String> listaIPS;

    public PaqueteEnvio() {

    }

    public String getIP() {
        return IP;
    }

    public String getNick() {
        return nick;
    }

    public String getMensaje() {
        return mensaje;
    }

    public Map<String, String> getListaIPS() {
        return listaIPS;
    }

    public void setListaIPS(Map<String, String> e) {
        listaIPS = e;
        return;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}