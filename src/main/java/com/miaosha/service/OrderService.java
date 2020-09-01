package com.miaosha.service;

import com.miaosha.error.BusinessException;
import com.miaosha.service.model.OrderModel;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/26
 */
public interface OrderService {

    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException;
}
