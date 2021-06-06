package ru.kireev.Suppliers_And_Consumers_Test_Task.Entities;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "consumer")
@Data
@Accessors(chain = true)
public class Consumer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String City;

    @ManyToMany()
    @JoinTable(name = "consumer_product",
            joinColumns = @JoinColumn(name = "consumer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
    private Set<Product> products;

}
