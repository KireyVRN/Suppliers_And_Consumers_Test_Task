package ru.kireev.Suppliers_And_Consumers_Test_Task;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Service.DistributionService;

@SpringBootApplication
@RequiredArgsConstructor
public class SuppliersAndConsumersTestTaskApplication implements ApplicationRunner {

    private final DistributionService distributionService;

    public static void main(String[] args) {
        SpringApplication.run(SuppliersAndConsumersTestTaskApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        distributionService.makeReport();
    }
}
