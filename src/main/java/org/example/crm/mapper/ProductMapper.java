package org.example.crm.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.crm.entity.Product;

import java.util.List;

/**
* @author 20839
* @description 针对表【product】的数据库操作Mapper
* @createDate 2025-03-06 22:52:57
* @Entity .org.example.crm.entity.Product
*/
@Mapper
public interface ProductMapper {

    int deleteByPrimaryKey(Long id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);


    Product findByName(String name);

    List<Product> queryProductList(String name, Integer status);

    int deleteProductsByIds(List<Long> ids);
}
