package com.fly.seata.dao;

import com.fly.seata.domain.Storage;
import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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

  /**
   * 插入库存
   * @return
   */
  @Insert("INSERT INTO `storage`(`product_id`, `total`, `used`, `residue`, `xid`) VALUES (1000, 1000000, 0, 1000000, #{xid})")
  int insert(@Param("xid") String xid);

  /**
   * 通过商品id获取商品信息
   * @param productId
   * @return
   */
  @Select("select * from storage where id = #{productId}")
  List<Storage> findByStorageByProductId(@Param("productId") Long productId);
}
