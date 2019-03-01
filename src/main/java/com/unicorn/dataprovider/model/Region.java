package com.unicorn.dataprovider.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Region {

    private Long id;

    private String name;

    private String code;

    private String link;

    private Long parent_id;

    private Integer status;

    private Integer level;

}