package ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
