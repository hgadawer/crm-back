package org.example.crm.service.impl;

import org.apache.poi.xssf.usermodel.*;
import org.example.crm.DTO.ContractStatusDTO;
import org.example.crm.entity.Contract;
import org.example.crm.entity.Customer;
import org.example.crm.mapper.ContractMapper;
import org.example.crm.mapper.CustomerMapper;
import org.example.crm.service.ContractService;
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
public class ContractServiceImpl implements ContractService {
    @Autowired
    ContractMapper contractMapper;

    @Autowired
    CustomerMapper customerMapper;
    @Override
    public Page<Contract> getContractList(String name, Integer customerId, Integer status, String startDate, String endDate, String sortField, String sortOrder, Integer pageNum, Integer pageSize) {
        // 调用 Mapper 查询全部满足条件的客户数据
        List<Contract> fullList = contractMapper.queryContractList(name, customerId, status, startDate, endDate, sortField, sortOrder);
        for (Contract contract:fullList){
            contract.setCustomerName(customerMapper.selectByPrimaryKey(contract.getCustomerId()).getName());
        }
        int total = fullList.size();

        // 计算分页的起始和结束索引（页码从1开始）
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Contract> pageList;
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
    public Contract queryContractInfo(Long id) {
        return contractMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean createContract(Contract contract) {
        Date now = new Date();
        contract.setCreated(now);
        contract.setUpdated(now);
        // 插入客户数据
        int rows = contractMapper.insertSelective(contract);
        return rows > 0;
    }

    @Override
    public boolean updateContract(Contract contract) {
        // 调用 Mapper 方法进行更新，返回受影响的行数
        Date now = new Date();
        contract.setUpdated(now);
        int rows = contractMapper.updateByPrimaryKeySelective(contract);
        return rows > 0;
    }

    @Override
    public boolean deleteContract(Long id) {
        return contractMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public boolean updateStatus(ContractStatusDTO contractStatusDTO) {
        Contract contract = queryContractInfo(contractStatusDTO.getId());
        Date now = new Date();
        contract.setUpdated(now);
        contract.setStatus(contractStatusDTO.getStatus());
        return updateContract(contract);
    }

    @Override
    public byte[] exportContractsToExcel() throws IOException {
        List<Contract> contracts = contractMapper.queryContractList(null,null,null,null,null,null,null);

        for (Contract contract:contracts){
            contract.setCustomerName(customerMapper.selectByPrimaryKey(contract.getCustomerId()).getName());
        }
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("合同信息");

            String[] headers = {"ID", "姓名", "客户名", "签订日期", "到期日期","合同金额","合同描述","备注","是否签订","创建人id","创建时间","更新时间"};
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
            for (Contract contract : contracts) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(contract.getId());
                row.createCell(1).setCellValue(contract.getName());
                row.createCell(2).setCellValue(contract.getCustomerName());
                row.createCell(3).setCellValue(
                        contract.getSignDate() != null ?
                                contract.getSignDate().toString() : "");
                row.createCell(4).setCellValue(
                        contract.getExpireDate() != null ?
                                contract.getExpireDate().toString() : "");
                row.createCell(5).setCellValue(contract.getAmount().toString());
                row.createCell(6).setCellValue(contract.getDescription());
                row.createCell(7).setCellValue(contract.getRemarks());
                row.createCell(8).setCellValue(contract.getStatus()==1?"已签":"未签");
                row.createCell(9).setCellValue(contract.getCreator());
                row.createCell(10).setCellValue(
                        contract.getCreated() != null ?
                                contract.getCreated().toString() : "");
                row.createCell(11).setCellValue(
                        contract.getUpdated() != null ?
                                contract.getUpdated().toString() : "");
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
