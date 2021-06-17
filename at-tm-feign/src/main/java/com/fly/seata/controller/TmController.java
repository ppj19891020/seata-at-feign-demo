package com.fly.seata.controller;

import com.fly.seata.common.api.OrderApi;
import com.fly.seata.common.api.StorageApi;
import com.fly.seata.common.dto.OrderDTO;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
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

  /**
   * 使用分布式事物测试
   * @param request
   * @param orderDTO
   * @return
   */
  @GlobalTransactional
  @PostMapping("/seata/tm/purchase")
  public String purchase(HttpServletRequest request,@RequestBody OrderDTO orderDTO){
    String type = request.getHeader("type");
//    String xid = UUID.randomUUID().toString();
//    orderDTO.setOrderNo(xid);
    orderApi.createOrder(orderDTO);
    if(null !=type && type.equalsIgnoreCase("hot")){
      //更新操作-热点数据测试
      storageApi.reduce(orderDTO.getProductId(),orderDTO.getCount());
    }else{
      //插入操作-非热点数据
      storageApi.save(orderDTO.getOrderNo());
    }
    return "ok";
  }

  /**
   * 未使用分布式事物测试
   * @param request
   * @param orderDTO
   * @return
   */
  @PostMapping("/normal/tm/purchase")
  public String normalPurchase(HttpServletRequest request,@RequestBody OrderDTO orderDTO){
    orderApi.createOrder(orderDTO);
    String type = request.getHeader("type");
    if(null !=type && type.equalsIgnoreCase("hot")){
      //更新操作-热点数据测试
      storageApi.reduce(orderDTO.getProductId(),orderDTO.getCount());
    }else{
      //插入操作-非热点数据
      storageApi.save("0");
    }
    return "ok";
  }

}
