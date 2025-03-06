package org.example.crm.DTO;

import lombok.Data;

import java.util.List;

@Data
public class DeleteCustomerRequest {
    private List<Long> Ids;
}