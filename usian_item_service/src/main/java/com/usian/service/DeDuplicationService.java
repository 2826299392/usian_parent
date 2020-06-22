package com.usian.service;

import com.usian.pojo.DeDuplication;

import java.util.List;

public interface DeDuplicationService {
    DeDuplication selectDeDuplicationByTxNo(String txNo);

    void insertDeDuplication(String txNo);
}
