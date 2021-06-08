package ru.kireev.Suppliers_And_Consumers_Test_Task.Entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "consumer")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(exclude = "products")
public class Consumer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @ManyToMany(mappedBy = "consumers", fetch = FetchType.EAGER)
    private Set<Product> products;

}
