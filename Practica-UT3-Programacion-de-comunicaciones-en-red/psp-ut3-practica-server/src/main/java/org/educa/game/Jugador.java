package org.educa.game;

/**
 * @author Sergio Gonzalez y Antonio Leon
 * Clase creada para guardar los datos del jugador
 */
public class Jugador {
    private String nombre; // Nombre del jugador
    private String direccion; // Dirección, por defecto va a ser "Localhost"
    private int puerto; // Puerto que se va a utilizar
    private boolean enPartida; // Determina si el jugador está en partida true y si no lo está false

    /**
     * Constructor de jugador
     * @param nombre recibe el nombre del jugador
     * @param direccion recibe la direccion (por defecto localhost)
     * @param puerto recibe el puerto al que se ha de conectar
     */
    public Jugador(String nombre, String direccion, int puerto) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.puerto = puerto;
        this.enPartida = false;
    }

    /**
     * Método para conseguir el nombre del jugador
     * @return devuelve el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Recibe un nombre para cambiarlo
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Método para conseguir la direccion del jugador
     * @return devuelve la direccion
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Recibe una dirección para cambiarla
     * @param direccion
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Método para conseguir el puerto del jugador
     * @return devuelve el puerto
     */
    public int getPuerto() {
        return puerto;
    }

    /**
     * Recibe un puerto para cambarlo
     * @param puerto
     */
    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    /**
     * Comprueba ei el jugador está en partida
     * @return true si esta en partida, false si no
     */
    public boolean isEnPartida() {
        return enPartida;
    }

    /**
     * Cambia el estado del jugador, si esta en partida o no
     * @param enPartida
     */
    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
    }
}
