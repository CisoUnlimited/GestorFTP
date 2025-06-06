package com.gestorftp;

import java.nio.file.*;

public class MainThread {

    private static final String LOCAL_DIRECTORY = "C:/FTPOriginalFolder";

    public static void main(String[] args) {
        try {
            Monitor monitor = new Monitor(LOCAL_DIRECTORY);
            System.out.println("Iniciando sistema");
            System.out.println("Directorio local: " + LOCAL_DIRECTORY);

            while (true) {
                WatchEvent<?> evento = monitor.eventWatcher();
                if (evento == null) {
                    continue;
                }

                WatchEvent.Kind<?> tipo = evento.kind();
                Path relativePath = (Path) evento.context();
                Path absolutePath = monitor.getDirectory().resolve(relativePath);
                String absolutePathString = absolutePath.toAbsolutePath().toString();
                String relativePathString = relativePath.toString();

                if (tipo == StandardWatchEventKinds.ENTRY_CREATE) {
                    Thread.sleep(2000);
                    new OperatorThread(absolutePathString, OperatorThread.operationType.SUBIR).start();
                } else if (tipo == StandardWatchEventKinds.ENTRY_DELETE) {
                    Thread.sleep(2000);
                    new OperatorThread(relativePathString, OperatorThread.operationType.ELIMINAR).start();
                } else if (tipo == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Thread.sleep(2000);
                    new OperatorThread(absolutePathString, OperatorThread.operationType.MODIFICAR).start();
                }
            }
        } catch (Exception e) {
            System.err.println("Ha ocurrido un error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
