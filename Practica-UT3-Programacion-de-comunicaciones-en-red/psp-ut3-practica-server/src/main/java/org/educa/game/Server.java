package org.educa.game;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Sergio Gonzalez y Antonio Leon
 * Clase Server, recibe y controla todos los contactos de los jugadores para crear la partida.
 */
public class Server {
    public final String DIRECCION = "localhost"; // Dirección del servidor
    public final int PUERTO = 5555; // Puerto del servidor
    private SalaEsperaJugadores salaEsperaJugadores = new SalaEsperaJugadores(); // Se crea el objeto de Sala de Espera
    private ArrayList<Partida> listaPartida = new ArrayList<>(); // Se crea una lista de partidas

    /**
     * Metodo que va creado hilos segun le vayan entrando peticiones (jugadores)
     */
    public void run() {
        System.out.println("Creando socket servidor");
        Socket resolverPeticiones = null;
        try (ServerSocket serverSocket = new ServerSocket();) {
            InetSocketAddress addr = new InetSocketAddress(DIRECCION, PUERTO);
            serverSocket.bind(addr);
            System.out.println("Server encendido, esperando conexiones...");

            while (true) {
                resolverPeticiones = serverSocket.accept();
                System.out.println("Conexión recibida; entra un nuevo Jugador.");
                Invitacion invitacion = new Invitacion(resolverPeticiones, this);
                Thread hilo = new Thread(invitacion);
                hilo.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cerrarSockets(resolverPeticiones);
        }
    }

    /**
     * Cierra el socket del server.
     * @param resolverPeticiones el socket que se va a cerrar
     */
    private static void cerrarSockets(Socket resolverPeticiones) {
        try {
            if (resolverPeticiones != null) { //Si existe lo cierras.
                resolverPeticiones.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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
     *
     * @return listaPartida
     */
    public synchronized ArrayList<Partida> getListaPartida() {
        return listaPartida;
    }

    /**
     * Comprueba la primera partida que no tenga 2 jugadores e introduce al jugador,
     * en caso de que no haya partidas libres, crea una nueva partida,
     * se mete al jugador y se añade a la lista de partidas
     * @param jugador recibe un jugador
     */
    public synchronized void comprobarPartida(Jugador jugador) {
        if (!getListaPartida().isEmpty()) {
            boolean hueco = false;
            for (int i = 0; i < listaPartida.size() && !hueco; i++) {
                if (listaPartida.get(i).getJugador2() == null) {
                    listaPartida.get(i).setJugador2(jugador);
                    jugador.setEnPartida(true);
                }
            }
            if (!hueco && !jugador.isEnPartida()) {
                Partida partida = new Partida();
                partida.setJugador1(jugador);
                jugador.setEnPartida(true);
                getListaPartida().add(partida);
            }
        } else {
            if (!jugador.isEnPartida()) {
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
    public synchronized boolean comprobarNumJugador(Jugador jugador) {
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
    public synchronized int obtenerIdPartida(Jugador jugador) {
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
     * Metodo que a partir de un ID te devuelve una partida.
     * @param idPartida El id de la partida que buscamos.
     * @return La partida.
     */
    public synchronized Partida creaccionDePartida(int idPartida) {
        for (Partida partida : listaPartida) {
            if (partida.getId() == idPartida) {
                return partida;
            }
        }
        return null;
    }

    /**
     * Metodo que finaliza una partida declarando el ganador de esta.
     * @param idPartida ID de la partida a acabar
     * @param resultado Resultado para saber quien gano.
     */
    public synchronized void acabarPartida(int idPartida, String resultado) {
        for (Partida partida : listaPartida) {
            if (partida.getId() == idPartida) {
                if ("V".equalsIgnoreCase(resultado)) {
                    partida.setGanador("Anfitrion");
                } else {
                    partida.setGanador("Invitado");
                }
            }
        }
    }

    /**
     * Metodo que elimina la partida de la memoria una vez esta esta acabada.
     * @param id El id de la partida a ser eliminada.
     */
    public synchronized void eliminarPartidas(int id) {
        for (int i = 0; i < listaPartida.size(); i++) {
            if (listaPartida.get(i).getId() == id) {
                listaPartida.remove(i);
            }
        }
    }
}
