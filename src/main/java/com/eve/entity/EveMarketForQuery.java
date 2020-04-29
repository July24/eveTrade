package com.eve.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class EveMarketForQuery implements Serializable {
    List<String> types;
}
