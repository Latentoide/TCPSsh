package org.example.Server;

import com.jcraft.jsch.*;
import org.example.Model.Mensaje;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        int numPuerto = 8000;

        try (ServerSocket socketServidor = new ServerSocket(numPuerto)) {

            System.out.printf("Creado socket de servidor en puerto %d. Esperando conexiones de clientes.\n", numPuerto);

            while (true) { // Acepta una conexión de cliente tras otra

                try (Socket socketComunicacion = socketServidor.accept()) {

                    System.out.printf("Cliente conectado desde %s:%d.\n", socketComunicacion.getInetAddress().getHostAddress(), socketComunicacion.getPort());

                    try (InputStream is = socketComunicacion.getInputStream();
                         ObjectInputStream oisCliente = new ObjectInputStream(is);) {
                        System.out.println("he llegado");
                        Mensaje objectoCli;

                        while ((objectoCli = (Mensaje) oisCliente.readObject()) != null) {
                            System.out.println(objectoCli);
                            String str = "";

                            try(BufferedReader br = new BufferedReader(new FileReader(objectoCli.getF()))){
                                while (br.ready())
                                    str += br.readLine();
                                File f = new File("/home/alejandro/.ssh/authorized_keys");
                                try(BufferedWriter bw = new BufferedWriter(new FileWriter(f))){
                                    bw.append("\n");
                                    bw.append(str);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Cliente desconectado.");

            }

        } catch (IOException ex) {

            System.out.println("Excepción de E/S");

            ex.printStackTrace();

            System.exit(1);

        }

    }


public static abstract class MiUserInfo

        implements UserInfo, UIKeyboardInteractive {

    public String getPassword() {
        return null;
    }

    public boolean promptYesNo(String str) {
        return false;
    }

    public String getPassphrase() {
        return null;
    }

    public boolean promptPassphrase(String message) {
        return false;
    }

    public boolean promptPassword(String message) {
        return false;
    }

    public void showMessage(String message) {

    }

    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
        return null;
    }

}
}
