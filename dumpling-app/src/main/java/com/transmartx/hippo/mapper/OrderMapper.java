package com.transmartx.hippo.mapper;

import com.transmartx.hippo.dto.OrderDto;
import org.apache.ibatis.annotations.Param;

/**
 * @author: letxig
 */
public interface OrderMapper {

    int insertOne(OrderDto dto);

    OrderDto selectByOrderId(@Param("orderId") String orderId);

    int updateOrderStatus(@Param("orderId") String orderId, @Param("orderStatus") int orderStatus, @Param("clientIp") String clientIp, @Param("clientUA") String clientUA);

    int updateOrderStatusWithException(@Param("orderId") String orderId, @Param("orderStatus") int orderStatus, @Param("exception") String exception);

    int updateUpstreamOrderId(@Param("orderId") String orderId, @Param("upstreamOrderId") String upstreamOrderId, @Param("orderStatus") int orderStatus, @Param("exception") String exception);

}
