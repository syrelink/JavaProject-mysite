package com.syr.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("tb_shop")
public class Shop {
    private Long id;
    private String name;
    private String images;
    private String address;
    private String area;
    private Long typeId;
    private Long avgPrice;
    private Long sold;
    private Long comments;
    private Long score;
    private String openHours;
    private String createTime;
    private String updateTime;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public Long getTypeId() { return typeId; }
    public void setTypeId(Long typeId) { this.typeId = typeId; }
    public Long getAvgPrice() { return avgPrice; }
    public void setAvgPrice(Long avgPrice) { this.avgPrice = avgPrice; }
    public Long getSold() { return sold; }
    public void setSold(Long sold) { this.sold = sold; }
    public Long getComments() { return comments; }
    public void setComments(Long comments) { this.comments = comments; }
    public Long getScore() { return score; }
    public void setScore(Long score) { this.score = score; }
    public String getOpenHours() { return openHours; }
    public void setOpenHours(String openHours) { this.openHours = openHours; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }
}
