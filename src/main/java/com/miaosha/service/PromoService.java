package com.miaosha.service;

import com.miaosha.service.model.PromoModel;

/**
 * @author sxy
 * @version 1.0
 * @className PromoService
 * @date 2020/8/26 16:56
 */

public interface PromoService {
    /**
     * 获取即将进行的或正在进行的秒杀活动
     * @param itemId
     * @return
     */
    PromoModel getPromoByItemId(Integer itemId);
}
