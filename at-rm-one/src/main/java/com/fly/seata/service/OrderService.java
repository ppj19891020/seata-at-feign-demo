package com.fly.seata.service;

import com.fly.seata.dao.OrderDao;
import com.fly.seata.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@Service
public class OrderService {

  @Autowired
  private OrderDao orderDao;


  /**
   * 创建订单
   * @param order
   */
  @Transactional
  public void createOrder(Order order){
    orderDao.insert(order);
  }

}
