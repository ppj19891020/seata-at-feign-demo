package com.fly.seata.common.api;

import com.fly.seata.common.dto.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@FeignClient(value = "seata-at-rm-one")
public interface OrderApi {

  @PostMapping(value = "/order/create",consumes = MediaType.APPLICATION_JSON_VALUE)
  String createOrder(@RequestBody OrderDTO order);

}
