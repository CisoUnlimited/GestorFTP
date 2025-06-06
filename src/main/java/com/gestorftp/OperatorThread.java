package com.gestorftp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hilo de operación que se encarga de realizar las operaciones FTP de forma
 * asíncrona. Cada instancia de este hilo realiza una única operación sobre un
 * fichero específico.
 * @author Ciso
 */
public class OperatorThread extends Thread {

    private final GestorFTP gestorFTP;
    private final String rutaFichero;
    private final operationType operacion;

    public enum operationType {
        SUBIR, ELIMINAR, MODIFICAR
    }

    public OperatorThread(String rutaFichero, operationType operacion) {
        this.gestorFTP = new GestorFTP();
        this.rutaFichero = rutaFichero;
        this.operacion = operacion;
    }

    @Override
    public void run() {
        try {
            gestorFTP.conectar();

            switch (operacion) {
                case SUBIR:
                    System.out.println("[" + Thread.currentThread().getName() + "] Subiendo fichero: " + rutaFichero);
                    gestorFTP.subirFichero(rutaFichero);
                    break;
                case ELIMINAR:
                    System.out.println("[" + Thread.currentThread().getName() + "] Eliminando fichero: " + rutaFichero);
                    gestorFTP.eliminarFichero(rutaFichero);
                    break;
                case MODIFICAR:
                    System.out.println("[ " + Thread.currentThread().getName() + "] Modificando fichero: " + rutaFichero);
                    gestorFTP.modificarFichero(rutaFichero);
            }

            gestorFTP.desconectar(this);

        } catch (IOException e) {
            System.err.println("[Hilo] Error: " + e.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(OperatorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
