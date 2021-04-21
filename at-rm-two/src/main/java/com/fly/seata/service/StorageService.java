package com.fly.seata.service;

import com.fly.seata.dao.StorageDao;
import com.fly.seata.domain.Storage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@Service
public class StorageService {

  @Autowired
  private StorageDao storageDao;

  @Transactional
  public int reduce(long productId,int count){
    return storageDao.reduce(productId,count);
  }

  @Transactional
  public  int insert(String xid){
    return storageDao.insert(xid);
  }

  public List<Storage> findByStorageByProductId(Long productId){
    return storageDao.findByStorageByProductId(productId);
  }

}
