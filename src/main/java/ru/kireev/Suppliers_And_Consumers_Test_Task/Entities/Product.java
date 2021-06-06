package ru.kireev.Suppliers_And_Consumers_Test_Task.Entities;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "product")
@Data
@Accessors(chain = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "products")
    private Set<Consumer> consumers;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "products")
    private Set<Supplier> suppliers;

}



