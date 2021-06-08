package ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Supplier;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
