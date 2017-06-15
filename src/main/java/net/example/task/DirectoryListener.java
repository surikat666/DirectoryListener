package net.example.task;

public interface DirectoryListener extends Runnable {

    void run();

    void stop();

    static DirectoryListener initListener(String dirSource, String dirDist) {
        return new DefaultDirectoryListener(dirSource, dirDist);
    }
}
