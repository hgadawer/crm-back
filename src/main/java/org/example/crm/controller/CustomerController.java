package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.CustomerIdAndName;
import org.example.crm.DTO.DeleteCustomerRequest;
import org.example.crm.DTO.ExcelResponse;
import org.example.crm.entity.Customer;
import org.example.crm.result.R;
import org.example.crm.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    CustomerService customerService;
    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }


    /**
     * 根据查询条件分页查询客户列表
     * GET /customer/list?name=xxx&source=xxx&pageNum=1&pageSize=10...
     */
    @GetMapping("/list")
    public R queryCustomerList(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String source,
                               @RequestParam(required = false) String industry,
                               @RequestParam(required = false) String level,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "10") int pageSize,
                               HttpServletRequest request){
        // 获取分页查询结果
        Page<Customer> page = customerService.getCustomerList(name, getCurrentId(request),source, industry, level, status, pageNum, pageSize);
        // 封装返回数据：total 和 list
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotalElements());
        result.put("list", page.getContent());
        return R.OK(result);
    }

    /**
     * 根据客户id获取对应客户信息
     */
    @GetMapping("/info")
    public R queryCustomerInfo(Long id){
        try {
            Customer customer = customerService.queryCustomerInfo(id);
            return R.OK(customer);
        }catch (Exception e){
            return R.FAIL(e.getMessage());
        }
    }

    @GetMapping("/all")
    public R queryAllCustomerIdsAndNames(HttpServletRequest request){
        List<CustomerIdAndName> customerIdAndNameList =  customerService.getAllCustomerIdsAndNames(getCurrentId(request));
        return R.OK(customerIdAndNameList);
    }

    /**
     * 创建客户
     * @param customer
     * @return
     * @param request :用来获取请求头中的uid，因为要存入新建客户的creator字段
     */
    @PostMapping("/create")
    public R createCustomer(@RequestBody Customer customer, HttpServletRequest request){
        try {
            //从请求头中获取当前登录人的id，就是这个人创建的这个客户，存入creator字段里
            Long uid = Long.valueOf(request.getHeader("uid"));
            customer.setCreator(uid);
            boolean success = customerService.createCustomer(customer);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.FAIL("用户名已存在");
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("保存失败").info(e.getMessage()).build();
        }
    }

    /**
     *更新客户信息
     * @return
     */

    @PutMapping("/update")
    public R updateCustomer(@RequestBody Customer customer){
        try {
            boolean success = customerService.updateCustomer(customer);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.builder().code(500).msg("更新失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("更新异常").info(e.getMessage()).build();
        }
    }

    /**
     *
     * @param deleteCustomerRequest:删除客户的DTO，里面存入删除的用户的列表
     * @return
     */
    @DeleteMapping("/delete")
    public R deleteCustomer(@RequestBody DeleteCustomerRequest deleteCustomerRequest){
        try {
            boolean success = customerService.deleteCustomers(deleteCustomerRequest.getIds());
            if (success) {
                return R.OK("删除成功");
            } else {
                return R.builder().code(500).msg("删除失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("删除异常").info(e.getMessage()).build();
        }

    }

    @GetMapping("/export")
    public R exportCustomers(HttpServletRequest request){
        try {
            // 获取Excel文件字节数组
            byte[] excelBytes = customerService.exportCustomersToExcel(getCurrentId(request));

            // 将字节数组转换为Base64字符串
            String base64Data = Base64.getEncoder().encodeToString(excelBytes);

            // 封装返回数据
            return R.OK(new ExcelResponse(base64Data, "客户信息.xlsx"));

        } catch (Exception e) {
            return R.FAIL("导出失败: " + e.getMessage());
        }
    }
}
