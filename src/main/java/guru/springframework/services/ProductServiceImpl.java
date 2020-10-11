package guru.springframework.services;

import guru.springframework.domain.Product;
import guru.springframework.repositories.ProductRepository;
import guru.springframework.services.jms.JmsTextMessageService;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final JmsTextMessageService jmsTextMessageService;
    private final CounterService counterService;
    private final GaugeService gaugeService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              JmsTextMessageService jmsTextMessageService,
                              CounterService counterService,
                              GaugeService gaugeService) {
        this.productRepository = productRepository;
        this.jmsTextMessageService = jmsTextMessageService;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }

    @Override
    public Product getProduct(Integer id) {
        jmsTextMessageService.sendTextMessage("Fetching Product ID: " + id);
        counterService.increment("guru.springframework.services.ProductService.getproduct");
        return productRepository.findOne(id);
    }

    @Override
    public List<Product> listProducts() {
        jmsTextMessageService.sendTextMessage("Listing Products");

        counterService.increment("guru.springframework.services.ProductService.listProducts");
        gaugeService.submit("guru.springframework.services.listProducts.ProductService.pageViewsPerMin", 6);

        return IteratorUtils.toList(productRepository.findAll().iterator());
    }

}
