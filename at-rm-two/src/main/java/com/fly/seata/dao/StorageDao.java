package com.fly.seata.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
@Mapper
public interface StorageDao {

  /**
   * 减少库存
   * @param productId
   * @param count
   * @return
   */
  @Update("update storage set used = used + #{count},residue = residue - #{count} where id = #{productId} and residue > 0")
  int reduce(@Param("productId") Long productId,@Param("count") Integer count);

}
