package com.tuanhc.model;

public class CanBoResult extends CanBo{
    public String phong;

    public CanBoResult(CanBo canBo, String phong) {
        super(canBo.stt, canBo.ma_cb, canBo.ho_ten, canBo.co_quan);
        this.phong = phong;
    }
}
