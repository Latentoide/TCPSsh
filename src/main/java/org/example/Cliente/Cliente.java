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
    public static void main(String[] args) throws IOException {
        String path = "/home/alejandro/.ssh/";
        String filename = "id_rsa";
        File f = new File(path+filename);
        if(f.exists()){
            Path pFile = Paths.get(f.getAbsolutePath());
            BasicFileAttributes bfa = Files.readAttributes(pFile, BasicFileAttributes.class);
            FileTime ft = bfa.creationTime();
            long fecha = (new Date().getTime() - new Date(ft.toMillis()).getTime())/86400000;
            System.out.println(fecha);
            if (fecha > 30 ) {
                f.delete();
                createPass(path, filename);
            }
            connectToServer();
        }else{
            createPass(path, filename);
        }
    }

    private static void connectToServer() {
        String path = "/home/alejandro/.ssh/";
        String filename = "id_rsa";
        File f = new File(path+filename);
        int numPuerto = 8000;
        String host = "127.0.0.1";
        try (Socket echoSocket = new Socket(host, numPuerto);
             OutputStream os = echoSocket.getOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(os);
        )
        {
            System.out.println("Conexión hecha");
            Mensaje m = new Mensaje(f);
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
