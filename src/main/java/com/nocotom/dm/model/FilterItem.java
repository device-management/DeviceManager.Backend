package com.nocotom.dm.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode
public class FilterItem {

    //@NotNull
    //@Size(min=1, max=30)
    private String key;

    //@NotNull
    //@Size(min=1, max=30)
    private Object value;

    private boolean exact = true;
}
