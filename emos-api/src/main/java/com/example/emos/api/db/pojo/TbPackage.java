package com.example.emos.api.db.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TbPackage {
    private Integer id;
    private Integer userId;
    private String number;
    private String origin;
    private String destination;
    private BigDecimal price;
    private Date createTime;
}
