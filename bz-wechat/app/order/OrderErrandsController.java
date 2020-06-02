package com.flc.controller.app.order;

import com.alibaba.fastjson.JSON;
import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.order.OrderErrandsManager;
import com.flc.service.user.UserManager;
import com.flc.util.DateUtil;
import com.flc.util.PageData;
import com.flc.util.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
	@Autowired
	private UserManager userService;
	
	/**
	 * 私人订制用户下单
	 * @return
	 */
	@RequestMapping("createOrder")
	@ResponseBody
	public Object createOrder(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
						pd.get("order") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token"))) ||
				((LinkedHashMap)pd.get("order")).get("remark") == null || 
				((LinkedHashMap)pd.get("order")).get("extm_id") == null || 
				StringUtils.isBlank(String.valueOf(((LinkedHashMap)pd.get("order")).get("remark"))) || 
				StringUtils.isBlank(String.valueOf(((LinkedHashMap)pd.get("order")).get("extm_id")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.saveCreateOrder(pd);
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
	 * 私人订制用户进入下单-查询饭点
	 * @return
	 */
	@RequestMapping("getUserPoint")
	@ResponseBody
	public Object getUserPoint(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.getUserPoint(pd);
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
	 * 查询私人订制订单列表
	 * @return
	 */
	@RequestMapping("getOrderList")
	@ResponseBody
	public Object getOrderList(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.getOrderList(pd);
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
	
	/**
	 * 修改支付方式
	 * @return
	 */
	@RequestMapping("updateOrderErrandsPaymentMethod")
	@ResponseBody
	public Object updateOrderErrandsPaymentMethod(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("user_id") == null || 
				pd.get("token") == null || 
				pd.get("order_id") == null || 
				pd.get("payment_method") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("user_id"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("token"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("order_id"))) ||
				StringUtils.isBlank(String.valueOf(pd.get("payment_method")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		PageData result = new PageData();
		try {
			result = orderErrandsService.updateOrderErrandsPaymentMethod(pd);
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
     * PayPal回调-android
     * @param pd
     * @return
     */
	@RequestMapping("paypalNotifyAndroid")
	@ResponseBody
	public Object paypalNotifyAndroid(@RequestBody PageData pd){
		PageData result = new PageData();
		try {
			String order_id = pd.getString("order_id");
			String notify_str = (String)pd.get("notify");
			if(StringUtils.isEmpty(notify_str)){
				return getResultMap(ExEnum.EX_PAYPAL_PAY_ERROR, result);
			}
			Map<String, Object> notify = (Map<String, Object>)JSON.parse(notify_str);
			Map<String, Object> res = (Map<String, Object>)notify.get("response");
			String create_time = (String )res.get("create_time");
			String id = (String )res.get("id");
			String state = (String )res.get("state");
			 if("approved".equals(state)){
	            	//修改订单状态为已支付
	            	PageData opd = new PageData();
	            	opd.put("order_id", order_id);
	            	opd.put("pay_state", "1");		//已支付
	            	opd.put("payment_method", "1");	//paypal
	            	opd.put("pay_time", "1");		//支付时间
	            	opd.put("paypal", "1");			//标记paypal支付
	            	orderErrandsService.updateOrderErrandsPaymentMethod(opd);
	            	
//	            	//支付成功 发短信给快递员提醒接单
//	            	String text = "New order, please fill as soon as possible!";
//	        		text = URLEncoder.encode(text, "UTF-8");
//	        		List<PageData> exp_list = userService.getExpList(pd);
//	        		for (int i = 0; i < exp_list.size(); i++) {
//						String phone = exp_list.get(i).getString("phone");
//						String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//		        		String re = SmsUtil.SMS("", url);
//					}
	        		
	        		//下单成功 极光推送给快递员提醒接单
	        		Map<String, String> jpmap = new HashMap<String, String>();
	    	  		jpmap.put("orderId", order_id);
	        		result.put("push_rtn", orderErrandsService.jg_push(jpmap, ExEnum.JG_PUSH_TYPE2, orderErrandsService.getRegId()));
	            	
	            	return getResultMap(ExEnum.EX_SUCCESS, result);
	            } else {
	            	return getResultMap(ExEnum.EX_APPLE_PAY_FAIL, result);
	            }
		} catch (Exception e) {
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, result);
		}
	}
	
	/**
	 * PayPal回调-ios
	 * @param pd
	 * @return
	 */
	@RequestMapping("paypalNotifyIos")
	@ResponseBody
	public Object paypalNotifyIos(@RequestBody PageData pd){
		PageData result = new PageData();
		try {
			String order_id = pd.getString("order_id");
			Map<String, Object> notify = (Map<String, Object>)pd.get("confirmation");
			if(notify == null){
				return getResultMap(ExEnum.EX_PAYPAL_PAY_CANCEL, result);
			}
			Map<String, Object> res = (Map<String, Object>)notify.get("response");
			String create_time = (String )res.get("create_time");
			String id = (String )res.get("id");
			String state = (String )res.get("state");
			 if("approved".equals(state)){
	            	//修改订单状态为已支付
	            	PageData opd = new PageData();
	            	opd.put("order_id", order_id);
	            	opd.put("pay_state", "1");		//已支付
	            	opd.put("payment_method", "1");	//paypal
	            	opd.put("pay_time", "1");		//支付时间
	            	opd.put("paypal", "1");			//标记paypal支付
	            	orderErrandsService.updateOrderErrandsPaymentMethod(opd);
	            	
//	            	//支付成功 发短信给快递员提醒接单
//	            	String text = "New order, please fill as soon as possible!";
//	        		text = URLEncoder.encode(text, "UTF-8");
//	        		List<PageData> exp_list = userService.getExpList(pd);
//	        		for (int i = 0; i < exp_list.size(); i++) {
//						String phone = exp_list.get(i).getString("phone");
//						String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//		        		String re = SmsUtil.SMS("", url);
//					}
	        		
	        		//下单成功 极光推送给快递员提醒接单
	        		Map<String, String> jpmap = new HashMap<String, String>();
	    	  		jpmap.put("orderId", order_id);
	        		result.put("push_rtn", orderErrandsService.jg_push(jpmap, ExEnum.JG_PUSH_TYPE2, orderErrandsService.getRegId()));
	        		
	            	return getResultMap(ExEnum.EX_SUCCESS, result);
	            } else {
	            	return getResultMap(ExEnum.EX_APPLE_PAY_FAIL, result);
	            }
		} catch (Exception e) {
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, result);
		}
	}
	
	/**
	 * 极光推送测试
	 * @return
	 */
	@RequestMapping("pushTest")
	@ResponseBody
	public Object pushTest(@RequestBody PageData pd){
		PageData result = new PageData();
		String type = "0";
		if(pd.get("type") != null && 
				StringUtils.isNotBlank(String.valueOf(pd.get("type")))) {
			type = String.valueOf(pd.get("type"));
		}
		try {
			Map<String, String> jpmap = new HashMap<String, String>();
	  		jpmap.put("orderId", DateUtil.getFormatDate(new Date(),
					new int[] { Calendar.HOUR }, new int[] { 0 }, "yyyyMMddHH") + Tools.getRandomNum(6));
			if(type.equals("0")) {
				result.put("push_rtn", orderErrandsService.jg_push(jpmap, ExEnum.JG_PUSH_TYPE1, orderErrandsService.getRegId()));
			}else{
				result.put("push_rtn", orderErrandsService.jg_push(jpmap, ExEnum.JG_PUSH_TYPE2, orderErrandsService.getRegId()));
			}
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
}
