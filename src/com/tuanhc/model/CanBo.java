package com.tuanhc.model;

import java.util.ArrayList;
import java.util.List;

public class CanBo {
    public int stt;
    public String ma_cb;
    public String ho_ten;
    public String co_quan;

    public List<String> pairedList;
    public List<String> phongList;

    public CanBo(int stt, String ma_cb, String ho_ten, String co_quan) {
        this.stt = stt;
        this.ma_cb = ma_cb;
        this.ho_ten = ho_ten;
        this.co_quan = co_quan;

        pairedList = new ArrayList<>();
        phongList = new ArrayList<>();
    }
}
