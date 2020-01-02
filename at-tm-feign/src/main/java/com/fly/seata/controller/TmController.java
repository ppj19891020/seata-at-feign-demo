package com.fly.seata.controller;

import com.fly.seata.common.api.OrderApi;
import com.fly.seata.common.api.StorageApi;
import com.fly.seata.common.dto.OrderDTO;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @GlobalTransactional
  @PostMapping("/tm/purchase")
  public String purchase(@RequestBody OrderDTO orderDTO){
//    OrderDTO order = new OrderDTO();
//    order.setProductId(1l);
//    order.setCount(1);
//    order.setMoney(new BigDecimal(1));
//    order.setUserId(1l);
    orderApi.createOrder(orderDTO);
    storageApi.reduce(orderDTO.getProductId(),orderDTO.getCount());
    return "ok";
  }

}
