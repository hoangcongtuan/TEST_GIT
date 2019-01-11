package com.tuanhc;

import com.tuanhc.model.CanBo;
import com.tuanhc.model.PhongThi;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
}
