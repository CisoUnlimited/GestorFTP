package com.gestorftp;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Key;
import java.util.Base64;

/**
 * Clase de utilidad que proporciona la funcionalidad para cifrar archivos
 * utilizando el algoritmo AES (Advanced Encryption Standard)
 * @author Ciso
 */
public class CifradorAESSimple {

    private static final int LONGITUD_BLOQUE = 32;
    private static final String PASSWORD = "NoMeLlamoSpidermanMeLlamoSpidermanMeLlamoSpiderman";

    public static File cipher(File archivoEntrada) throws Exception {

        if (!archivoEntrada.exists() || !archivoEntrada.isFile()) {
            throw new IOException("El archivo de entrada no existe o no es un archivo válido: " + archivoEntrada.getAbsolutePath());
        }

        Key clave = AESSimpleManager.obtenerClave(PASSWORD, LONGITUD_BLOQUE);

        System.out.println("Leyendo contenido de: " + archivoEntrada.getAbsolutePath());
        String textoEnClaro = new String(Files.readAllBytes(archivoEntrada.toPath()), "UTF-8");

        System.out.println("Cifrando contenido...");
        String textoCifradoBase64 = AESSimpleManager.cifrar(textoEnClaro, clave);

        byte[] contenidoCifradoBytes = Base64.getDecoder().decode(textoCifradoBase64);

        String nombreArchivoSalida = archivoEntrada.getAbsolutePath() + ".cipher";
        File archivoSalidaCifrado = new File(nombreArchivoSalida);

        System.out.println("Escribiendo contenido cifrado en: " + archivoSalidaCifrado.getAbsolutePath());
        Files.write(archivoSalidaCifrado.toPath(), contenidoCifradoBytes);

        System.out.println("Archivo cifrado creado exitosamente.");
        return archivoSalidaCifrado;
    }

}
