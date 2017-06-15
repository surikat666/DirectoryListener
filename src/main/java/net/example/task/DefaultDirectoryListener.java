package net.example.task;

import java.nio.file.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class DefaultDirectoryListener implements DirectoryListener {

    private final String dirSource;
    private final String dirDist;
    private final AtomicBoolean stopped;

    public DefaultDirectoryListener(String dirSource, String dirDist) {
        this.dirSource = dirSource;
        this.dirDist = dirDist;
        this.stopped = new AtomicBoolean(false);
    }


    @Override
    public void run() {
        try {
            Path pathSource = Paths.get(dirSource);
            WatchService watcher = FileSystems.getDefault().newWatchService();
            while (!stopped.get()) {
                try {
                    // wait for a key to be available
                    WatchKey key = pathSource.register(watcher, ENTRY_CREATE);
                    for (WatchEvent<?> event : key.pollEvents()) {
                        // get event type
                        WatchEvent.Kind<?> kind = event.kind();

                        // get file name
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        String fileName = ev.context().toFile().getName();

                        if (kind == ENTRY_CREATE) {
                            Path sourceFile = Paths.get(dirSource, fileName);
                            Path distPath = Paths.get(dirDist, fileName);
                            Files.copy(sourceFile, distPath, REPLACE_EXISTING);
                        }
                    }

                    // IMPORTANT: The key must be reset after processed
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                } catch (Exception ex) {
                    System.err.println("Произошла ошибка копирования");
                }
            }
        } catch (Exception ex) {
            System.err.println("Произошла ошибка копирования");
        }
    }

    @Override
    public void stop() {
        this.stopped.compareAndSet(false, true);
    }
}
