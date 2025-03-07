package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.DeleteProductRequest;
import org.example.crm.DTO.ExcelResponse;
import org.example.crm.entity.Product;
import org.example.crm.result.R;
import org.example.crm.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 获取产品列表
     */
    @GetMapping("/list")
    public R queryProductList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        try {
            Page<Product> page = productService.getProductList(name, status, pageNum, pageSize);
            // 封装返回数据：total 和 list
            Map<String, Object> result = new HashMap<>();
            result.put("total", page.getTotalElements());
            result.put("list", page.getContent());
            return R.OK(result);
        } catch (Exception e) {
            return R.FAIL("查询产品列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    public R queryProductInfo(Long id){
        try {
            Product product = productService.queryProductInfo(id);
            return R.OK(product);
        }catch (Exception e){
            return R.FAIL(e.getMessage());
        }
    }

    @PostMapping("/create")
    public R createProduct(@RequestBody Product product,HttpServletRequest request) {
        try {
            //从请求头中获取当前登录人的id，就是这个人创建的这个客户，存入creator字段里
            Long uid = Long.valueOf(request.getHeader("uid"));
            product.setCreator(uid);
            boolean success = productService.createProduct(product);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.FAIL("用户名已存在");
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("保存失败").info(e.getMessage()).build();
        }
    }

    @DeleteMapping("/delete")
    public R deleteProduct(@RequestBody DeleteProductRequest deleteProductRequest){
        try {
            boolean success = productService.deleteProducts(deleteProductRequest.getIds());
            if (success) {
                return R.OK("删除成功");
            } else {
                return R.builder().code(500).msg("删除失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("删除异常").info(e.getMessage()).build();
        }
    }

    @PutMapping("/update")
    public R updateProduct(@RequestBody Product product){
        try {
            boolean success = productService.updateProduct(product);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.builder().code(500).msg("更新失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("更新异常").info(e.getMessage()).build();
        }
    }

    @GetMapping("/export")
    public R exportProducts(){
        try {
            // 获取Excel文件字节数组
            byte[] excelBytes = productService.exportCustomersToExcel();

            // 将字节数组转换为Base64字符串
            String base64Data = Base64.getEncoder().encodeToString(excelBytes);

            // 封装返回数据
            return R.OK(new ExcelResponse(base64Data, "产品信息.xlsx"));

        } catch (Exception e) {
            return R.FAIL("导出失败: " + e.getMessage());
        }
    }
}
