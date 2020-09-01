package com.miaosha.service;

import com.miaosha.error.BusinessException;
import com.miaosha.service.model.ItemModel;

import java.util.List;

/**
 * @author sxy
 * @version 1.0
 * @className miaosha
 * @date 2020/8/26
 */
public interface ItemService {

    /**
     * 创建商品
     * @param itemModel
     * @return
     * @throws BusinessException
     */
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    /**
     * 商品列表浏览
     * @return
     */
    List<ItemModel> listItem();


    /**
     * 商品详情浏览
     * @param id
     * @return
     */
    ItemModel getItemById(Integer id);

    /**
     * 库存减少
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException ;

    /**
     * 销量增加
     * @param id
     * @param amount
     * @throws BusinessException
     */
    void increaseSales(Integer id, Integer amount) throws BusinessException ;
}
