package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.crm.entity.Contract;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
* @author 20839
* @description 针对表【contract(合同表)】的数据库操作Mapper
* @createDate 2025-03-30 16:05:17
* @Entity .org.example.crm.entity.Contract
*/
@Mapper
public interface ContractMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Contract record);

    int insertSelective(Contract record);

    Contract selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Contract record);

    int updateByPrimaryKey(Contract record);

    List<Contract> queryContractList(String name, Integer customerId, Integer status, String startDate, String endDate, String sortField, String sortOrder, Long uid);

    int countAllContracts(Long uid);

    BigDecimal sumContractAmount(Long uid);

    BigDecimal queryAmountByDate(Date today, Long uid);
}
