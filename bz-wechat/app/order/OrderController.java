package com.flc.controller.app.order;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.order.OrderErrandsManager;
import com.flc.service.order.OrderManager;
import com.flc.service.user.UserManager;
import com.flc.util.AppUtil;
import com.flc.util.IosVerify;
import com.flc.util.JGPushUtil;
import com.flc.util.PageData;
import com.flc.util.SmsUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;


/**
 * 订单-支付
 * @author ZQ
 *
 */
@Controller
@RequestMapping(value="/app/order")
public class OrderController extends BaseController {
	
	
	@Autowired
	private OrderManager orderService;
//	@Autowired
//    private PaypalService paypalService;
	@Autowired
	private UserManager userService;
	@Autowired
	private OrderErrandsManager orderErrandsService;
	
	/**
	 * 创建订单
	 * @return
	 */
	@RequestMapping("createOrder")
	@ResponseBody
	public Object createOrder(@RequestBody PageData order){
		PageData pd = null;
		PageData result = new PageData();
		try {
			result = orderService.createOrder(order);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), e.getPd());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 使用优惠券
	 * @return
	 */
	@RequestMapping("useCoupon")
	@ResponseBody
	public Object useCoupon(@RequestBody PageData pd){
		PageData result = new PageData();
		try {
			result = orderService.useCoupon(pd);
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
	 * 统计订单
	 * @param order
	 * @return
	 */
	@RequestMapping("countOrderAmount")
	@ResponseBody
	public Object countOrderAmount(@RequestBody PageData order){
		PageData pd = null;
		PageData result = new PageData();
		try {
			result = orderService.countOrderAmount(order);
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
	 * 查询订单详情
	 * @return
	 */
	@RequestMapping("getOrderDetail")
	@ResponseBody
	public Object getOrderDetail(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = orderService.getOrderDetail(pd);
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
	 * 查询订单列表
	 * @return
	 */
	@RequestMapping("getOrderList")
	@ResponseBody
	public Object getOrderList(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = orderService.getOrderList(pd);
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
	 * 查询快递员经纬度
	 * @return
	 */
	@RequestMapping("getExpLngLat")
	@ResponseBody
	public Object getExpLngLat(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = orderService.getExpLngLat(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	public static final String PAYPAL_SUCCESS_URL = "/app/order/success.do";
    public static final String PAYPAL_CANCEL_URL = "/app/order/cancel.do";
	
	
    @RequestMapping("index")
    public ModelAndView index(){
    	ModelAndView mv = new ModelAndView();
    	mv.setViewName("app/index");
        return mv;
    }
    
    
	/**
	 * 选择使用paypal支付
	 * @param request
	 * @return
	 */
//	@RequestMapping(method = RequestMethod.POST, value = "pay")
//	@ResponseBody
//    public Object pay(HttpServletRequest request){
////		public Object pay(HttpServletRequest request, @RequestBody PageData pd){
//		PageData result = new PageData();
//        try {
//        	PageData pd = this.getPageData();
//        	String order_id = pd.getString("order_id");
//        	//回调地址
//        	String cancelUrl = URLUtils.getBaseURl(request) + "/" +  PAYPAL_CANCEL_URL + "?order_id=" + order_id;
//            String successUrl = URLUtils.getBaseURl(request) + "/" +  PAYPAL_SUCCESS_URL + "?order_id=" + order_id;
//        	
//        	PageData order = orderService.getOrderDetail(pd);
//        	BigDecimal money = (BigDecimal)order.get("money");
//        	//订单金额
//            Payment payment = paypalService.createPayment(
//            		money.doubleValue(), 
//                    "USD", 
//                    PaypalPaymentMethod.paypal, 
//                    PaypalPaymentIntent.sale,
//                    "payment description", 
//                    cancelUrl, 
//                    successUrl);
//            for(Links links : payment.getLinks()){
//                if(links.getRel().equals("approval_url")){
//                	System.out.println(links.getHref());
//                	result.put("status", "10001");
//                	result.put("msg_cn", "成功");
//                	result.put("msg_en", "success");
//                	
//                	Map<String, Object> map = new HashMap<String, Object>();
//                	map.put("pay_url", links.getHref());
//                	result.put("data", map);
//                    return result;
//                }
//            }
//        } catch (PayPalRESTException e) {
//        	e.printStackTrace();
//        } catch (Exception e) {
//			e.printStackTrace();
//		}
//        result.put("status", "10033");
//    	result.put("msg_cn", "支付失败");
//    	result.put("msg_en", "Payment failure");
//    	
//    	result.put("data", new PageData());
//        return result;
//    }
	
	/**
	 * 取消支付
	 * @return
	 */
//	@RequestMapping(method = RequestMethod.GET, value = "cancel")
//    public String cancelPay(@RequestParam("order_id") String order_id){
//		return "redirect:pay_ret://paypal?state=cancel&orderId=" + order_id;
//    }
//	@RequestMapping(method = RequestMethod.GET, value = "cancel")
//    public ModelAndView cancelPay(){
//    	ModelAndView mv = new ModelAndView();
//    	mv.setViewName("app/cancel");
//        return mv;
//    }

	
	/**
	 * 支付成功
	 * @param paymentId
	 * @param payerId
	 * @return
	 */
//	@RequestMapping(method = RequestMethod.GET, value = "success")
//    public Object successPay(@RequestParam("paymentId") String paymentId, 
//    		@RequestParam("PayerID") String payerId, @RequestParam("order_id") String order_id){
//        try {
//            Payment payment = paypalService.executePayment(paymentId, payerId);
//            if(payment.getState().equals("approved")){
//            	//修改订单状态为已支付
//            	PageData pd = new PageData();
//            	pd.put("order_id", order_id);
//            	orderService.updateOrderPayState(pd);
//            	return "redirect:pay_ret://paypal?state=success&orderId=" + order_id;
//            } else {
//            	return "redirect:pay_ret://paypal?state=fail&orderId=" + order_id;
//            }
//        } catch (PayPalRESTException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//			e.printStackTrace();
//		}
//        return "redirect:pay_ret://paypal?state=fail&orderId=" + order_id;
//    }
	
    /**
     * paypal支付回调，安卓使用
     * @param pd
     * @return
     */
	@RequestMapping("paypal_notify")
	@ResponseBody
	public Object paypal_notify(@RequestBody PageData pd){
		PageData result = new PageData();
		try {
			String order_id = pd.getString("order_id");
			String notify_str = (String)pd.get("notify");
			if(StringUtils.isBlank(notify_str)){
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
	            	orderService.updateOrderPayState(opd);
	            	
//	            	//支付成功 发短信给快递员提醒接单
//	            	String text = "New order, please fill as soon as possible!";
//	        		text = URLEncoder.encode(text, "UTF-8");
//	        		List<PageData> exp_list = userService.getExpList(pd);
//	        		for (int i = 0; i < exp_list.size(); i++) {
//						String phone = exp_list.get(i).getString("phone");
//						String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//		        		String re = SmsUtil.SMS("", url);
//					}
	        		
	        		//支付成功 极光推送给快递员提醒接单
	        		Map<String, String> map = new HashMap<String, String>();
	        		map.put("orderId", order_id);
	    			result.put("push_rtn", orderErrandsService.jg_push(map, ExEnum.JG_PUSH_TYPE1, orderErrandsService.getRegId()));
	            	
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
	 * paypal支付回调，ios使用
	 * @param pd
	 * @return
	 */
	@RequestMapping("paypal_notify4ios")
	@ResponseBody
	public Object paypal_notify4ios(@RequestBody PageData pd){
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
	            	orderService.updateOrderPayState(opd);
	            	
//	            	//支付成功 发短信给快递员提醒接单
//	            	String text = "New order, please fill as soon as possible!";
//	        		text = URLEncoder.encode(text, "UTF-8");
//	        		List<PageData> exp_list = userService.getExpList(pd);
//	        		for (int i = 0; i < exp_list.size(); i++) {
//						String phone = exp_list.get(i).getString("phone");
//						String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//		        		String re = SmsUtil.SMS("", url);
//					}
	        		
	        		//支付成功 极光推送给快递员提醒接单
	        		Map<String, String> map = new HashMap<String, String>();
	        		map.put("orderId", order_id);
	        		result.put("push_rtn", orderErrandsService.jg_push(map, ExEnum.JG_PUSH_TYPE1, orderErrandsService.getRegId()));
	        		
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
	 * 废弃代码
	 * @param request
	 * @param response
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("applepay_notify")
	@ResponseBody
	public Object doIosRequest(HttpServletRequest request, HttpServletResponse response,@RequestBody PageData pd)
			throws Exception {
		//需要解析pd
		//pd json字典
//		{token=xx,
//		user_id=xx,
//		order_id=xx,
//		signature=xx,
//		header={
//				publicKeyHash=xx
//				transactionId=xx
//				wrappedKey=xx
//			},
//		version=RSA_v1
//		}
		String re = IosVerify.buyAppVerify(pd);
		System.out.println(re);
		return getResultMap(ExEnum.EX_SUCCESS, new PageData());
	}
	
	
	 
	
	/**
	 * 信用卡支付
	 * 2018-05-29 已更换支付通道
	 * @return
	 */
	@RequestMapping("authorize_pay")
	@ResponseBody
	public Object authorize_pay(){
		PageData pd = null;
		pd = this.getPageData();
		if(pd.get("stripe_token") == null || 
				pd.get("order_id") == null || 
				pd.get("order_type") == null || 
				org.apache.commons.lang3.StringUtils.isBlank(String.valueOf(pd.get("order_id"))) || 
				org.apache.commons.lang3.StringUtils.isBlank(String.valueOf(pd.get("stripe_token"))) || 
				org.apache.commons.lang3.StringUtils.isBlank(String.valueOf(pd.get("order_type")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		Stripe.apiKey = "sk_live_Lwu7C5n6u3GU1jNHFzXYJO1J";
		String stripe_token = pd.getString("stripe_token");   //token
		String order_type = pd.getString("order_type");   //订单类型：0外卖1跑腿
        try {
        	//根据订单类型和订单编号获取结算价格，单位为分
        	BigDecimal money = new BigDecimal(0);
        	BigDecimal bd = new BigDecimal(100);  
    		if(order_type.equals("0")){
    			PageData order = orderService.getOrderMoney(pd);
    			if(null == order){
    				return getResultMap(ExEnum.EX_NO_ORDER, new PageData());
    			}
    			//订单金额
    			money = (BigDecimal)order.get("total_money");
    		}else{
    			PageData order = orderErrandsService.getOrderErrandsMoney(pd);
    			if(null == order){
    				return getResultMap(ExEnum.EX_NO_ORDER, new PageData());
    			}
    			if(!order.get("state").equals("8")) {
    				//只有已反馈状态才可信用卡支付
    				return getResultMap(ExEnum.EX_OPERATION_ABNORMALITY_ERROR, new PageData());
    			}
    			//订单金额
    			money = (BigDecimal)order.get("total_money");
    		}
    		
    		//调用支付通道进行结算
            Map<String, Object> chargeMap = new HashMap<String, Object>();
            chargeMap.put("amount", money.multiply(bd).intValue());
            chargeMap.put("currency", "usd");
            chargeMap.put("source", stripe_token); // obtained via Stripe.js
        	
            Charge charge = Charge.create(chargeMap);
            pd.put("charge_id", charge.getId());
            //结算成功，保存chargeId
            if(order_type.equals("0")){
            	//外卖订单
            	orderService.updateOrderChargeId(pd);
                orderService.updateOrderPayState(pd);
            }else{
            	//跑腿订单
            	orderErrandsService.updateOrderErrandsChargeId(pd);
            	orderErrandsService.updateOrderErrandsStateByCCPay(pd);
            }
            Map<String, Object> map = new HashMap<String,Object>();
            
//	        //支付成功 发短信给快递员提醒接单
//	      	String text = "New order, please fill as soon as possible!";
//	  		text = URLEncoder.encode(text, "UTF-8");
//	  		List<PageData> exp_list = userService.getExpList(pd);
//	  		for (int i = 0; i < exp_list.size(); i++) {
//				String phone = exp_list.get(i).getString("phone");
//				String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//				SmsUtil.SMS("", url);
//			}
	  		
	  		//支付成功 极光推送给快递员提醒接单
	  		Map<String, String> jpmap = new HashMap<String, String>();
	  		jpmap.put("orderId", String.valueOf(pd.get("order_id")));
	  		map.put("push_rtn", orderErrandsService.jg_push(jpmap, ExEnum.JG_PUSH_TYPE1, orderErrandsService.getRegId()));
            
    		map.put("status","10001");
    		map.put("msg_cn",charge.getOutcome().getSellerMessage());
    		map.put("msg_en",charge.getOutcome().getSellerMessage());
    		return AppUtil.returnObject(pd, map);
        }catch (StripeException e) {
        	e.printStackTrace();
        	Map<String, Object> map = new HashMap<String,Object>();
    		map.put("status","10000");
    		map.put("msg_cn",e.getMessage());
    		map.put("msg_en",e.getMessage());
    		return AppUtil.returnObject(pd, map);
        } catch (Exception e) {
        	e.printStackTrace();
        	return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		}
	}
	
	/**
	 * 根据邮编查询配送时间列表
	 * @return
	 */
	@RequestMapping("getDistributionTimeListByZipCode")
	@ResponseBody
	public Object getDistributionTimeListByZipCode(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = orderService.getDistributionTimeListByZipCode(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**获取所有有效期接口(废弃，卡信息保存在客户端)
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/getExpiration")
	@ResponseBody
	public Object getExpiration(){
		PageData pd = new PageData();
		try {
			List<PageData> list = userService.getExpiration(null);
			pd.put("data", list);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e) {
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		}
		return getResultMap(ExEnum.EX_SUCCESS, pd);
	}
	
}
