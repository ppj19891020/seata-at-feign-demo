package com.fly.seata.controller;

import com.fly.seata.common.api.OrderApi;
import com.fly.seata.common.api.StorageApi;
import com.fly.seata.common.dto.OrderDTO;
import io.seata.spring.annotation.GlobalTransactional;
import javax.servlet.http.HttpServletRequest;
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
  public String purchase(HttpServletRequest request,@RequestBody OrderDTO orderDTO){
//    OrderDTO order = new OrderDTO();
//    order.setProductId(1l);
//    order.setCount(1);
//    order.setMoney(new BigDecimal(1));
//    order.setUserId(1l);
    orderApi.createOrder(orderDTO);

    String type = request.getHeader("type");
    if(null !=type && type.equalsIgnoreCase("hot")){
      //更新操作-热点数据测试
      storageApi.reduce(orderDTO.getProductId(),orderDTO.getCount());
    }else{
      //插入操作-非热点数据
      storageApi.save();
    }
    return "ok";
  }

}
