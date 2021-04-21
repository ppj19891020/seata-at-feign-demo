package com.fly.seata.common.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@FeignClient(value = "seata-at-rm-two")
public interface StorageApi {

  @GetMapping(value = "/storage/reduce/{productId}/{count}")
  String reduce(@PathVariable("productId") long productId,@PathVariable("count") Integer count);

  @GetMapping(value = "/storage/save/{xid}")
  int save(@PathVariable("xid") String xid);
}
