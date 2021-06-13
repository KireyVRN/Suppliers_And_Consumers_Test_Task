package ru.kireev.Suppliers_And_Consumers_Test_Task.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Dto.ProductGroup;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Consumer;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Product;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Supplier;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories.ConsumerRepository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories.ProductRepository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories.SupplierRepository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.config.ThymeLeafConfig;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistributionServiceImpl implements DistributionService {

    private final ConsumerRepository consumerRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final ThymeLeafConfig thymeLeafConfig;

    @Value("${pathToReportFile}")
    private String pathToReportFile;

    @PostConstruct
    public void start() {
        init();
    }

    public void init() {

        Consumer consumer1 = consumerRepository.save(new Consumer().setName("Пятёрочка").setCity("Воронеж"));
        Consumer consumer2 = consumerRepository.save(new Consumer().setName("Магнит").setCity("Москва"));
        Consumer consumer3 = consumerRepository.save(new Consumer().setName("ВкусВилл").setCity("Москва"));
        Consumer consumer4 = consumerRepository.save(new Consumer().setName("Ларек N22").setCity("Ростов"));
        Supplier supplier1 = supplierRepository.save(new Supplier().setName("ООО \"Сладкий дом\"").setCity("Москва"));
        Supplier supplier2 = supplierRepository.save(new Supplier().setName("ИП \"Григорян\"").setCity("Воронеж"));
        Supplier supplier3 = supplierRepository.save(new Supplier().setName("ООО \"Продуктовый рай\"").setCity("Воронеж"));
        Supplier supplier4 = supplierRepository.save(new Supplier().setName("ИП \"Василькова\"").setCity("Ростов"));

        productRepository.save(new Product()
                .setName("Молоко")
                .setConsumers(Set.of(consumer1, consumer2))
                .setSuppliers(Set.of(supplier1)));

        productRepository.save(new Product()
                .setName("Яблоки")
                .setConsumers(Set.of(consumer1))
                .setSuppliers(Set.of(supplier3)));

        productRepository.save(new Product()
                .setName("Орехи")
                .setConsumers(Set.of(consumer3, consumer2))
                .setSuppliers(Set.of(supplier1, supplier2, supplier3)));

        productRepository.save(new Product()
                .setName("Сыр")
                .setConsumers(Set.of(consumer3, consumer4))
                .setSuppliers(Set.of(supplier2, supplier4)));
    }

    public Map<String, List<ProductGroup>> collectDistributionData() {

        Map<String, List<Consumer>> consumersByCity = consumerRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Consumer::getCity));

        Map<String, List<Supplier>> suppliersByCity = supplierRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Supplier::getCity));

        return getCommonCities(consumersByCity, suppliersByCity)
                .stream()
                .collect(Collectors.toMap(Function.identity(),
                        city -> {
                            List<Consumer> consumers = consumersByCity.get(city);
                            List<Supplier> suppliers = suppliersByCity.get(city);
                            Set<Product> commonProducts = getCommonProducts(consumers, suppliers);
                            return createProductGroup(commonProducts, consumers, suppliers);
                        }
                ));
    }

    private List<ProductGroup> createProductGroup(Set<Product> products, List<Consumer> consumers, List<Supplier> suppliers) {

        return products
                .stream()
                .map(product -> new ProductGroup()
                        .setProductName(product.getName())
                        .setConsumers(consumers
                                .stream()
                                .filter(c -> c.getProducts().contains(product))
                                .map(Consumer::getName)
                                .collect(Collectors.toSet()))
                        .setSuppliers(suppliers.stream()
                                .filter(c -> c.getProducts().contains(product))
                                .map(Supplier::getName)
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    private Set<Product> getCommonProducts(List<Consumer> consumers, List<Supplier> suppliers) {

        Set<Product> consumerProducts = consumers.stream()
                .map(Consumer::getProducts)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<Product> supplierProducts = suppliers.stream()
                .map(Supplier::getProducts)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return consumerProducts
                .stream()
                .filter(supplierProducts::contains)
                .collect(Collectors.toSet());
    }

    private Set<String> getCommonCities(Map<String, List<Consumer>> consumersByCity, Map<String, List<Supplier>> suppliersByCity) {

        Set<String> consumersCities = consumersByCity.keySet();
        Set<String> suppliersCities = suppliersByCity.keySet();

        return consumersCities
                .stream()
                .filter(suppliersCities::contains)
                .collect(Collectors.toSet());
    }

    public void makeReport() {
        createHtmlFile();
    }

    private void createHtmlFile() {

        Context thymeLeafContext = new Context();

        try (FileWriter fileWriter = new FileWriter(pathToReportFile + "/report.html")) {

            thymeLeafContext.setVariable("d", collectDistributionData());
            thymeLeafContext.setVariable("path", pathToReportFile);
            thymeLeafConfig.templateEngine().process("templates/report.html", thymeLeafContext, fileWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
