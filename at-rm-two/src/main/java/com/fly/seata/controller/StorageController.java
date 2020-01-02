package com.fly.seata.controller;

import com.fly.seata.service.StorageService;
import io.seata.spring.annotation.GlobalLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@RestController
public class StorageController {

  @Autowired
  private StorageService storageService;

  @GetMapping(value = "/storage/reduce/{productId}/{count}")
  public String reduce(@PathVariable("productId") long productId,@PathVariable("count") Integer count){
    int result = storageService.reduce(productId,count);
    if(result <= 0){
        throw new RuntimeException("库存扣减失败！！！");
    }
    return "ok";
  }

}
