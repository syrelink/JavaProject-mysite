package com.syr.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
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

}
