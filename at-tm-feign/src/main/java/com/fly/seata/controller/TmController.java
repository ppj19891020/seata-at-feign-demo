package com.fly.seata.controller;

import com.fly.seata.common.api.OrderApi;
import com.fly.seata.common.api.StorageApi;
import com.fly.seata.common.dto.OrderDTO;
import io.seata.spring.annotation.GlobalLock;
import io.seata.spring.annotation.GlobalTransactional;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@RestController
public class TmController {

  @Autowired
  private OrderApi orderApi;

  @Autowired
  private StorageApi storageApi;

  @GlobalLock
  @GlobalTransactional
  @GetMapping("/tm/purchase")
  public String purchase(){
    OrderDTO order = new OrderDTO();
    order.setProductId(1l);
    order.setCount(1);
    order.setMoney(new BigDecimal(1));
    order.setUserId(1l);
    orderApi.createOrder(order);
    storageApi.reduce(order.getProductId(),order.getCount());
    return "ok";
  }

}
