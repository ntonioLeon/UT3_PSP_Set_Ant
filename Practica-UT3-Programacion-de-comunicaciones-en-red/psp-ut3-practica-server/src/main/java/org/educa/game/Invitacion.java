package org.educa.game;

import java.io.*;
import java.net.Socket;

public class Invitacion implements Runnable{
    Socket socket;
    Server server;

    /**
     * Constructor de invitación, recibe socket y server
     * @param socket
     * @param server
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
            if("empiezo".equalsIgnoreCase(msg)){
                envioInfo.println("ok");
                envioInfo.flush();
            }
            msg=reciboInfo.readLine();
            String [] apuntes = msg.split(",");
            if ("dados".equalsIgnoreCase(apuntes[0])) {
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
                String[] aux= datos.toString().split(",");
                if("anfitrion".equalsIgnoreCase(aux[3])){
                    while((msg = reciboInfo.readLine())==null){
                        // ESTO ESPERA MIENTRAS EL MENSAJE SEA NULL(HASTA QUE ACABAN LAS PARTIDAS)
                    }
                    if (msg != null) {
                        int id = Integer.parseInt(msg.split(" ")[0]);
                        String resultado = msg.split(" ")[1];
                        server.acabarPartida(id, resultado);
                        System.out.println(partida);
                        server.eliminarPartidas(id);
                    }
                }
            }else{
                System.out.println("Aquí irían otros juegos si los hubiera");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
