package com.gestorftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class GestorFTP {

    private final FTPClient clienteFTP;
    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 21;
    private static final String USUARIO = "ciso";
    private static final String PASSWORD = "ciso";

    public GestorFTP() {
        clienteFTP = new FTPClient();
    }

    private void conectar() throws SocketException, IOException {
        clienteFTP.connect(SERVIDOR, PUERTO);
        int respuesta = clienteFTP.getReplyCode();

        if (!FTPReply.isPositiveCompletion(respuesta)) {
            clienteFTP.disconnect();
            throw new IOException("Error al conectar con el servidor FTP. Código de respuesta: " + respuesta);
        }

        boolean credencialesOK = clienteFTP.login(USUARIO, PASSWORD);
        if (!credencialesOK) {
            clienteFTP.disconnect();
            throw new IOException("Error al conectar con el servidor FTP. Credenciales incorrectas.");
        }
        
        clienteFTP.enterLocalPassiveMode();

        clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);
        clienteFTP.setControlEncoding("UTF-8");
    }

    private void desconectar() throws IOException {
        if (clienteFTP.isConnected()) {
            clienteFTP.logout();
            clienteFTP.disconnect();
        }
    }

    private boolean subirFichero(String localFilePath) throws IOException {
        File ficheroLocal = new File(localFilePath);
        if (!ficheroLocal.exists() || !ficheroLocal.isFile()) {
            System.err.println("Error: El archivo local no existe o no es un archivo: " + localFilePath);
            return false;
        }

        System.out.println("Intentando subir: " + ficheroLocal.getName());
        try (InputStream is = new FileInputStream(ficheroLocal)) {
            
            boolean enviado = clienteFTP.storeFile(ficheroLocal.getName(), is);
            if (!enviado) {
                System.err.println("Fallo al subir " + ficheroLocal.getName() + ". Respuesta del servidor: " + clienteFTP.getReplyString());
            }
            return enviado;
        }
    }

    private boolean descargarFichero(String remoteFileName, String localSavePath) throws IOException {
        File localFile = new File(localSavePath);
        
        File parentDir = localFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        System.out.println("Intentando descargar: " + remoteFileName + " a " + localSavePath);
        try (OutputStream os = new BufferedOutputStream(new FileOutputStream(localFile))) {
            
            boolean recibido = clienteFTP.retrieveFile(remoteFileName, os);
            if (!recibido) {
                System.err.println("Fallo al descargar " + remoteFileName + ". Respuesta del servidor: " + clienteFTP.getReplyString());
            }
            return recibido;
        }
    }

    public void realizarCopiaDeSeguridadSimple(String localSourceFolder) throws IOException {
        File folder = new File(localSourceFolder);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IOException("La carpeta de origen local no existe o no es un directorio: " + localSourceFolder);
        }

        try {

            conectar();
            System.out.println("Conectado al servidor FTP para copia de seguridad simple.");
            System.out.println("Subiendo archivos de: " + folder.getAbsolutePath() + " a la raíz del FTP.");

            File[] listOfFiles = folder.listFiles();
            if (listOfFiles != null) {
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        subirFichero(file.getAbsolutePath());
                    } else if (file.isDirectory()) {
                        System.out.println("Ignorando subdirectorio: " + file.getName() + " (modo de copia simple)");
                    }
                }
            }
            System.out.println("Copia de seguridad simple completada.");

        } finally {
            desconectar();
            System.out.println("Desconectado del servidor FTP.");
        }
    }

    public static void main(String[] args) {
        GestorFTP gestorFTP = new GestorFTP();
        String carpetaOrigenLocal = "C:/Users/Ciso/FTPOriginalFolder";
        String ficheroRemotoParaDescarga = "test.txt";
        String rutaLocalParaDescarga = "C:/Users/Ciso/FTPOriginalFolder/";

        try {

            gestorFTP.realizarCopiaDeSeguridadSimple(carpetaOrigenLocal);
            System.out.println("\n--- Probando la descarga ---");
            gestorFTP.conectar();
            boolean descargado = gestorFTP.descargarFichero(ficheroRemotoParaDescarga, rutaLocalParaDescarga + ficheroRemotoParaDescarga);
            if (descargado) {
                System.out.println("Fichero '" + ficheroRemotoParaDescarga + "' descargado correctamente.");
            } else {
                System.err.println("Ha ocurrido un error al intentar descargar el fichero '" + ficheroRemotoParaDescarga + "'.");
            }
            gestorFTP.desconectar();

        } catch (Exception e) {
            System.err.println("Ha ocurrido un error en la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
