package org.educa.game;

import java.io.*;
import java.net.Socket;
import java.sql.SQLOutput;

/**
 * @author Sergio Gonzalez y Antonio Leon
 * Clase que se encarga de resolver la peticion del jugador (cliente)
 */
public class Invitacion implements Runnable{
    Socket socket;
    Server server;

    /**
     * Constructor de invitación, recibe socket y server
     * @param socket que se conecta a jugadores
     * @param server Objeto del server para evitar los metodos estaticos
     */
    Invitacion(Socket socket, Server server){
        this.socket=socket;
        this.server=server;
    }

    /**
     * Metodo run para iniciar la invitación
     */
    @Override
    public void run() {
        StringBuilder datos = new StringBuilder();
        try(
            PrintWriter envioInfo = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            BufferedReader reciboInfo= new BufferedReader(new InputStreamReader(socket.getInputStream()))){
            String msg = reciboInfo.readLine();

            //Cuando se crea partida se asigna a los jugadores una sala, se les confiere la info de su rival, su status en la partida y se corta comunicacion
            if("empiezo".equalsIgnoreCase(msg)){
                envioInfo.println("ok");
                envioInfo.flush();
                //---
                msg=reciboInfo.readLine();
                String [] apuntes = msg.split(",");
                if ("dados".equalsIgnoreCase(apuntes[0])) {
                    empezarPartidaDados(apuntes, datos, envioInfo);
                } else{
                    System.out.println("Aquí hirían otros juegos si los hubiera.");
                }
            }

            //Termina la partida (Solo conexion entre server anfitrion)
            if ("termino".equalsIgnoreCase(msg)) {
                terminarPartidaDados(envioInfo, reciboInfo);
            }
        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    /**
     * Metodo que se encarga de recoger los datos del ganador y acabar la partida.
     * @param envioInfo Objeto que se encarga de enviar la informacion al cliente
     * @param reciboInfo Onjeto que se encarga de recibir la info del cliente
     */
    private void terminarPartidaDados(PrintWriter envioInfo, BufferedReader reciboInfo) throws IOException {
        String msg;
        envioInfo.println("ok");
        envioInfo.flush();
        msg = reciboInfo.readLine();
        String[] apuntes = msg.split(",");

        if ("dados".equalsIgnoreCase(apuntes[2])) {
            Partida partida = server.creaccionDePartida(Integer.parseInt(apuntes[0]));
            server.acabarPartida(Integer.parseInt(apuntes[0]), apuntes[1]);
            System.out.println(partida);
            server.eliminarPartidas(Integer.parseInt(apuntes[0]));
        } else {
            System.out.println("Se cerraria otro juego que no esta implementado.");
        }
    }

    /**
     * Metodo que realiza las funciones de recogida y envio de datos entre el servidor y los jugadores.
     * @param apuntes Datos sobre los jugadores
     * @param datos Datos que se enviaran a los jugadores
     * @param envioInfo El mecanismo que enviara la informacion (PrintWriter)
     */
    private void empezarPartidaDados(String[] apuntes, StringBuilder datos, PrintWriter envioInfo) {
        Jugador jugador = new Jugador(apuntes[1],
                server.DIRECCION,
                Integer.parseInt(apuntes[2]));
        server.getSala().getListaJugadores().add(jugador);
        server.comprobarPartida(jugador);
        Partida partida = server.creaccionDePartida(server.obtenerIdPartida(jugador));

        while (partida.getJugador1() == null || partida.getJugador2() == null) {
            partida = server.creaccionDePartida(server.obtenerIdPartida(jugador));
        }

        Jugador oponente;
        if (partida.getJugador1().getNombre().equalsIgnoreCase(jugador.getNombre())) {
            oponente = partida.getJugador2();
        } else {
            oponente = partida.getJugador1();
        }
        //Recibimos una String; nickNameOponente, host, puerto, anfitrion, idPartida
        datos.append(oponente.getNombre()).append(",").append(oponente.getDireccion()).append(",").append(5555 + server.obtenerIdPartida(jugador)).append(",");

        if (server.comprobarNumJugador(jugador)) {
            datos.append("anfitrion").append(",");
        } else {
            datos.append("invitado").append(",");
        }
        datos.append(server.obtenerIdPartida(jugador));
        envioInfo.println(datos.toString());
        envioInfo.flush();
        System.out.println("Se envian los datos de "+oponente.getNombre()+" a "+jugador.getNombre());
    }
}
