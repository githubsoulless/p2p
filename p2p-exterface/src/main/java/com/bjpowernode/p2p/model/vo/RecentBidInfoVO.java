package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:RecentBidInfoVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2019/6/18 11:13
 * @author:guoxin
 */
@Data
public class RecentBidInfoVO implements Serializable {

    private String productName;

    private Double bidMoney;

    private Date bidTime;



}
