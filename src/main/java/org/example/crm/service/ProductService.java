package org.example.crm.service;

import org.example.crm.entity.Product;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    Page<Product> getProductList(String name, Integer status,Long uid, int pageNum, int pageSize);

    boolean createProduct(Product product);

    Product queryProductInfo(Long id);

    boolean deleteProducts(List<Long> ids);

    boolean updateProduct(Product product);

    byte[] exportCustomersToExcel(Long uid) throws IOException;
}
