package ru.otus.model.dto;

import lombok.Data;

@Data
public class ClientCreateDto {
    private String name;
    private String street;
    private String phones;
}
