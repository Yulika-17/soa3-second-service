package com.itmo.secondspring.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class House {
    private String name; //Поле может быть null
    private int year; //Максимальное значение поля: 210, Значение поля должно быть больше 0
    private long numberOfFlatsOnFloor; //Значение поля должно быть больше 0
    private long numberOfLifts; //Значение поля должно быть больше 0
}
