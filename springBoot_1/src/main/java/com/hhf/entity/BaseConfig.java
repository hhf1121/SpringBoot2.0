package com.hhf.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Data
@Slf4j
public class BaseConfig {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    private String configName;

    private String configCode;

    private String color;

    private String typeLabel;

    private Integer typeValue;

    @TableLogic
    private Integer isDelete;

    private Date lastTime;

    @TableField(exist = false)
    private String label;

    @TableField(exist = false)
    private Integer key;

}