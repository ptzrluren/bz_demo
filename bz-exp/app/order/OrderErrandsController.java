package com.flc.controller.app.order;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.order.OrderErrandsManager;
import com.flc.util.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** 
 * 说明：私人订制订单
 * 创建人：FLC
 * 创建时间：2018-03-17
 */
@Controller
@RequestMapping(value="/app/orderErrands")
public class OrderErrandsController extends BaseController {
	
	@Autowired
	private OrderErrandsManager orderErrandsService;
	
	/**
	 * 根据条件查询邮编列表及私人订制订单数量
	 * @return
	 */
	@RequestMapping("getExpOrderCodeList")
	@ResponseBody
	public Object getExpOrderCodeList(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.getExpOrderCodeList(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 根据条件查询私人订制订单列表
	 * @return
	 */
	@RequestMapping("getOrderListByZipCode")
	@ResponseBody
	public Object getOrderListByZipCode(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.getOrderListByZipCode(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 查看私人订制订单详情
	 * @return
	 */
	@RequestMapping("getOrderDetail")
	@ResponseBody
	public Object getOrderDetail(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				pd.get("order_id") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("order_id")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.getOrderDetail(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 接单-快递端
	 * @return
	 */
	@RequestMapping("takeOrders")
	@ResponseBody
	public Object takeOrders(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				pd.get("order_id") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token"))) ||
				StringUtils.isBlank(String.valueOf(pd.get("order_id")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.updateTakeOrders(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 反馈订单价格
	 * @return
	 */
	@RequestMapping("updateOrderErrandsTotalMoney")
	@ResponseBody
	public Object updateOrderErrandsTotalMoney(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				pd.get("order_id") == null || 
				pd.get("total_money") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("order_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("total_money")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.updateOrderErrandsTotalMoney(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 修改订单状态
	 * @return
	 */
	@RequestMapping("updateOrderErrandsState")
	@ResponseBody
	public Object updateOrderErrandsState(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				pd.get("order_id") == null || 
				pd.get("state") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("order_id"))) ||
				StringUtils.isBlank(String.valueOf(pd.get("state")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.updateOrderErrandsState(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
}
