package ru.kireev.Suppliers_And_Consumers_Test_Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import ru.kireev.Suppliers_And_Consumers_Test_Task.Service.DistributionService;

@ComponentScan
@SpringBootApplication
public class SuppliersAndConsumersTestTaskApplication {

    @Autowired
    private DistributionService distributionService;

    public static void main(String[] args) {
        SpringApplication.run(SuppliersAndConsumersTestTaskApplication.class, args);
    }

}
