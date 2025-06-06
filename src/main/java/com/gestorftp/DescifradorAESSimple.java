package com.gestorftp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Key;

public class DescifradorAESSimple {

    public static void decipher() {

        final int LONGITUD_BLOQUE = 32;//Expresado en bytes
        final String NOMBRE_FICHERO = "mensaje_cifrado.txt";
        final String PASSWORD = "NoMeLlamoSpidermanMeLlamoSpidermanMeLlamoSpiderman";

        try {

            File file = new File(NOMBRE_FICHERO);
            Key clave = AESSimpleManager.obtenerClave(PASSWORD, LONGITUD_BLOQUE);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String textoCifrado = br.readLine();
            String textoEnClaro = AESSimpleManager.descifrar(textoCifrado, clave);
            br.close();

            String textoEnClaroFormateado = AESSimpleManager.formatear(textoEnClaro, 120);
            System.out.println("El texto cifrado es: " + textoCifrado);
            System.out.println("El texto descifrado es: " + textoEnClaroFormateado);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
