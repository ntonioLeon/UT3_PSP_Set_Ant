package org.educa.game;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * @author Sergio Gonzalez y Antonio Leon
 */
public class Player extends Thread {

    private String gameType; // Tipo de juego que jugará
    private boolean enPartida = false; // Determina si está en partida, falso por defecto

    /**
     * Constructor de Player
     * @param name     recibe un nombre
     * @param gameType recibe un tipo de juego
     */
    public Player(String name, String gameType) {
        super.setName(name);
        this.gameType = gameType;
    }

    @Override
    /**
     * Metodo que lanza a los jugadores para conectarse al server
     */
    public void run() {
        System.out.println(getName() + " Intenta conectarse");
        try (Socket jugador = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
            jugador.connect(addr);
            conexion(jugador);
        } catch (IOException e) {
            //e.printStackTrace();
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
                    if (checkNickName(getName())) {
                        envioServ.println(gameType + "," + getName() + "," + 5555);
                        envioServ.flush();
                        //Recibimos una String; nickNameOponente, host, puerto, anfitrion, idPartida
                        msg = reciboServ.readLine();
                        String[] datos = msg.split(",");
                        if ("anfitrion".equalsIgnoreCase(datos[3])) {
                            System.out.println("----------JUGADORES DE LA PARTIDA " + datos[4] + "---------- \n\t\t\tANFITRION: " + getName() + "\n\t\t\tINVITADO:  " + datos[0] + "\n");
                            conexionAnfitrion(datos);
                        } else {
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
            //e.printStackTrace();
        }
    }

    /**
     * Metodo que realiza las conexiones con el anfitrion cuando se es invitado
     * @param datos recibe los datos
     */
    private void conexionInvitado(String[] datos) {
        boolean done = false;
        while (!done) {
            try (Socket socketInvitado = new Socket()) {
                InetSocketAddress addrInv = new InetSocketAddress(datos[1], Integer.parseInt(datos[2]));
                socketInvitado.connect(addrInv);
                partidaInvitado(socketInvitado, datos);
                done = true;
            } catch (Exception e) {
                //Si el server no esta up tiene que pasar por aqui.
                esperaInvitado(); //Para que no de multitud de errores que duerma un poco.
                //e.printStackTrace(); Excepcion controlada
            }
        }
    }

    /**
     * Metodo que duerme al hilo cliente que es el invitado cuando el server (Anfitrion) no esta conectado aun.
     */
    private void esperaInvitado() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            //ex.printStackTrace();
        }
    }

