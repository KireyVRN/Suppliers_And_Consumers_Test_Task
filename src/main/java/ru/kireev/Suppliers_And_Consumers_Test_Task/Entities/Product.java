package ru.kireev.Suppliers_And_Consumers_Test_Task.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "product")
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToMany()
    @JoinTable(name = "product_consumer",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "consumer_id")})
    private Set<Consumer> consumers;

    @ManyToMany()
    @JoinTable(name = "product_supplier",
            joinColumns = {@JoinColumn(name = "product_id")},
            inverseJoinColumns = {@JoinColumn(name = "supplier_id")})
    private Set<Supplier> suppliers;

}




