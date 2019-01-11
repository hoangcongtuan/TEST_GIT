package com.tuanhc;

import com.tuanhc.model.CanBo;
import com.tuanhc.model.CanBoResult;
import com.tuanhc.model.PhongThi;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    private static Utils sInstance;

    public static Utils getInstance() {
        if (sInstance == null)
            sInstance = new Utils();
        return sInstance;
    }

    public XSSFWorkbook getWorkBook(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        return new XSSFWorkbook(fis);
    }

    public List<CanBo> getListCanBo(XSSFSheet sheet) {
        List<CanBo> canBoList = new ArrayList<>();

        Iterator<Row> rowIterator  = sheet.iterator();
        //skip first row
        rowIterator.next();
        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int stt = (int)row.getCell(0).getNumericCellValue();
            String ma_cb = row.getCell(1).getStringCellValue();
            String ho_ten = row.getCell(2).getStringCellValue();
            String co_quan = row.getCell(3).getStringCellValue();

            CanBo canBo = new CanBo(stt, ma_cb, ho_ten, co_quan);
            canBoList.add(canBo);
        }

        return canBoList;
    }

    public List<PhongThi> getListPhongThi(XSSFSheet sheet) {
        List<PhongThi> phongThiList = new ArrayList<>();

        Iterator<Row> rowIterator  = sheet.iterator();
        //skip first row
        rowIterator.next();
        while(rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Iterator<Cell> cellIterator = row.cellIterator();
            int stt = (int)row.getCell(0).getNumericCellValue();
            String ma_phong = row.getCell(1).getStringCellValue();
            String ten_phong = row.getCell(2).getStringCellValue();

            PhongThi phongThi = new PhongThi(stt, ma_phong, ten_phong);
            phongThiList.add(phongThi);
        }

        return phongThiList;
    }

    public void saveToFile(File file, List<CanBoResult> canBoResultList, String sheetName) throws IOException {
        XSSFWorkbook workbook;
        FileInputStream fis = null;
        if (file.exists()) {
            fis = new FileInputStream(file);
            workbook = new XSSFWorkbook(fis);
        } else
        workbook = new XSSFWorkbook();

        int sheetIndex = workbook.getSheetIndex(sheetName);
        if (sheetIndex != -1) {
            workbook.removeSheetAt(sheetIndex);
        }
        XSSFSheet sheet = workbook.createSheet(sheetName);

        Cell cell;
        Row row;


        row = sheet.createRow(0);

        //stt
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("stt");

        //ma_cb
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("ma_cb");

        //ho_ten
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("ho_ten");

        //co_quan
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("co_quan");

        //phong
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("phong");

        if (canBoResultList != null) {
            for(int i = 0; i < canBoResultList.size(); i++) {
                CanBoResult canBoResult = canBoResultList.get(i);

                row = sheet.createRow(i + 1);

                //stt
                cell = row.createCell(0, CellType.STRING);
                cell.setCellValue(String.valueOf(i + 1));

                //ma_cb
                cell = row.createCell(1, CellType.STRING);
                cell.setCellValue(canBoResult.ma_cb);

                //ho_ten
                cell = row.createCell(2, CellType.STRING);
                cell.setCellValue(canBoResult.ho_ten);

                //co_quan
                cell = row.createCell(3, CellType.STRING);
                cell.setCellValue(canBoResult.co_quan);

                //phong
                cell = row.createCell(4, CellType.STRING);
                cell.setCellValue(canBoResult.phong);
            }
        }

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
        System.out.println("save result to file");

        if (fis != null)
            fis.close();
        outFile.close();
    }
}
