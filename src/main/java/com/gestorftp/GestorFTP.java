package com.gestorftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTP;

public class GestorFTP {

    private final FTPClient clienteFTP;
    private static final String SERVIDOR = "localhost";
    private static final int PUERTO = 21;
    private static final String USUARIO = "ciso";
    private static final String PASSWORD = "ciso";

    public GestorFTP() {
        clienteFTP = new FTPClient();
    }

    public void conectar() throws SocketException, IOException {
        clienteFTP.connect(SERVIDOR, PUERTO);
        int respuesta = clienteFTP.getReplyCode();
        // Check de código de respuesta negativo
        if (!FTPReply.isPositiveCompletion(respuesta)) {
            clienteFTP.disconnect();
            throw new IOException("Error al conectar con el servidor FTP");
        }
        // Intento de login
        if (!clienteFTP.login(USUARIO, PASSWORD)) {
            clienteFTP.disconnect();
            throw new IOException("Error al conectar con el servidor FTP. Credenciales incorrectas.");
        }
        clienteFTP.setFileType(FTP.BINARY_FILE_TYPE);
        System.out.println("Conectado al servidor FTP");
    }

    public void desconectar(OperatorThread hilo) throws IOException {
        if (clienteFTP.isConnected()) {
            hilo.interrupt();
            clienteFTP.disconnect();
            System.out.println("Desconectado del servidor FTP");
        }
    }

    public boolean subirFichero(String rutaFicheroOriginalLocal) throws IOException, Exception {

        File ficheroOriginalLocal = new File(rutaFicheroOriginalLocal);

        if (!ficheroOriginalLocal.exists() || !ficheroOriginalLocal.isFile()) {
            System.err.println("Error: El archivo original no existe o no es válido: " + rutaFicheroOriginalLocal);
            return false;
        }

        System.out.println("Cifrando archivo local: " + ficheroOriginalLocal.getName() + "...");
        File ficheroCifradoTemporal = CifradorAESSimple.cipher(ficheroOriginalLocal);

        InputStream is = null;
        boolean enviado = false;

        try {
            is = new FileInputStream(ficheroCifradoTemporal);

            String nombreFicheroCifrado = ficheroOriginalLocal.getName() + ".cipher";

            System.out.println("Subiendo fichero cifrado como: " + nombreFicheroCifrado + " al servidor FTP...");

            // Sube el contenido del archivo cifrado temporal al servidor FTP.
            enviado = clienteFTP.storeFile(nombreFicheroCifrado, is);

            if (enviado) {
                System.out.println("Fichero cifrado subido correctamente: " + nombreFicheroCifrado);
            } else {
                System.err.println("Error al subir el fichero cifrado: " + nombreFicheroCifrado);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el InputStream: " + e.getMessage());
                }
            }

            if (ficheroCifradoTemporal.exists()) {
                if (ficheroCifradoTemporal.delete()) {
                    System.out.println("Archivo cifrado temporal eliminado localmente: " + ficheroCifradoTemporal.getAbsolutePath());
                } else {
                    System.err.println("Advertencia: No se pudo eliminar el archivo cifrado temporal: " + ficheroCifradoTemporal.getAbsolutePath());
                }
            }
        }
        return enviado;
    }

    public boolean eliminarFichero(String rutaFichero) throws IOException {
        boolean eliminado = clienteFTP.deleteFile(rutaFichero + ".cipher");
        if (eliminado) {
            System.out.println("Fichero eliminado correctamente: " + rutaFichero);
        } else {
            System.err.println("Error al eliminar el fichero: " + rutaFichero);
        }
        return eliminado;
    }

    public boolean modificarFichero(String rutaFichero) throws IOException, Exception {
        boolean modificado = subirFichero(rutaFichero);
        return modificado;
    }
}
