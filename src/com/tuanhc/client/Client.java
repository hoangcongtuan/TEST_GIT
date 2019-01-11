package com.tuanhc.client;

import com.tuanhc.Utils;
import com.tuanhc.model.CanBo;
import com.tuanhc.model.PhongThi;
import com.tuanhc.server.Server;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Client {
    public final static int CLIENT_PORT = 8888;
    private static final int BUFFER_SIZE = 4096;


    public Client() throws IOException {

        Socket socket;

        socket = new Socket("localhost", Server.SERVER_PORT);
        File file = new File("input.xlsx");

        //send start message
        sendStartMessage(socket);
        sendFile(file, socket);
    }

    private void sendStartMessage(Socket socket) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
//        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        String messageText = "start_send_file";
        printWriter.println(messageText);
        System.out.println("Client: send start message");
    }

    public void sendFile(File file, Socket socket) throws IOException {
        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        FileInputStream fis = new FileInputStream(file);

        //send file lenght
        String fileLengthMessage = String.valueOf(file.length());
        printWriter.println(fileLengthMessage);
        System.out.println("Client: send file length = " + fileLengthMessage);

        //send data;
        dos = new DataOutputStream(socket.getOutputStream());
        byte[] buffer = new byte[BUFFER_SIZE];

        while (fis.read(buffer) > 0) {
            dos.write(buffer);
        }

        fis.close();
        dos.close();
    }


    public static void main(String[] args) {
        try {
            new Client();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getData() {
        try {
            File file = new File("input.xlsx");
            if (!file.exists()) {
                System.out.println("File not exitst!!");
                return;
            }
            XSSFWorkbook workbook = Utils.getInstance().getWorkBook(file);
            XSSFSheet sheetCanBo = workbook.getSheet("can_bo");
            List<CanBo> canBoList = Utils.getInstance().getListCanBo(sheetCanBo);

            XSSFSheet sheetPhongThi = workbook.getSheet("phong_thi");
            List<PhongThi> phongThiList = Utils.getInstance().getListPhongThi(sheetPhongThi);

            System.out.println("get data success!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
