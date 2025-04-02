package org.example.crm.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.poi.xssf.usermodel.*;
import org.example.crm.DTO.CustomerIdAndName;
import org.example.crm.DTO.SendEmailParamDTO;
import org.example.crm.entity.Customer;
import org.example.crm.entity.MailConfig;
import org.example.crm.entity.Notice;
import org.example.crm.mapper.CustomerMapper;
import org.example.crm.mapper.MailConfigMapper;
import org.example.crm.mapper.NoticeMapper;
import org.example.crm.result.R;
import org.example.crm.service.CustomerService;
import org.example.crm.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    NoticeMapper noticeMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    MailConfigMapper mailConfigMapper;

    @Autowired
    MailService mailService;

    @Override
    public Page<Customer> getCustomerList(String name, Long uid,String source, String industry, String level, String status, int pageNum, int pageSize) {
        // 调用 Mapper 查询全部满足条件的客户数据
        List<Customer> fullList = customerMapper.queryCustomerList(name, uid,source, industry, level, status);
        int total = fullList.size();

        // 计算分页的起始和结束索引（页码从1开始）
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<Customer> pageList;
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
    public Customer queryCustomerInfo(Long id) {

        return customerMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean createCustomer(Customer customer) {
        // 根据客户名称查询是否已存在相同客户
        Customer existing = customerMapper.findByName(customer.getName());
        if (existing != null) {
            return false;
        }
        Date now = new Date();
        customer.setCreated(now);
        customer.setUpdated(now);
        // 插入客户数据
        int rows = customerMapper.insertSelective(customer);
        if(rows>0){
            //产生通知
            Notice notice = new Notice();
            notice.setCreator(customer.getCreator());
            notice.setContent("你创建了新客户: "+customer.getName());
            notice.setCreated(now);
            notice.setUpdated(now);
            notice.setStatus(2);
            noticeMapper.insertSelective(notice);
        }
        return rows > 0;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        // 调用 Mapper 方法进行更新，返回受影响的行数
        Date now = new Date();
        customer.setUpdated(now);
        int rows = customerMapper.updateByPrimaryKeySelective(customer);

        if(rows > 0){
            Customer temp = customerMapper.selectByPrimaryKey(customer.getId());
            //产生通知
            Notice notice = new Notice();
            notice.setCreator(temp.getCreator());
            notice.setContent("你更新了客户: "+temp.getName());
            notice.setCreated(now);
            notice.setUpdated(now);
            notice.setStatus(2);
            noticeMapper.insertSelective(notice);
        }
        return rows > 0;
    }

    @Override
    public boolean deleteCustomers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        int rows = customerMapper.deleteByIds(ids);
        return rows > 0;
    }

    @Override
    public byte[] exportCustomersToExcel(Long uid) throws IOException {
        List<Customer> customers = customerMapper.queryCustomerList(null, uid, null,null,null,null);

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("客户信息");

            String[] headers = {"ID", "姓名", "邮箱", "电话", "创建时间"};
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
            for (Customer customer : customers) {
                XSSFRow row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getEmail());
                row.createCell(3).setCellValue(customer.getPhone());
                row.createCell(4).setCellValue(
                        customer.getCreated() != null ?
                                customer.getCreated().toString() : "");
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Override
    public List<CustomerIdAndName> getAllCustomerIdsAndNames(Long uid) {

        return  customerMapper.selectAllCustomerIdsAndNames(uid);
    }

    @Override
    public R sendMail(SendEmailParamDTO sendEmailParamDTO, Long uid) throws MessagingException, IOException {
        MailConfig mailConfig = mailConfigMapper.selectByCreatorId(uid);
        if(!mailService.checkConfig(uid)){
            return R.builder().code(50002).msg("邮件服务检验未通过").build();
        }
        if(mailConfig == null){
            return R.builder().code(50003).msg(" 邮件配置不存在").build();
        }
        //配置发送人信息
        JavaMailSenderImpl sender = new JavaMailSenderImpl() {{
            setHost(mailConfig.getStmp());
            setPort(587); // 强制使用587端口
            setUsername(mailConfig.getEmail());
            setPassword(mailConfig.getAuthCode());
            setJavaMailProperties(new Properties() {{
                put("mail.smtp.auth",  "true");
                put("mail.smtp.starttls.enable",  "true");
                put("mail.smtp.starttls.required",  "true");
//                put("mail.smtp.ssl.protocols",  "TLSv1.2");
//                put("mail.smtp.ssl.ciphersuites",
//                        "TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256:TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
//                put("mail.smtp.dane.enable",  "true"); // DANE协议支持
            }});
        }};
        //配置接收人信息
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailConfig.getEmail());
        helper.setTo(sendEmailParamDTO.getReceiver());
        helper.setSubject(sendEmailParamDTO.getSubject());
        helper.setText(sendEmailParamDTO.getContent(),  true);
        if (sendEmailParamDTO.getAttachment()  != null) {
            InputStream is = sendEmailParamDTO.getAttachment().startsWith("http")
                    ? new URL(sendEmailParamDTO.getAttachment()).openStream()
                    : Files.newInputStream(Paths.get(sendEmailParamDTO.getAttachment()));

            helper.addAttachment("file",  new InputStreamResource(is));
        }
        sender.send(message);
        return R.OK("邮件发送成功");
    }

}
