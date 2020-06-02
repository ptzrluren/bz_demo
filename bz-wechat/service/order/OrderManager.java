package com.flc.service.order;

import com.flc.util.PageData;

public interface OrderManager {

	/**
	 * 查询订单
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData createOrder(PageData pd) throws Exception;
	
	/**
	 * 使用优惠券
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData useCoupon(PageData pd) throws Exception;
	
	/**
	 * 查询订单详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getOrderDetail(PageData pd) throws Exception;
	
	/**
	 * 查询订单列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getOrderList(PageData pd) throws Exception;
	
	/**
	 * 修改订单支付状态
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData updateOrderPayState(PageData pd) throws Exception;
	public PageData updateOrderPayState2(PageData pd) throws Exception;
	public PageData countOrderAmount(PageData pd) throws Exception;//统计订单金额
	
	/**
	 * 查询快递员经纬度
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getExpLngLat(PageData pd) throws Exception;
	
	public PageData getOrderMoney(PageData pd) throws Exception;
	
	public String getRealTime(PageData pd) throws Exception;
	
//	public PageData print(PageData pd) throws Exception;
	
	/**
	 * 根据邮编查询配送时间列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getDistributionTimeListByZipCode(PageData pd) throws Exception;
	
	/**
	 * 修改订单表，保存支付通道返回的ID
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData updateOrderChargeId(PageData pd) throws Exception;
}
