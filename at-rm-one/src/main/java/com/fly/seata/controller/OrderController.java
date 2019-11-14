package com.fly.seata.controller;

import com.fly.seata.domain.Order;
import com.fly.seata.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@RestController
public class OrderController {

  @Autowired
  private OrderService orderService;

  @PostMapping(value = "/order/create",consumes = MediaType.APPLICATION_JSON_VALUE)
  public String createOrder(@RequestBody Order order){
    orderService.createOrder(order);
//    throw new RuntimeException("模拟抛出异常");
    return "ok";
  }

}
