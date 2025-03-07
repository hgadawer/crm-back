package org.example.crm.service.impl;

import org.apache.poi.xssf.usermodel.*;
import org.example.crm.entity.Product;
import org.example.crm.mapper.ProductMapper;
import org.example.crm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;

    @Override
    public Page<Product> getProductList(String name, Integer status, int pageNum, int pageSize) {
        // 调用 Mapper 查询全部满足条件的客户数据
        List<Product> fullList = productMapper.queryProductList(name, status);
        int total = fullList.size();

        // 计算分页的起始和结束索引（页码从1开始）
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Product> pageList;
        if (fromIndex >= total) {
            pageList = new ArrayList<>();
        } else {
            pageList = fullList.subList(fromIndex, toIndex);
        }
        // 构造 Pageable 对象（注意：PageRequest 的页码从0开始，所以 pageNum - 1）
        PageRequest pageable = PageRequest.of(pageNum - 1, pageSize);
        // 返回 PageImpl 对象
        return new PageImpl<>(pageList, pageable, total);
    }

    @Override
    public boolean createProduct(Product product) {
        // 根据产品名称查询是否已存在相同产品
        Product existing = productMapper.findByName(product.getName());
        if (existing != null) {
            return false;
        }
        Date now = new Date();
        product.setCreated(now);
        product.setUpdated(now);
        // 插入客户数据
        int rows = productMapper.insertSelective(product);
        return rows > 0;
    }

    @Override
    public Product queryProductInfo(Long id) {
        return productMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean deleteProducts(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int rows = productMapper.deleteProductsByIds(ids);
        return rows > 0;
    }

    @Override
    public boolean updateProduct(Product product) {
        // 调用 Mapper 方法进行更新，返回受影响的行数
        Date now = new Date();
        product.setUpdated(now);
        int rows = productMapper.updateByPrimaryKeySelective(product);
        return rows > 0;
    }

    @Override
    public byte[] exportCustomersToExcel() throws IOException {
        List<Product> products = productMapper.queryProductList(null, null);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("客户信息");

            String[] headers = {"ID", "产品名称", "是否上架", "产品类型","产品单位","产品编码","产品价格","产品描述", "创建时间","最近更新时间"};
            XSSFRow headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                XSSFCell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                XSSFCellStyle style = workbook.createCellStyle();
                XSSFFont font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowNum = 1;
            for (Product product : products) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getStatus()==1?"上架":"下架");
                row.createCell(3).setCellValue(product.getType());
                row.createCell(4).setCellValue(product.getUnit());
                row.createCell(5).setCellValue(product.getCode());
                row.createCell(6).setCellValue(product.getDescription());
                row.createCell(7).setCellValue(String.valueOf(product.getPrice()));
                row.createCell(8).setCellValue(
                        product.getCreated() != null ?
                                product.getCreated().toString() : "");
                row.createCell(9).setCellValue(
                        product.getCreated() != null ?
                                product.getUpdated().toString() : "");

            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
