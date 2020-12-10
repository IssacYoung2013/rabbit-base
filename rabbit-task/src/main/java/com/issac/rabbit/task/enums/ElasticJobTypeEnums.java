package com.issac.rabbit.task.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author: ywy
 * @date: 2020-12-08
 * @desc:
 */
@AllArgsConstructor
@Getter
public enum ElasticJobTypeEnums {
    SIMPLE("SimpleJob", "简单类型job"),
    DATAFLOW("DataflowJob", "流式类型job"),
    SCRIPT("ScriptJob", "脚本类型job");

    private String type;

    private String desc;
}
