package com.transmartx.hippo.mapper;

import com.transmartx.hippo.dto.ProductDto;
import org.apache.ibatis.annotations.Param;

/**
 * @author: letxig
 */
public interface ProductMapper {

    ProductDto selectByProductId(@Param("productId") String productId);

}
