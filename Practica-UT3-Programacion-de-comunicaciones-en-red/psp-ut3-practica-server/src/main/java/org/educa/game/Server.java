package org.educa.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Clase Server, recibe y controla todos los contactos de los jugadores para crear la partida
 */
public class Server {
    public final String DIRECCION = "localhost"; // Dirección del servidor
    public final int PUERTO = 5555; // Puerto del servidor
    private SalaEsperaJugadores salaEsperaJugadores=new SalaEsperaJugadores(); // Se crea el objeto de Sala de Espera
    private ArrayList <Partida> listaPartida = new ArrayList<>(); // Se crea una lista de partidas

    public void run() {
        System.out.println("Creando socket servidor");
        try(ServerSocket serverSocket= new ServerSocket();) {
            InetSocketAddress addr = new InetSocketAddress(DIRECCION,PUERTO);
            serverSocket.bind(addr);
            System.out.println("Se han aceptado las conexiones");
            while (true) {
                Socket resolverPeticiones = serverSocket.accept();
                System.out.println("Conexión recibida");
                Invitacion invitacion = new Invitacion(resolverPeticiones, this);
                Thread hilo = new Thread(invitacion);
                hilo.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Consigue la sala de espera de los jugadores
     * @return SalaEsperaJugadores
     */
    public synchronized SalaEsperaJugadores getSala() {
        return salaEsperaJugadores;
    }

    /**
     * consigue la lista de partida
     * @return listaPartida
     */
    public synchronized ArrayList<Partida> getListaPartida(){
        return listaPartida;
    }

    /**
     * Comprueba la primera partida que no tenga 2 jugadores e introduce al jugador,
     * en caso de que no haya partidas libres, crea una nueva partida,
     * se mete al jugador y se añade a la lista de partidas
     * @param jugador recibe un jugador
     */
    public synchronized void comprobarPartida(Jugador jugador){
        if(!getListaPartida().isEmpty()){
            boolean hueco = false;
            for (int i = 0; i < listaPartida.size()&&!hueco; i++) {
                if(listaPartida.get(i).getJugador2()==null){
                    listaPartida.get(i).setJugador2(jugador);
                    jugador.setEnPartida(true);
                }
            }
            if(!hueco&&!jugador.isEnPartida()){
                Partida partida = new Partida();
                partida.setJugador1(jugador);
                jugador.setEnPartida(true);
                getListaPartida().add(partida);
            }
        }else{
            if(!jugador.isEnPartida()) {
                Partida partida = new Partida();
                partida.setJugador1(jugador);
                jugador.setEnPartida(true);
                getListaPartida().add(partida);
            }
        }
    }

    /**
     * Busca al jugador en la partida
     * @param jugador recibe el jugador
     * @return boolean, devuelve True si es jugador1 y False si es jugador2
     */
    public synchronized boolean comprobarNumJugador(Jugador jugador){
        for (Partida partida : listaPartida) {
            if (partida.getJugador1().getNombre().equalsIgnoreCase(jugador.getNombre())) {
                return true;
            } else if (partida.getJugador2().getNombre().equalsIgnoreCase(jugador.getNombre())) {
                return false;
            }
        }
        return false;
    }

    /**
     * Busca el ID de la partida del jugador
     * @param jugador recibe el jugador
     * @return devuelve el ID de la partida, -1 si no lo encuentra en ninguna partida
     */
    public synchronized int obtenerIdPartida(Jugador jugador){
        for (Partida partida : listaPartida) {
            if (partida.getJugador1().getNombre().equalsIgnoreCase(jugador.getNombre())) {
                return partida.getId();
            } else if (partida.getJugador2().getNombre().equalsIgnoreCase(jugador.getNombre())) {
                return partida.getId();
            }
        }
        return -1;
    }

    /**
     * Comprueba si la partida a la que pertenece el jugador ya está completa (2 jugadores)
     * @param jugador recibe jugador
     * @return boolean, true si está llena, false si no está llena
     */
    public synchronized boolean comprobarPartidaLlena(Jugador jugador){
        for (Partida partida : listaPartida) {
            if ((jugador.getNombre().equalsIgnoreCase(partida.getJugador1().getNombre()) && partida.getJugador2() != null) ||
                    (jugador.getNombre().equalsIgnoreCase(partida.getJugador2().getNombre()) && partida.getJugador1() != null)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Partida creaccionDePartida(int idPartida){
        for (Partida partida : listaPartida) {
            if(partida.getId()== idPartida){
                return partida;
            }
        }
        return null;
    }

    public synchronized void acabarPartida(int idPartida, String resultado){
        for (Partida partida:listaPartida) {
            if(partida.getId()==idPartida){
                if("V".equalsIgnoreCase(resultado)){
                    partida.setGanador("Anfitrion");
                }else{
                    partida.setGanador("Invitado");
                }
            }
        }
    }

    public synchronized void eliminarPartidas(int id){
        for (int i = 0; i < listaPartida.size(); i++) {
            if(listaPartida.get(i).getId()==id){
                listaPartida.remove(i);
            }
        }
    }

}
