package org.educa.game;

import javax.naming.ldap.SortKey;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.sql.SQLOutput;
import java.util.Random;

public class Player extends Thread {

    private String gameType; // Tipo de juego que jugará
    private boolean enPartida = false; // Determina si está en partida, falso por defecto

    /**
     * Constructor de Player
     * @param name recibe un nombre
     * @param gameType recibe un tipo de juego
     */
    public Player(String name, String gameType) {
        super.setName(name);
        this.gameType = gameType;
    }

    @Override
    public void run() {
        System.out.println(getName()+" Intenta conectarse");
        try (Socket jugador = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
            jugador.connect(addr);
            conexion(jugador);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clase que realiza la conexión de los jugadores para la partida, determina si es anfitrion o invitado
     * @param jugador recibe jugador
     */
    private void conexion(Socket jugador) {
        try (BufferedReader reciboServ = new BufferedReader(new InputStreamReader(jugador.getInputStream()));
             PrintWriter envioServ = new PrintWriter(new OutputStreamWriter(jugador.getOutputStream()))) {
            if (!enPartida) {
                enPartida = true;
                envioServ.println("empiezo");
                envioServ.flush();
                String msg = reciboServ.readLine();
                if ("ok".equalsIgnoreCase(msg)) {
                    ////////////////////////////////
                    /////////// PREGUNTAR SI HAY QUE INTRODUCIR UN NOMBRE O CON EL GETNAME DEL HILO VALE
                    ///////////////////////////////
                    if (checkNickName(getName())) {
                        envioServ.println(gameType + "," + getName()+","+5555);
                        envioServ.flush();
                        //Recibimos una String; nickNameOponente, host, puerto, anfitrion, idPartida
                        msg = reciboServ.readLine();
                        String[] datos = msg.split(",");
                        if ("anfitrion".equalsIgnoreCase(datos[3])) {
                            System.out.println("----------JUGADORES DE LA PARTIDA "+datos[4]+"---------- \n\t\t\tANFITRION: "+getName()+"\n\t\t\tINVITADO:  " + datos[0]+"\n");
                            //System.out.println("En la partida "+datos[4]+" ANFITRION: \n\t\tYo: "+getName()+". \n\t\tOponente: " + datos[0]);
                            conexionAnfitrion(datos, envioServ);
                        } else {
                            //System.out.println("En la partida "+datos[4]+" INVITADO: \n\t\tYo: "+getName()+". \n\t\tOponente: " + datos[0]);
                            conexionInvitado(datos);
                        }
                    } else {
                        System.out.println("El nombre del jugador es demasiado largo, máx 10 carácteres");
                    }
                } else {
                    System.out.println("La sala está llena");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que realiza las conexiones con el anfitrion cuando se es invitado
     * @param datos recibe los datos
     */
    private void conexionInvitado(String[] datos) {
        try (Socket socketInvitado = new Socket()) {
            boolean esperando = true;
            /////////////////////
            /////////////////////
            //////// PREGUNTAR MANUEL FORMA DE HACER ESPERAR A LOS INVITADOS
            ////////////////////
            ////////////////////
            Thread.sleep(1000);
            InetSocketAddress addrInv = new InetSocketAddress(datos[1],Integer.parseInt(datos[2]));
            socketInvitado.connect(addrInv);
            partidaInvitado(socketInvitado, datos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que realiza la interacción del invitado con el anfitrión cuando empieza la partida
     * @param socketInvitado
     */
    private void partidaInvitado(Socket socketInvitado, String [] datos) {
        try(BufferedReader reciboSInv = new BufferedReader(new InputStreamReader(socketInvitado.getInputStream()));
            PrintWriter envioInv = new PrintWriter(new OutputStreamWriter(socketInvitado.getOutputStream()))){
            String resultado="";
            while(!"V".equalsIgnoreCase(resultado)&&!"D".equalsIgnoreCase(resultado)){
                int tiradaInv = new Random().nextInt(1,7);
                envioInv.println(tiradaInv);
                envioInv.flush();
                resultado = reciboSInv.readLine();
                if("V".equalsIgnoreCase(resultado)){
                    System.out.println(">>>>>>>>>>>>>>>>>>>> RESULTADO PARTIDA: "+datos[4]+" <<<<<<<<<<<<<<<<<<<<\n\t\t\t   Ha ganado el ANFITRION, "+datos[0]+"\n");
                }else if("D".equalsIgnoreCase(resultado)){
                    System.out.println(">>>>>>>>>>>>>>>>>>>> RESULTDO PARTIDA: "+datos[4]+" <<<<<<<<<<<<<<<<<<<<\n\t\t\t   Ha ganado el INVITADO, "+getName()+"\n");
                }else{
                    System.out.println("<<<<<<<<<<<<<<<<<<<< RESULTADO PARTIDA: "+datos[4]+" >>>>>>>>>>>>>>>>>>>>\n\t\t    ¡¡¡HEMOS EMPATADO, SE JUEGA DE NUEVO!!!\n");
                }
            }
            enPartida=false;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Metodo que realiza las conexiones con el invitado cuando se es anfitrion
     * @param datos recibe los datos
     * @param envioServ recibe el PrintWriter para conectarse con el servidor
     */
    private void conexionAnfitrion(String[] datos, PrintWriter envioServ) {
        try (ServerSocket socketAnfitrion = new ServerSocket()) {
            InetSocketAddress addrPartida = new InetSocketAddress(datos[1], Integer.parseInt(datos[2]));
            socketAnfitrion.bind(addrPartida);
            partidaAnfitrion(datos, envioServ, socketAnfitrion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que realiza la interacción del invitado con el anfitrión cuando empieza la partida
     * @param datos recibe los datos
     * @param envioServ recibe el PrintWriter para conectarse con el servidor
     * @param socketAnfitrion recibe el Server socket para aceptar la conexión
     */
    private void partidaAnfitrion(String[] datos, PrintWriter envioServ, ServerSocket socketAnfitrion) {
        try (Socket partidaAnfitrion = socketAnfitrion.accept();
             BufferedReader reciboDePartidaAnf = new BufferedReader(new InputStreamReader(partidaAnfitrion.getInputStream()));
             PrintWriter envioDePartidaAnf = new PrintWriter(new OutputStreamWriter(partidaAnfitrion.getOutputStream()))) {
            String resultadoPartida = "";
            while (!"V".equalsIgnoreCase(resultadoPartida) && !"D".equalsIgnoreCase(resultadoPartida)) {
                String resultadoInv = reciboDePartidaAnf.readLine();
                if (checkNum(resultadoInv) && checkRango(Integer.parseInt(resultadoInv))) {
                    int tiradaInv = Integer.parseInt(resultadoInv);
                    int tiradaAnf = new Random().nextInt(1, 7);
                    System.out.println("========== JUGADA DE LA PARTIDA "+datos[4]+" ==========\n\t\t\tInvitado saca:  "+tiradaInv+"\n\t\t\tAnfitrion saca: "+tiradaAnf+"\n");
                    //System.out.println("Resultado invitado: " + tiradaInv + "\nResultado anfitrion: " + tiradaAnf);
                    if (tiradaAnf > tiradaInv) {
                        envioDePartidaAnf.println("V");
                        envioDePartidaAnf.flush();
                        resultadoPartida = "V";
                        envioServ.println(datos[4]+" "+resultadoPartida);
                        envioServ.flush();

                    } else if (tiradaAnf < tiradaInv) {
                        envioDePartidaAnf.println("D");
                        envioDePartidaAnf.flush();
                        resultadoPartida = "D";
                        envioServ.println(datos[4]+" "+resultadoPartida);
                        envioServ.flush();

                    } else {
                        envioDePartidaAnf.println("E");
                        envioDePartidaAnf.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comprueba que el nombre del usuario es menor a 10 carácteres
     * @param name recibe el nombre
     * @return devuelve boolean, si es válido true, si no, false
     */
    private boolean checkNickName(String name) {
        return name.length() <= 10;
    }

    /**
     * Comprueba el numero del "Dado", si es un número y si tiene solo 1 dígido (para que nadie haga trampas)
     * @param num recibe el numero
     * @return devuelve true si es un numero y es de 1 dígito
     */
    private boolean checkNum(String num) {
        for (int i = 0; i < num.length(); i++) {
            if (!Character.isDigit(num.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * comprueba si el número es un numero entre 1 y 6
     * @param num recibe el numero
     * @return devuelve boolean, true si es un numero dentro del rango, false si no
     */
    private boolean checkRango(int num) {
        return 0 < num && 6 >= num;
    }
}
