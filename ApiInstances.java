package org.example.service;

import lombok.Data;

import java.util.List;

@Data
public class ApiInstances {

    private String ip;
    private List<Long> oldInstances;

}
