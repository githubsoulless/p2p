package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PaginationVO<T> implements Serializable {

    private List<T> dataList;
    private Long total;//总条数
}
