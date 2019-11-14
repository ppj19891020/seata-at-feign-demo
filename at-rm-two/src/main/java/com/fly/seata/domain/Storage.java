package com.fly.seata.domain;

/**
 * @author: peijiepang
 * @date 2019-11-13
 * @Description:
 */
public class Storage {

  /**
   * 主键
   */
  private Long id;

  /**
   * 商品id
   */
  private Long productId;

  /**
   * 总库存
   */
  private Integer total;

  /**
   * 已用库存
   */
  private Integer used;

  /**
   * 剩余库存
   */
  private Integer residue;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public Integer getUsed() {
    return used;
  }

  public void setUsed(Integer used) {
    this.used = used;
  }

  public Integer getResidue() {
    return residue;
  }

  public void setResidue(Integer residue) {
    this.residue = residue;
  }
}
