package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.ContractStatusDTO;
import org.example.crm.DTO.ExcelResponse;
import org.example.crm.entity.Contract;
import org.example.crm.result.R;
import org.example.crm.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/contract")
public class ContractController {
    @Autowired
    ContractService contractService;

    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }

    @GetMapping("/list")
    public R getList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer customerId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder,
            HttpServletRequest request
    ){
        // 获取分页查询结果
        Page<Contract> page = contractService.getContractList(name,customerId,status,startDate,endDate,sortField,sortOrder,getCurrentId(request), pageNum, pageSize);
        // 封装返回数据：total 和 list
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotalElements());
        result.put("list", page.getContent());
        return R.OK(result);
    }

    @GetMapping("/info")
    public R queryContractInfo(Long id){
        try {
            Contract contract = contractService.queryContractInfo(id);
            return R.OK(contract);
        }catch (Exception e){
            return R.FAIL(e.getMessage());
        }
    }
    @PostMapping("/create")
    public R createContract(@RequestBody Contract contract, HttpServletRequest request){
        try {
            //从请求头中获取当前登录人的id，就是这个人创建的这个客户，存入creator字段里
            Long uid = Long.valueOf(request.getHeader("uid"));
            contract.setCreator(uid);
            boolean success = contractService.createContract(contract);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.FAIL("用户名已存在");
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("保存失败").info(e.getMessage()).build();
        }
    }
    @PutMapping("/update")
    public R updateContract(@RequestBody Contract contract){
        try {
            boolean success = contractService.updateContract(contract);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.builder().code(500).msg("更新失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("更新异常").info(e.getMessage()).build();
        }
    }

    @DeleteMapping("/delete")
    public R deleteContract(Long id){
        try {
            boolean success = contractService.deleteContract(id);
            if (success) {
                return R.OK("删除成功");
            } else {
                return R.builder().code(500).msg("删除失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("删除异常").info(e.getMessage()).build();
        }

    }

    @PutMapping("/status")
    public R updateStatus(ContractStatusDTO contractStatusDTO){
        try {
            boolean success = contractService.updateStatus(contractStatusDTO);
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
    public R exportCustomers(HttpServletRequest request){
        try {
            // 获取Excel文件字节数组
            byte[] excelBytes = contractService.exportContractsToExcel(getCurrentId(request));

            // 将字节数组转换为Base64字符串
            String base64Data = Base64.getEncoder().encodeToString(excelBytes);

            // 封装返回数据
            return R.OK(new ExcelResponse(base64Data, "合同信息.xlsx"));

        } catch (Exception e) {
            return R.FAIL("导出失败: " + e.getMessage());
        }
    }
}
