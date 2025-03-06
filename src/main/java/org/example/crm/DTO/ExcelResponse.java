package org.example.crm.DTO;

import lombok.Data;

@Data
public class ExcelResponse {
    private String data;      // Base64编码的Excel数据
    private String fileName;  // 文件名
    
    public ExcelResponse(String data, String fileName) {
        this.data = data;
        this.fileName = fileName;
    }
}