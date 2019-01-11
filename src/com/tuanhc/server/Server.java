package com.tuanhc.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public final static int SERVER_PORT = 8889;
    private static final int BUFFER_SIZE = 4096;

    enum STATE {WAIT_FOR_START, WAIT_FILE_LENGTH, WAIT_FILE, FINISH}

    ServerSocket serverSocket;
    Socket clientSocket;
    STATE state;
    long fileLenght;
    DataInputStream dis;
    BufferedReader br;


    public Server() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        state = STATE.WAIT_FOR_START;
        clientSocket = serverSocket.accept();
        dis = new DataInputStream(clientSocket.getInputStream());
        br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        boolean isContinue = true;
        while (isContinue) {
            switch (state) {
                case WAIT_FOR_START:
                    processStartMessage(clientSocket);
                    break;

                case WAIT_FILE_LENGTH:
                    processFileLengthMessage(clientSocket);
                    break;

                case WAIT_FILE:
                    System.out.println("Wait file");
                    processFileMessage(clientSocket);
                    break;

                case FINISH:
                    isContinue = false;
                    System.out.println("Finish!!");
                    break;
            }
        }
    }

    private void processFileMessage(Socket clientSocket) throws IOException {
        FileOutputStream fos = new FileOutputStream("serverInputFile.xlsx");
        byte[] buffer = new byte[BUFFER_SIZE];

        int read = 0;
        int totalRead = 0;
        long remaining = fileLenght;
        while((read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            System.out.println("read " + totalRead + " bytes.");
            fos.write(buffer, 0, read);
        }

        fos.close();
        dis.close();
        state = STATE.FINISH;
    }

    private void processFileLengthMessage(Socket clientSocket) throws IOException {
        String message = br.readLine();
//        while((message = br.readLine()) == null);

        fileLenght = Long.valueOf(message);
        System.out.println("Server: Received file length = " + fileLenght );
        state = STATE.WAIT_FILE;
    }

    private void processStartMessage(Socket clientSocket) throws IOException {
        String message = br.readLine();
        message = message.trim();

        if ("start_send_file".equals(message)) {
            state = STATE.WAIT_FILE_LENGTH;
            System.out.println("Server: Received start message" );
        }
    }

    public static void main(String[] args) {
        try {
            new Server();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
