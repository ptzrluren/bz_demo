package com.flc.service.order;

import com.flc.util.PageData;

public interface OrderManager {

	public PageData getOrderCodeList(PageData pd) throws Exception;
	public PageData getOrderListByZipCode(PageData pd) throws Exception;
	public PageData expOrders(PageData pd) throws Exception;
	public PageData getExpOrderCodeList(PageData pd) throws Exception;
	public PageData getExpOrderListByZipCode(PageData pd) throws Exception;
	public PageData pickupOrderFoods(PageData pd) throws Exception;
	public PageData getOrderList4Map(PageData pd) throws Exception;
	public PageData onTheWay(PageData pd) throws Exception;
	public PageData sendTo(PageData pd) throws Exception;
	public PageData takeOrders(PageData pd) throws Exception;
	public PageData getOrderDetail(PageData pd) throws Exception;
	public PageData payOrder(PageData pd) throws Exception;
	public PageData uploadLocation(PageData pd) throws Exception;
}
