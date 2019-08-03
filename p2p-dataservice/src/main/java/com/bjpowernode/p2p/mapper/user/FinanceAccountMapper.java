package com.bjpowernode.p2p.mapper.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_finance_account
     *
     * @mbggenerated Tue Jun 11 18:06:15 CST 2019
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_finance_account
     *
     * @mbggenerated Tue Jun 11 18:06:15 CST 2019
     */
    int insert(FinanceAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_finance_account
     *
     * @mbggenerated Tue Jun 11 18:06:15 CST 2019
     */
    int insertSelective(FinanceAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_finance_account
     *
     * @mbggenerated Tue Jun 11 18:06:15 CST 2019
     */
    FinanceAccount selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_finance_account
     *
     * @mbggenerated Tue Jun 11 18:06:15 CST 2019
     */
    int updateByPrimaryKeySelective(FinanceAccount record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table u_finance_account
     *
     * @mbggenerated Tue Jun 11 18:06:15 CST 2019
     */
    int updateByPrimaryKey(FinanceAccount record);

    FinanceAccount selectFinanceAccountByUid(Integer uid);

    int updateFinanceAccountByBid(Map<String, Object> paramMap);

//    收益金额返还到用户账户
    int updateFinanceAccountByIncomeBack(Map<String, Object> paramMap);

    int updateFinanceAccountByRecharge(Map<String, Object> paramMap);
}