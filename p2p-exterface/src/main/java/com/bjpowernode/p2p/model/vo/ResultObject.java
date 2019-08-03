package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResultObject implements Serializable {

    /**
     * 错误码：SUCCESS|FAIL
     */
    private String errorCode;
}
