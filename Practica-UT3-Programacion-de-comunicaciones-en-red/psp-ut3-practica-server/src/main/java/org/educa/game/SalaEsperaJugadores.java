package org.educa.game;


import java.util.ArrayList;

/**
 * Clase que se utiliza para crear la sala de espera de los jugadores antes de empezar la partida
 */
public class SalaEsperaJugadores {

    private ArrayList <Jugador> listaJugadores = new ArrayList<>(); // Lista de jugadores

    /**
     * Consigue los jugadores de la lista de la sala de Espera
     * @return listaJugadores
     */
    public ArrayList<Jugador> getListaJugadores() {
        return listaJugadores;
    }

    /**
     * Modifica la lista de jugadores de la Sala de espera
     * @param listaJugadores
     */
    public void setListaJugadores(ArrayList<Jugador> listaJugadores) {
        this.listaJugadores = listaJugadores;
    }
}
