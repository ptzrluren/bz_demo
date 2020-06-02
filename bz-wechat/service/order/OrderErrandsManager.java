package com.flc.service.order;

import com.flc.enums.ExEnum;
import com.flc.util.PageData;

import java.util.Map;

/** 
 * 说明： 私人订制订单接口
 * 创建人：FLC
 * 创建时间：2018-03-17
 * @version
 */
public interface OrderErrandsManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public PageData saveCreateOrder(PageData pd)throws Exception;
	
	/**私人订制用户进入下单-查询饭点
	 * @param pd
	 * @throws Exception
	 */
	public PageData getUserPoint(PageData pd)throws Exception;
	
	/**查询私人订制订单列表
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderList(PageData pd)throws Exception;
	
	/**查看私人订制订单详情
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderDetail(PageData pd)throws Exception;
	
	/**修改订单状态
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsState(PageData pd)throws Exception;
	
	/**修改支付方式
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsPaymentMethod(PageData pd)throws Exception;
	
	/**
	 * 修改订单表，保存支付通道返回的ID
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData updateOrderErrandsChargeId(PageData pd) throws Exception;
	
	/**
	 * 信用卡支付成功，修改订单状态
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData updateOrderErrandsStateByCCPay(PageData pd) throws Exception;
	
	/**根据orderid查询私人定制金额
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderErrandsMoney(PageData pd)throws Exception;
	
	/**
	 * 极光推送快递员
	 * @param order_id
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public String jg_push(Map<String, String> map, ExEnum type, String regId) throws Exception;
	
	/**
	 * 获取快递员RegId
	 * @return
	 * @throws Exception
	 */
	public String getRegId() throws Exception;
}

