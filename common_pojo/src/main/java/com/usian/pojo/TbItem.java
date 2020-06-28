package com.usian.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
@ApiModel//生成文档告诉这个pojo接收参数
public class TbItem {
    @ApiModelProperty(hidden = true) //id是在数据库自动生成，隐藏一下
    private Long id;

    @ApiModelProperty(name = "title",dataType = "String",value = "商品描述信息")//用对象接收参数时，描述对象的一个字段
    private String title;

    @ApiModelProperty(name = "sellPoint",dataType = "String",value = "商品规格参数信息")//用对象接收参数时，描述对象的一个字段
    private String sellPoint;

    @ApiModelProperty(name = "price",dataType = "Long",value = "商品价格信息")//用对象接收参数时，描述对象的一个字段
    private Long price;

    @ApiModelProperty(name = "num",dataType = "Integer",value = "商品库存数量信息")//用对象接收参数时，描述对象的一个字段
    private Integer num;

    private String barcode;

    private String image;

    private Long cid;

    private Byte status;

    private Date created;

    private Date updated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSellPoint() {
        return sellPoint;
    }

    public void setSellPoint(String sellPoint) {
        this.sellPoint = sellPoint == null ? null : sellPoint.trim();
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode == null ? null : barcode.trim();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}