package org.example.crm.service;

import org.example.crm.DTO.ContractStatusDTO;
import org.example.crm.entity.Contract;
import org.springframework.data.domain.Page;

import java.io.IOException;

public interface ContractService {

    Page<Contract> getContractList(String name, Integer customerId, Integer status, String startDate, String endDate, String sortField, String sortOrder, Integer pageNum, Integer pageSize);

    Contract queryContractInfo(Long id);

    boolean createContract(Contract contract);

    boolean updateContract(Contract contract);

    boolean deleteContract(Long id);

    boolean updateStatus(ContractStatusDTO contractStatusDTO);

    byte[] exportContractsToExcel() throws IOException;
}
