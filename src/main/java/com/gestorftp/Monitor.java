package com.gestorftp;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class Monitor {
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys;
    private final Path directory;

    public Monitor(String pathDirectorio) throws IOException {
        this.directory = Paths.get(pathDirectorio);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<>();

        WatchKey key = directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, directory);
        System.out.println("se está analizando el directorio: " + directory);
    }

    public WatchEvent<?> eventWatcher() throws InterruptedException {
        WatchKey key = watchService.take();

        for (WatchEvent<?> eevent : key.pollEvents()) {
            
            WatchEvent.Kind<?> kind = eevent.kind();
            WatchEvent<Path> event = (WatchEvent<Path>)eevent;
            Path relativePath = event.context();
            Path absolutePath = directory.resolve(relativePath);
//
            System.out.println("Evento detectado: " + kind.name() + " para " + absolutePath);

            key.reset();

            return event;
        }

        return null;
    }

    public Path getDirectory() {
        return directory;
    }
}