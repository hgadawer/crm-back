package org.example.crm.DTO;

import lombok.Data;

import java.util.List;
@Data
public class DeleteBusinessRequest {
    private List<Long> Ids;
}