    /**
     * Método que realiza la interacción del invitado con el anfitrión cuando empieza la partida
     * @param socketInvitado Socket ya conectado con el anfitrion para el desarrollo de la partida
     */
    private void partidaInvitado(Socket socketInvitado, String[] datos) {
        try (BufferedReader reciboSInv = new BufferedReader(new InputStreamReader(socketInvitado.getInputStream()));
             PrintWriter envioInv = new PrintWriter(new OutputStreamWriter(socketInvitado.getOutputStream()))) {
            String resultado = "";
            while (!"V".equalsIgnoreCase(resultado) && !"D".equalsIgnoreCase(resultado)) {
                int tiradaInv = new Random().nextInt(1, 7);
                envioInv.println(tiradaInv);
                envioInv.flush();
                resultado = reciboSInv.readLine();
                if ("V".equalsIgnoreCase(resultado)) {
                    System.out.println("Invitado imprime:\n>>>>>>>>>>>>>>>>>>>> RESULTADO PARTIDA: " + datos[4] + " <<<<<<<<<<<<<<<<<<<<\n\t\t\t   Ha ganado el ANFITRION, " + datos[0] + "\n");
                } else if ("D".equalsIgnoreCase(resultado)) {
                    System.out.println("Invitado imprime:\n>>>>>>>>>>>>>>>>>>>> RESULTDO PARTIDA: " + datos[4] + " <<<<<<<<<<<<<<<<<<<<\n\t\t\t   Ha ganado el INVITADO, " + getName() + "\n");
                } else {
                    System.out.println("Invitado imprime:\n<<<<<<<<<<<<<<<<<<<< RESULTADO PARTIDA: " + datos[4] + " >>>>>>>>>>>>>>>>>>>>\n\t\t    ¡¡¡HEMOS EMPATADO, SE JUEGA DE NUEVO!!!\n");
                }
            }
            enPartida = false;
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Metodo que realiza las conexiones con el invitado cuando se es anfitrion
     * @param datos recibe los datos
     */
    private void conexionAnfitrion(String[] datos) {
        try (ServerSocket socketAnfitrion = new ServerSocket()) {
            InetSocketAddress addrPartida = new InetSocketAddress(datos[1], Integer.parseInt(datos[2]));
            socketAnfitrion.bind(addrPartida);
            partidaAnfitrion(datos, socketAnfitrion);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Método que realiza la interacción del invitado con el anfitrión cuando empieza la partida
     * @param datos           recibe los datos
     * @param socketAnfitrion recibe el Server socket para aceptar la conexión
     */
    private void partidaAnfitrion(String[] datos, ServerSocket socketAnfitrion) {
        try (Socket partidaAnfitrion = socketAnfitrion.accept();
             BufferedReader reciboDePartidaAnf = new BufferedReader(new InputStreamReader(partidaAnfitrion.getInputStream()));
             PrintWriter envioDePartidaAnf = new PrintWriter(new OutputStreamWriter(partidaAnfitrion.getOutputStream()))) {
            String resultadoPartida = "";
            while (!"V".equalsIgnoreCase(resultadoPartida) && !"D".equalsIgnoreCase(resultadoPartida)) {
                String resultadoInv = reciboDePartidaAnf.readLine();
                if (checkNum(resultadoInv) && checkRango(Integer.parseInt(resultadoInv))) {
                    int tiradaInv = Integer.parseInt(resultadoInv);
                    int tiradaAnf = new Random().nextInt(1, 7);
                    System.out.println("========== JUGADA DE LA PARTIDA " + datos[4] + " ==========\n\t\t\tInvitado saca:  " + tiradaInv + "\n\t\t\tAnfitrion saca: " + tiradaAnf + "\n");
                    if (tiradaAnf > tiradaInv) {
                        envioDePartidaAnf.println("V");
                        envioDePartidaAnf.flush();
                        resultadoPartida = "V";
                        System.out.println("Anfitrion imprime:\n>>>>>>>>>>>>>>>>>>>> RESULTADO PARTIDA: " + datos[4] + " <<<<<<<<<<<<<<<<<<<<\n\t\t\t   Ha ganado el ANFITRION, " + datos[0] + "\n");
                    } else if (tiradaAnf < tiradaInv) {
                        envioDePartidaAnf.println("D");
                        envioDePartidaAnf.flush();
                        resultadoPartida = "D";
                        System.out.println("Anfitrion imprime:\n>>>>>>>>>>>>>>>>>>>> RESULTADO PARTIDA: " + datos[4] + " <<<<<<<<<<<<<<<<<<<<\n\t\t\t   Ha ganado el INVITADO, " + datos[0] + "\n");
                    } else {
                        envioDePartidaAnf.println("E");
                        envioDePartidaAnf.flush();
                        System.out.println("Anfitrion imprime:\n<<<<<<<<<<<<<<<<<<<< RESULTADO PARTIDA: " + datos[4] + " >>>>>>>>>>>>>>>>>>>>\n\t\t    ¡¡¡HEMOS EMPATADO, SE JUEGA DE NUEVO!!!\n");
                    }
                }
            }
            finDeLaPartida(datos, resultadoPartida);  //Informamos al server de que el game esta acabado
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    /**
     * Metodo que se encarga de notificar al servidor que la partida ha terminado y quien es el ganador.
     * @param datos Datos que se enviaran al server
     * @param resultadoPartida El resultado del match
     */
    private void finDeLaPartida(String[] datos, String resultadoPartida) {
        try (Socket jugador = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress("localhost", 5555);
            jugador.connect(addr);
            try (BufferedReader reciboFin = new BufferedReader(new InputStreamReader(jugador.getInputStream()));
                 PrintWriter envioFin = new PrintWriter(new OutputStreamWriter(jugador.getOutputStream()))) {
                envioFin.println("termino");
                envioFin.flush();
                String msg = reciboFin.readLine();
                if ("ok".equalsIgnoreCase(msg)) {
                    envioFin.println(datos[4] + "," + resultadoPartida + "," + gameType);
                    envioFin.flush();
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        } catch (IOException e) {
            //e.printStackTrace();
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
