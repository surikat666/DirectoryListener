package net.example.task;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;

import static java.lang.System.*;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static net.example.task.DirectoryListener.initListener;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            out.println("Неверно число аргументов");
            exit(1);
        }

        String dirSource = args[0];
        if (!checkDir(dirSource)) {
            out.println("Директория-источник не существует");
            exit(1);
        }

        String dirDist = args[1];
        if (!checkDir(dirDist)) {
            out.println("Директория-назначения не существует");
            exit(1);
        }

        DirectoryListener directoryListener = initListener(dirSource, dirDist);

        // Создает тред пул из одного потока для запуска слушателя, чтобы программа могла принимать команды
        ExecutorService threadPool = newSingleThreadExecutor();
        threadPool.execute(directoryListener);

        // если пользователь ввел 'exit' останавливаем слушателя, закрывает тред пул, выходим из программы
        Scanner scanner = new Scanner(in);
        String next = scanner.next();
        if ("exit".equals(next)) {
            directoryListener.stop();
            threadPool.shutdown();
            exit(0);
        }

    }

    private static boolean checkDir(String path) {
        File file = new File(path);
        return file.exists() && file.isDirectory();
    }
}
