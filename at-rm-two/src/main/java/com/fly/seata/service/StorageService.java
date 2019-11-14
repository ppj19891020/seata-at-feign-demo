package com.fly.seata.service;

import com.fly.seata.dao.StorageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@Service
public class StorageService {

  @Autowired
  private StorageDao storageDao;

  public int reduce(long productId,int count){
    return storageDao.reduce(productId,count);
  }

}
