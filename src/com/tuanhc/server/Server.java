package com.tuanhc.server;

import com.tuanhc.Utils;
import com.tuanhc.model.CanBo;
import com.tuanhc.model.CanBoResult;
import com.tuanhc.model.PhongThi;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

    List<CanBo> canBoList;
    List<PhongThi> phongThiList;
    Random random;
    int xepLichId;


    public Server() throws IOException {
//        serverSocket = new ServerSocket(SERVER_PORT);
//        state = STATE.WAIT_FOR_START;
//        clientSocket = serverSocket.accept();
//        dis = new DataInputStream(clientSocket.getInputStream());
//        br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        boolean isContinue = true;
//        while (isContinue) {
//            switch (state) {
//                case WAIT_FOR_START:
//                    processStartMessage(clientSocket);
//                    break;
//
//                case WAIT_FILE_LENGTH:
//                    processFileLengthMessage(clientSocket);
//                    break;
//
//                case WAIT_FILE:
//                    System.out.println("Wait file");
//                    processFileMessage(clientSocket);
//                    break;
//
//                case FINISH:
//                    isContinue = false;
//                    System.out.println("Finish!!");
//                    break;
//            }
//        }

        random = new Random();
        xepLichId = 0;

        getData();
        File file = new File("output.xlsx");
        for (int i = 0; i < 4; i++) {
            List<CanBoResult> canBoResultList = xepLich();
            if (canBoResultList == null)
                System.out.println("impossible!!");
            Utils.getInstance().saveToFile(file, canBoResultList, "ca " + i);
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
            canBoList = Utils.getInstance().getListCanBo(sheetCanBo);

            XSSFSheet sheetPhongThi = workbook.getSheet("phong_thi");
            phongThiList = Utils.getInstance().getListPhongThi(sheetPhongThi);

            System.out.println("get data success!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<CanBoResult> xepLich() {
        boolean isPossible = true;
        List<CanBoResult> canBoResultList = new ArrayList<>();
        List<CanBo> tmpCanBoList = new ArrayList<>();
        List<CanBo> tmpCanBoList2 = new ArrayList<>();
        List<CanBo> exceptList = new ArrayList<>();
        tmpCanBoList.addAll(canBoList);
        tmpCanBoList2.addAll(canBoList);

        HashMap<String, CanBo> canBoMap = new HashMap<>();
        HashMap<String, String> phongMap = new HashMap<>();

        for (PhongThi phongThi : phongThiList) {
//            System.out.println("phong thi " + phongThi.ma_phong);
            exceptList.clear();
            while (true) {
                if (exceptList.size() == tmpCanBoList2.size() && tmpCanBoList2.size() != 0) {
                    isPossible = false;
                    break;
                }

                CanBo canBo1 = getCanBo(tmpCanBoList2, phongThi, exceptList);
                if (canBo1 == null) {
                    isPossible = false;
                    break;
                }
                tmpCanBoList2.remove(canBo1);

                CanBo canBo2 = getCanBo(tmpCanBoList2, phongThi, canBo1);
                if (canBo2 == null) {
                    exceptList.add(canBo1);
                    tmpCanBoList2.clear();
                    tmpCanBoList2.addAll(tmpCanBoList);
                } else {
                    CanBoResult canBoResult1 = new CanBoResult(canBo1, phongThi.ma_phong);
                    CanBoResult canBoResult2 = new CanBoResult(canBo2, phongThi.ma_phong);
                    canBoResultList.add(canBoResult1);
                    canBoResultList.add(canBoResult2);

                    tmpCanBoList.remove(canBo1);
                    tmpCanBoList.remove(canBo2);

                    tmpCanBoList2.clear();
                    tmpCanBoList2.addAll(tmpCanBoList);

                    canBoMap.put(canBo1.ma_cb, canBo2);
                    phongMap.put(canBo1.ma_cb, phongThi.ma_phong);
                    break;
                }
            }

            if (!isPossible)
                break;
        }

        if (!isPossible) {
            return null;
        }

        //imposible, update paired list, phongList
        for(CanBo canBo: canBoList) {
            if (canBoMap.containsKey(canBo.ma_cb)) {
                CanBo canBo2 = canBoMap.get(canBo.ma_cb);
                String phong = phongMap.get(canBo.ma_cb);

                canBo.pairedList.add(canBo2.ma_cb);
                canBo.phongList.add(phong);

                canBo2.pairedList.add(canBo.ma_cb);
                canBo2.phongList.add(phong);
            }
        }

        for (CanBo canBo : tmpCanBoList2) {
            CanBoResult canBoGiamSat = new CanBoResult(canBo, "giam_sat");
            canBoResultList.add(canBoGiamSat);
        }

        return canBoResultList;
    }

    public CanBo getCanBo(List<CanBo> canBoList, PhongThi phongThi) {
        List<Integer> exceptList = new ArrayList<>();
        int index;
        while (true) {
            if (exceptList.size() == canBoList.size())
                return null;

            index = random.nextInt(canBoList.size());
            if (exceptList.indexOf(index) != -1)
                continue;

            CanBo canBo = canBoList.get(index);
            if (canBo.phongList.indexOf(phongThi.ma_phong) == -1) {
                return canBo;
            } else {
                exceptList.add(index);
            }
        }
    }

    public CanBo getCanBo(List<CanBo> canBoList, PhongThi phongThi, List<CanBo> exceptionList) {
        List<Integer> exceptList = new ArrayList<>();
        int index;
        while (true) {
            if (exceptList.size() == canBoList.size())
                return null;

            index = random.nextInt(canBoList.size());
            if (exceptList.indexOf(index) != -1)
                continue;

            CanBo canBo = canBoList.get(index);
            if (exceptionList.indexOf(canBo) != -1) {
                exceptList.add(index);
                continue;
            }
            if (canBo.phongList.indexOf(phongThi.ma_phong) == -1) {
                return canBo;
            } else {
                exceptList.add(index);
            }
        }
    }

    public CanBo getCanBo(List<CanBo> canBoList, PhongThi phongThi, CanBo pair) {
        List<Integer> exceptList = new ArrayList<>();
        int index;
        while (true) {
            if (exceptList.size() == canBoList.size())
                return null;
            index = random.nextInt(canBoList.size());
            if (exceptList.indexOf(index) != -1)
                continue;

            CanBo canBo = canBoList.get(index);
            if (canBo.phongList.indexOf(phongThi.ma_phong) == -1 && canBo.pairedList.indexOf(pair.ma_cb) == -1) {
                return canBo;
            } else
                exceptList.add(index);
        }
    }

    private void processFileMessage(Socket clientSocket) throws IOException {
        FileOutputStream fos = new FileOutputStream("serverInputFile.xlsx");
        byte[] buffer = new byte[BUFFER_SIZE];

        int read = 0;
        int totalRead = 0;
        long remaining = fileLenght;
        while ((read = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) > 0) {
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
        System.out.println("Server: Received file length = " + fileLenght);
        state = STATE.WAIT_FILE;
    }

    private void processStartMessage(Socket clientSocket) throws IOException {
        String message = br.readLine();
        message = message.trim();

        if ("start_send_file".equals(message)) {
            state = STATE.WAIT_FILE_LENGTH;
            System.out.println("Server: Received start message");
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
