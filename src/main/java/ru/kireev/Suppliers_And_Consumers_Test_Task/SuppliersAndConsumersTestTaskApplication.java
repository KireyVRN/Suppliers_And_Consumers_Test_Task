package ru.kireev.Suppliers_And_Consumers_Test_Task;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ru.kireev.Suppliers_And_Consumers_Test_Task.Service.DistributionService;


@SpringBootApplication
@RequiredArgsConstructor
public class SuppliersAndConsumersTestTaskApplication implements CommandLineRunner {

    @Autowired
    private final DistributionService distributionService;

    public static void main(String[] args) {
        SpringApplication.run(SuppliersAndConsumersTestTaskApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        distributionService.makeReport();
    }
}
