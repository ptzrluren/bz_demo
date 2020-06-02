package com.flc.service.order;

import com.flc.util.PageData;

/** 
 * 说明： 私人订制订单接口
 * 创建人：FLC
 * 创建时间：2018-03-17
 * @version
 */
public interface OrderErrandsManager{

	/**根据条件查询邮编列表及私人订制订单数量
	 * @param pd
	 * @throws Exception
	 */
	public PageData getExpOrderCodeList(PageData pd)throws Exception;
	
	/**根据条件查询私人订制订单列表
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderListByZipCode(PageData pd)throws Exception;
	
	/**查看私人订制订单详情
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderDetail(PageData pd)throws Exception;
	
	/**接单-快递端
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateTakeOrders(PageData pd)throws Exception;
	
	/**反馈订单价格
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsTotalMoney(PageData pd)throws Exception;
	
	/**修改订单状态
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsState(PageData pd)throws Exception;
	
}

