package ru.kireev.Suppliers_And_Consumers_Test_Task.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Dto.ProductGroup;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Consumer;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Product;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Entities.Supplier;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories.ConsumerRepository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories.ProductRepository;
import ru.kireev.Suppliers_And_Consumers_Test_Task.Repositories.SupplierRepository;

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

                            Set<Product> commonItemGroups = getCommonItemGroups(consumers, suppliers);
                            return createItemGroup(commonItemGroups, consumers, suppliers);
                        }
                ));

    }

    private List<ProductGroup> createItemGroup(Set<Product> products, List<Consumer> consumers, List<Supplier> suppliers) {
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

    private Set<Product> getCommonItemGroups(List<Consumer> consumers, List<Supplier> suppliers) {

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

    public void makeReport() {
        createHtmlFile();
    }

    public void createHtmlFile() {

        /*  Корявый метод, пока что не смог создать HTML не для веб приложения с помощью шаблонизатора  */

        try (FileWriter fileWriter = new FileWriter(pathToReportFile + "/report.html")) {

            fileWriter.write("<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Report</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<h3 style=\"font-size:30px\" align=\"center\">Отчёт</h3>\n" +
                    "<table border=\"1\" style=\"width:100%; font-size:20px\">\n" +
                    "    <tr>\n" +
                    "        <th>Город</th>\n" +
                    "        <th>Продукт</th>\n" +
                    "        <th>Поставщик</th>\n" +
                    "        <th>Потребитель</th>\n" +
                    "    </tr>");

            for (Map.Entry<String, List<ProductGroup>> entry : collectDistributionData().entrySet()) {

                fileWriter.write("<tr>\n");
                //City
                fileWriter.write("<th>" + entry.getKey() + "</th>\n");

                for (ProductGroup productGroup : entry.getValue()) {

                    if (productGroup != entry.getValue().get(0)) {
                        fileWriter.write("<tr>\n<th></th>\n");
                    }

                    //Product
                    fileWriter.write("<th>" + productGroup.getProductName() + "</th>\n");

                    fileWriter.write("<th>");
                    for (String supplier : productGroup.getSuppliers()) {

                        //Supplier
                        fileWriter.write(supplier + "<br>");
                    }
                    fileWriter.write("</th>\n");


                    fileWriter.write("<th>");
                    for (String consumer : productGroup.getConsumers()) {

                        //Consumer
                        fileWriter.write(consumer + "<br>");
                    }
                    fileWriter.write("</th>\n");

                    if (productGroup != entry.getValue().get(entry.getValue().size() - 1)) {
                        fileWriter.write("</tr>\n");
                    }
                }
                fileWriter.write("</tr>\n");
            }

            fileWriter.write("</table>\n" +
                    "</body>\n" +
                    "</html>");
            fileWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
