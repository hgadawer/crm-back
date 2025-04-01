package org.example.crm.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.crm.DTO.DeleteBusinessRequest;
import org.example.crm.entity.Business;
import org.example.crm.entity.Customer;
import org.example.crm.result.R;
import org.example.crm.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/business")
public class BusinessController {
    @Autowired
    private BusinessService businessService;
    //获取当前使用系统的用户ID
    private Long getCurrentId(HttpServletRequest request){
        return Long.valueOf(request.getHeader("uid"));
    }

    /**
     * 新建业务
     * @param business
     * @param request
     * @return
     */

    @PostMapping("/create")
    public R createBusiness(@RequestBody Business business, HttpServletRequest request){
        try{
            //测试
            System.out.println(business.getProductlist());
            business.setCreator(getCurrentId(request));
            boolean success = businessService.createBusiness(business);

            if(success){
                return R.OK("创建成功");
            }else {
                return R.FAIL("创建失败");
            }
        }catch (Exception e){
            return R.builder().code(500).msg(e.getMessage()).build();
        }
    }

    @GetMapping("/list")
    public R listBusiness(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request
    ){
        // 获取分页查询结果
        Page<Business> page = businessService.getBusinessList(name,getCurrentId(request),pageNum, pageSize);
        // 封装返回数据：total 和 list
        Map<String, Object> result = new HashMap<>();
        result.put("total", page.getTotalElements());
        result.put("list", page.getContent());
        return R.OK(result);
    }

    @DeleteMapping("/delete")
    public R deleteBusiness(@RequestBody DeleteBusinessRequest deleteBusinessRequest){
        try {
            boolean success = businessService.deleteBusinesses(deleteBusinessRequest.getIds());
            if (success) {
                return R.OK("删除成功");
            } else {
                return R.builder().code(500).msg("删除失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("删除异常").info(e.getMessage()).build();
        }

    }

    @GetMapping("/info")
    public R queryBusinessInfo(Long id){
        try {
            Business business = businessService.queryBusinessInfo(id);
            return R.OK(business);
        }catch (Exception e){
            return R.FAIL(e.getMessage());
        }
    }

    @PutMapping("/update")
    public R updateBusiness(@RequestBody Business business){
        try {
            boolean success = businessService.updateBusiness(business);
            if (success) {
                return R.OK("保存成功");
            } else {
                return R.builder().code(500).msg("更新失败").info(null).build();
            }
        } catch (Exception e) {
            return R.builder().code(500).msg("更新异常").info(e.getMessage()).build();
        }
    }

}
