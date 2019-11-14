package com.fly.seata.controller;

import com.fly.seata.service.StorageService;
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
    storageService.reduce(productId,count);
    return "ok";
  }

}
