package com.fly.seata.dao;

import com.fly.seata.domain.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * order dao
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@Mapper
public interface OrderDao {

  /**
   * 插入订单
   * @param order
   */
  @Insert({"INSERT INTO `order`(`order_no`, `user_id`, `product_id`, `count`, `money`) VALUES (#{orderNo},#{userId},#{productId},#{count},#{money})"})
  void insert(Order order);

}
