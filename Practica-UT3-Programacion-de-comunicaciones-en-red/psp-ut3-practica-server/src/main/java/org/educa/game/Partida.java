package org.educa.game;

import java.util.ArrayList;

public class Partida {
    private Jugador jugador1, jugador2; // Se crean dos objetos jugadores, que van a ser el numero de jugadores en este typeGame
    private String ganador; // Variable que determina si es ganador "V", perdedor "P", o ha habido un empate "E"
    private int id; // Identificador de la partida
    static int cont = 0; // Contador autoincrementable para el ID

    /**
     * Constructor de partida, cada partida tiene un ID diferente
     */
    Partida(){
        cont++;
        id=cont;
    }

    /**
     * Consigue el id de la partida
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Consigue el objeto Jugador 1 de la partida
     * @return jugador
     */
    public Jugador getJugador1() {
        return jugador1;
    }

    /**
     * Modifica jugador 1 de la partida
     * @param jugador1
     */
    public void setJugador1(Jugador jugador1) {
        this.jugador1 = jugador1;
    }
    /**
     * Consigue el objeto Jugador 2 de la partida
     * @return jugador
     */
    public Jugador getJugador2() {
        return jugador2;
    }

    /**
     * Modifica judador 2 de la partida
     * @param jugador2
     */
    public void setJugador2(Jugador jugador2) {
        this.jugador2 = jugador2;
    }

    /**
     * Consigue el ganador de la partida
     * @return ganador
     */
    public String getGanador() {
        return ganador;
    }

    /**
     * modifica el ganador de la partida
     * @param ganador
     */
    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    @Override
    public String toString() {
        return "Partida{" +
                "jugador1=" + jugador1.getNombre() +
                ", jugador2=" + jugador2.getNombre() +
                ", ganador='" + ganador + '\'' +
                ", id=" + id +
                '}';
    }
}
