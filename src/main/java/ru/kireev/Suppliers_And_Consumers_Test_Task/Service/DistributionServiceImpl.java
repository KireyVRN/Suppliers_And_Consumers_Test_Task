package ru.kireev.Suppliers_And_Consumers_Test_Task.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Dto.ProductGroup;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Consumer;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Product;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Supplier;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repository.ConsumerRepository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repository.ProductReposiroty;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repository.SupplierRepository;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistributionServiceImpl implements DistributionService {

    private final SupplierRepository supplierRepository;

    private final ConsumerRepository consumerRepository;

    private final ProductReposiroty productReposiroty;

    @Value("${pathToReportFile}")
    private String pathToReportFile;

    @PostConstruct
    public void start() {

        init();
        createHtmlReport();

    }

    public void init() {

        Consumer consumer1 = consumerRepository.saveAndFlush(new Consumer().setName("Пятёрочка").setCity("Воронеж"));
        Consumer consumer2 = consumerRepository.saveAndFlush(new Consumer().setName("Магнит").setCity("Москва"));
        Consumer consumer3 = consumerRepository.saveAndFlush(new Consumer().setName("ВкусВилл").setCity("Москва"));

        Supplier supplier1 = supplierRepository.saveAndFlush(new Supplier().setName("A").setCity("Москва"));
        Supplier supplier2 = supplierRepository.saveAndFlush(new Supplier().setName("B").setCity("Воронеж"));
        Supplier supplier3 = supplierRepository.saveAndFlush(new Supplier().setName("C").setCity("Воронеж"));

        productReposiroty.save(new Product()
                .setName("Молоко")
                .setConsumers(Set.of(consumer1, consumer2))
                .setSuppliers(Set.of(supplier1)));

        productReposiroty.save(new Product()
                .setName("Яблоки")
                .setConsumers(Set.of(consumer1))
                .setSuppliers(Set.of(supplier3)));

        productReposiroty.save(new Product()
                .setName("Орехи")
                .setConsumers(Set.of(consumer3, consumer2))
                .setSuppliers(Set.of(supplier1, supplier2, supplier3)));

        productReposiroty.save(new Product()
                .setName("Сыр")
                .setConsumers(Set.of(consumer3))
                .setSuppliers(Set.of(supplier2)));

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

                            Set<Product> commonItemGroups = getCommonItemGroups(consumers, suppliers);
                            return createItemGroup(commonItemGroups, consumers, suppliers);
                        }
                ));

    }

    private List<ProductGroup> createItemGroup(Set<Product> items, List<Consumer> consumers, List<Supplier> suppliers) {

        return items
                .stream()
                .map(item -> new ProductGroup()
                        .setProductName(item.getName())
                        .setConsumers(consumers
                                .stream()
                                .filter(c -> c.getProducts().contains(item))
                                .map(Consumer::getName)
                                .collect(Collectors.toSet()))
                        .setSuppliers(suppliers.stream()
                                .filter(c -> c.getProducts().contains(item))
                                .map(Supplier::getName)
                                .collect(Collectors.toSet())))
                .collect(Collectors.toList());
    }

    private Set<Product> getCommonItemGroups(List<Consumer> consumers, List<Supplier> suppliers) {

        Set<Product> consumerItems = consumers.stream()
                .map(Consumer::getProducts)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<Product> supplierItems = suppliers.stream()
                .map(Supplier::getProducts)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return consumerItems
                .stream()
                .filter(supplierItems::contains)
                .collect(Collectors.toSet());

    }

    private Set<String> getCommonCities(

            Map<String, List<Consumer>> consumersByCity,
            Map<String, List<Supplier>> suppliersByCity) {

        Set<String> consumersCities = consumersByCity.keySet();
        Set<String> suppliersCities = suppliersByCity.keySet();

        return consumersCities
                .stream()
                .filter(suppliersCities::contains)
                .collect(Collectors.toSet());
    }

    private void createHtmlReport() {

   /*  Очень кривой метод, не было времени + нерабочий. Не смог  с ходу реализовать
      */

//        Map<String, List<ProductGroup>> distributionData = collectDistributionData();
//
//        try (FileWriter writer = new FileWriter(pathToReportFile, false)) {
//
//            writer.write("<html><head><title>Report</title></head><body><table>");
//
//            for (Map.Entry<String, List<ProductGroup>> entry : distributionData.entrySet()) {
//
//                writer.write("<tr>");
//                writer.write("<th>");
//                writer.write(entry.getKey());                     // Cтолбец Город
//                writer.write("<th>");
//
//                for (ProductGroup productGroup : entry.getValue()
//                ) {
//                    writer.write("<th>");
//                    writer.write(productGroup.getProductName());
//                    writer.write("\n");                                     // Cтолбец  Продукт
//                    writer.write("<th>");
//
//                    writer.write("<th>");
//                    Set<String> suppliers = productGroup.getSuppliers();
//                    for (String supplier : suppliers) {
//                        writer.write(supplier);                            // Cтолбец  Поставщики
//                        writer.write("\n");
//                    }
//                    writer.write("<th>");
//
//                    writer.write("<th>");
//                    Set<String> comsumers = productGroup.getConsumers();
//                    for (String consumer : comsumers) {                       // Cтолбец  Потребители
//                        writer.write(consumer);
//                        writer.write("\n");
//                    }
//                    writer.write("<th>");
//
//                }
//
//
//                writer.write("<tr>");
//            }
//
//
//            writer.write("</table></body></html>");
//            writer.flush();
//        } catch (IOException ex) {
//            System.out.println(ex.getMessage());
//        }
    }


}
