package com.nocotom.dm.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Map;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Device {

    @NotNull
    @Size(min=2, max=30)
    @Id
    private String id;

    @NotNull
    @Size(min=2, max=30)
    private String name;

    @NotNull
    @Size(min=2, max=30)
    private String type;

    @NotNull
    private Map<String, Object> configuration;

    @NotNull
    private Map<String, Object> state;
}
