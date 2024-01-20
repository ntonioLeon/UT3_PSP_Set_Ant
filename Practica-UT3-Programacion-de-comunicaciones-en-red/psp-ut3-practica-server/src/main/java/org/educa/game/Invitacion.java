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
            System.out.println(msg);
            String [] apuntes = msg.split(",");
            Jugador jugador = new Jugador(apuntes[1],
                    server.DIRECCION,
                    Integer.parseInt(apuntes[2]));
            server.getSala().getListaJugadores().add(jugador);
            server.comprobarPartida(jugador);
            Partida partida = server.creaccionDePartida(server.obtenerIdPartida(jugador));
            while(partida.getJugador1() ==null|| partida.getJugador2() ==null){
                partida = server.creaccionDePartida(server.obtenerIdPartida(jugador));
            }

            Jugador oponente;
            if(partida.getJugador1().getNombre().equalsIgnoreCase(jugador.getNombre())){
                oponente = partida.getJugador2();
            }else{
                oponente = partida.getJugador1();
            }
            //Recibimos una String; nickNameOponente, host, puerto, anfitrion, idPartida
            datos.append(oponente.getNombre()).append(",").append(oponente.getDireccion()).append(",").append(5555+server.obtenerIdPartida(jugador)).append(",");

            if(server.comprobarNumJugador(jugador)){
                datos.append("anfitrion").append(",");
            }else{
                datos.append("invitado").append(",");
            }
            datos.append(server.obtenerIdPartida(jugador));
            System.out.println(partida.toString());
            envioInfo.println(datos.toString());
            envioInfo.flush();
            System.out.println("Servidor envia los datos");
            Thread.sleep(3000);
            msg=reciboInfo.readLine();

            if(msg!=null){
                int id = Integer.parseInt(msg.split(" ")[0]);
                String resultado = msg.split(" ")[1];
                server.acabarPartida(id,resultado);
                System.out.println(partida);
                server.eliminarPartidas(id);
            }

            if (socket != null) {
                socket.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /////////////////////////////////////
    /////////////////////////////////////
    /////TODO   AÑADIR IF DE GAME TIPE
    /////TODO   COMENTAR TODO
    /////TODO   PREGUNTAR MECANISMO QUE EL CLIENTE ESPERA QUE EL SERVIDOR SE CONECTE
    /////TODO   DEPURAR LOS SYSTEM OUT PRINT PARA QUE DEN LA INFORMACION BIEN BONITA
    /////TODO   CAMBIAR SLEEP DE INVITACIONES POR "WHILE SMG = READER.READLINE)==NULL"
    /////////////////////////////////////
    /////////////////////////////////////



}
