package org.example.Cliente;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import com.jcraft.jsch.Session;
import org.example.Model.Mensaje;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;

public class Cliente {
    public static void main(String[] args)  {
        try{
            connectSsh();
        }catch (Exception e) {
            String path = "/home/alejandro/.ssh/";
            String filename = "id_rsa";
            File f = new File(path+filename);
            if(f.exists()){
                Path pFile = Paths.get(f.getAbsolutePath());
                BasicFileAttributes bfa = null;
                try {
                    bfa = Files.readAttributes(pFile, BasicFileAttributes.class);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                FileTime ft = bfa.creationTime();
                long fecha = (new Date().getTime() - new Date(ft.toMillis()).getTime())/86400000;
                System.out.println(fecha);
                if (fecha > 30 ) {
                    f.delete();
                    createPass(path, filename);
                }
                connectToServer();
            }else {
                createPass(path, filename);
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            connectSsh();
        }


    }

    private static void connectSsh() {
        try {
            JSch jsch = new JSch();
            String user = "alejandro";
            String host = "127.0.0.1";
            int port = 22;
            String privateKey = "/home/alejandro/.ssh/id_rsa";
            jsch.addIdentity(privateKey);
            System.out.println("identity added ");
            Session session = jsch.getSession(user, host, port);

            // Si es necesario introducir el password para iniciar sesion
            //session.setPassword("alejandro");
            // Para permitir conectarse sin comprobar el host
            session.setConfig("StrictHostKeyChecking", "no");
            System.out.println("session created.");
            // Conectamos
            session.connect();
            System.out.println("session connected.....");
            Channel channel = session.openChannel("shell");
            channel.setInputStream(System.in);
            channel.setOutputStream(System.out);
            channel.connect(3 * 1000);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private static void connectToServer() {
        String path = "/home/alejandro/.ssh/";
        String filename = "id_rsa.pub";
        File f = new File(path+filename);
        int numPuerto = 8000;
        String host = "127.0.0.1";
        try (Socket echoSocket = new Socket(host, numPuerto);)
        {
            System.out.println("Conexión hecha");
            Mensaje m = new Mensaje(f);
            OutputStream os = echoSocket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(m);


        } catch (UnknownHostException e)
        {
            System.err.println("Don't know about host " + host);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to " + host);
            System.exit(1);
        }
    }

    private static void createPass(String path, String filename) {
        String file= path + filename;
        String comment="alejandro@lmde5";
        JSch jsch=new JSch();
        try{
            KeyPair kpair= KeyPair.genKeyPair(jsch, KeyPair.RSA,2048);

            // Solo para mostrar por consola kpair.writePrivateKey(System.out); kpair.writePublicKey(System.out,comment);
            kpair.writePrivateKey(System.out);
            kpair.writePublicKey(System.out, comment);

            // Creamos los ficheros de las claves privadas y publicas

            kpair.writePrivateKey(file);
            kpair.writePublicKey(file+".pub", comment);
            System.out.println("Finger print: "+kpair.getFingerPrint()); kpair.dispose();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.exit(0);
    }
}
