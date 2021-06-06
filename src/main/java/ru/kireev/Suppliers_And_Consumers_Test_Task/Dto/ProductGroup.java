package ru.kireev.Suppliers_And_Consumers_Test_Task.Dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class ProductGroup {

    private String productName;
    private Set<String> consumers;
    private Set<String> suppliers;

}