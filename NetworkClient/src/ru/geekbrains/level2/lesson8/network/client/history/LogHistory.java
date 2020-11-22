package ru.geekbrains.level2.lesson8.network.client.history;

import java.io.*;

public class LogHistory {
    private RandomAccessFile rwFile;
    private File filePath;


    public LogHistory(String fileName) {
        this.filePath = new File(fileName);
        filePath.exists();
        try {
            this.rwFile = new RandomAccessFile(filePath, "rw");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }

    }

    public void writeFile(String message) {
        long fileLength = 0;
        try {
            fileLength = rwFile.length();
            rwFile.seek(fileLength);
            rwFile.writeUTF(message);

        } catch (IOException e) {
            System.out.println("Error to write log file");
        }

    }

    public void closeFile() {
        try {
            rwFile.close();
        } catch (IOException e) {
            System.out.println("Error close file");
        }
    }

    public String readLastNLines(long N) {
        long fileLength = 0;
        StringBuilder sb = new StringBuilder();
        int line = 0;
        int readByte = 0;
        String readString;
        long filePointer = 0;
        try {
            fileLength = rwFile.length() - 1;
            //этот цикл предназначен для вывода курсора на N сообщений вверх,
            //
            for (filePointer = fileLength; filePointer >= 0; filePointer--) {
                rwFile.seek(filePointer);
                readByte = rwFile.readByte();
                if (readByte == 0 && filePointer < fileLength)
                    line++;//когда вызывается метод writeUTF он в начале строки пишет символ с кодировкой равный 0
                if (line >= N) {
                    filePointer--;
                    break;
                }

            }
            rwFile.seek(filePointer + 1);
            for (long i = 0; i < line && line > 0; i++) {
                readString = rwFile.readUTF();
                sb.append(readString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return sb.toString();
        }
    }

}
