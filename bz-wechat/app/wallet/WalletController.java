package com.flc.controller.app.wallet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.flc.config.Constant;
import com.flc.config.Description;
import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.userwalletlog.UserWalletLogManager;
import com.flc.service.wallet.WalletManager;
import com.flc.util.AppUtil;
import com.flc.util.Const;
import com.flc.util.PageData;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;

/**
 * 用户钱包
 * 
 * @author ZQ
 *
 */
@Controller
@RequestMapping(value = "/app/wallet")
public class WalletController extends BaseController {

	@Autowired
	private WalletManager walletService;
	@Autowired
	private UserWalletLogManager userwalletlogService;

	/**
	 * 钱包页面接口
	 * 
	 * @return
	 */
	@RequestMapping("getWalletPage")
	@ResponseBody
	public Object getHomePage() {
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = walletService.getWalletPage(pd);
		} catch (NjmsException e) {
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e) {
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		}

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}

	/**
	 * 兑换
	 * 
	 * @return
	 */
	@RequestMapping("exchange")
	@ResponseBody
	public Object exchange() {
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = walletService.exchange(pd);
		} catch (NjmsException e) {
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e) {
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		}

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}

	/**
	 * 获取常量信息
	 * 
	 * @return
	 */
	@RequestMapping("getConst")
	@ResponseBody
	public Object getConst(@RequestBody PageData pd) {
		PageData result = new PageData();
		if (pd.get("type") != null && StringUtils.isNotBlank(pd.getString("type"))) {
			if (pd.getString("type").equals("1")) {
				// 注册赠送饭点说明
				result.put("DESCRIPTION_STR_CN", Constant.ORDER_DESCRIPTION_CN);
				result.put("DESCRIPTION_STR_EN", Constant.ORDER_DESCRIPTION_EN);
			}
		}
		
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> whatMap = new HashMap<String, String>();
		whatMap.put("TITLE_CN", "");
		whatMap.put("TITLE_EN", "");
		whatMap.put("DESCRIPTION_CN", "易点是用于支付订单的代币，1易点=1USD");
		whatMap.put("DESCRIPTION_EN", "E-point is one payment method to pay in-app orders, 1 E-point=1$");
		list.add(whatMap);
		result.put("WHAT", new Description("什么是“易点”","What is E-point",list));
		
		List<Map<String, String>> list1 = new ArrayList<Map<String, String>>();
		Map<String, String> whatMap1 = new HashMap<String, String>();
		whatMap1.put("TITLE_CN", "邀请好友");
		whatMap1.put("TITLE_EN", "Invite your fiends to Etopia");
		whatMap1.put("DESCRIPTION_CN", "邀请您的好友注册易点并填写您的手机号，他/她完成第一次订单后您将获得5易点");
		whatMap1.put("DESCRIPTION_EN", "Invite your fiends to Etopia , you get 5 Epoint each one of them finished their first order");
		list1.add(whatMap1);
		Map<String, String> whatMap2 = new HashMap<String, String>();
		whatMap2.put("TITLE_CN", "累计消费");
		whatMap2.put("TITLE_EN", "Cumulative consumption");
		whatMap2.put("DESCRIPTION_CN", "您的每一笔消费都将被记录，每消费$500即可兑换10易点");
		whatMap2.put("DESCRIPTION_EN", "You can exchange 10 E-point with after every time your total order amount reach $500");
		list1.add(whatMap2);
		Map<String, String> whatMap3 = new HashMap<String, String>();
		whatMap3.put("TITLE_CN", "购买");
		whatMap3.put("TITLE_EN", "Buy");
		whatMap3.put("DESCRIPTION_CN", "您可以直接购买易点");
		whatMap3.put("DESCRIPTION_EN", "You can buy E-point from us");
		list1.add(whatMap3);
		result.put("HOW", new Description("如何获得“易点”","How to get E-points",list1));
		
		List<Map<String, String>> list2 = new ArrayList<Map<String, String>>();
		Map<String, String> freeMap = new HashMap<String, String>();
		freeMap.put("TITLE_CN", "");
		freeMap.put("TITLE_EN", "");
		freeMap.put("DESCRIPTION_CN", "每邀请一个好友使用易点app，您就可以获得5”易点”。被您邀请的朋友首单免3USD餐费并获得2”易点”。");
		freeMap.put("DESCRIPTION_EN", "You earn 5 E-point from every friends you’ve invited, your friend get $3 discount at first order.");
		list2.add(freeMap);
		result.put("FREE", new Description("免费餐","Free deliveries",list2));
		
		List<Map<String, String>> list3 = new ArrayList<Map<String, String>>();
		Map<String, String> questionMap = new HashMap<String, String>();
		questionMap.put("TITLE_CN", "");
		questionMap.put("TITLE_EN", "");
		questionMap.put("DESCRIPTION_CN", "邀请好友注册易点APP时填写您的账号：即手机号，TA的第一份订单完成后您将获得5个易点，同时被您邀请的朋友首单获得3USD的价格优惠，账户内自动获得2个易点");
		questionMap.put("DESCRIPTION_EN", "Invite friends to Etopia, They can enter your account Number .(your phone No.) to get $3 discount at first order and 2 free E-point, You get 5 E-point after they finished their first order");
		list3.add(questionMap);
		result.put("QUESTION", new Description("问号文本","Question mark text",list3));
		
		result.put("RECHARGE_LIST", Const.RECHARGE_LIST); // 充值饭点金额
		result.put("OPEN_CLOSE", Const.OPEN_CLOSE); // 充值开关0关1开
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}

	/**
	 * 饭点明细列表
	 * 
	 * @return
	 */
	@RequestMapping("userWalletLogList")
	@ResponseBody
	public Object userWalletLogList(@RequestBody PageData pd) {
		// 判断入参是否为空
		if (pd.get("TYPE") == null || pd.get("USER_ID") == null || StringUtils.isBlank(String.valueOf(pd.get("TYPE")))
				|| StringUtils.isBlank(String.valueOf(pd.get("USER_ID")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}

		PageData result = new PageData();
		try {
			result = userwalletlogService.list(pd);
		} catch (NjmsException e) {
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e) {
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		}
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}

	/**
	 * paypal支付回调，安卓使用
	 * 
	 * @param pd
	 * @return
	 */
	@RequestMapping("paypal_notify")
	@ResponseBody
	public Object paypal_notify(@RequestBody PageData pd) {
		PageData result = new PageData();
		try {
			String notify_str = (String) pd.get("notify");
			if (StringUtils.isEmpty(notify_str)) {
				return getResultMap(ExEnum.EX_PAYPAL_PAY_ERROR, result);
			}
			Map<String, Object> notify = (Map<String, Object>) JSON.parse(notify_str);
			Map<String, Object> res = (Map<String, Object>) notify.get("response");
			String create_time = (String) res.get("create_time");
			String id = (String) res.get("id");
			String state = (String) res.get("state");
			if ("approved".equals(state)) {
				userwalletlogService.save(pd, ExEnum.WL_TYPE1);
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
	 * 
	 * @param pd
	 * @return
	 */
	@RequestMapping("paypal_notify4ios")
	@ResponseBody
	public Object paypal_notify4ios(@RequestBody PageData pd) {
		PageData result = new PageData();
		try {
			Map<String, Object> notify = (Map<String, Object>) pd.get("confirmation");
			if (notify == null) {
				return getResultMap(ExEnum.EX_PAYPAL_PAY_CANCEL, result);
			}
			Map<String, Object> res = (Map<String, Object>) notify.get("response");
			String create_time = (String) res.get("create_time");
			String id = (String) res.get("id");
			String state = (String) res.get("state");
			if ("approved".equals(state)) {
				userwalletlogService.save(pd, ExEnum.WL_TYPE1);
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
	 * authorize_pay.net  信用卡支付
	 * 有安全问题，暂时注释掉了 返回错误提示，刷卡支付暂不可用
	 * 2018-05-29 已更换支付通道，重新开启
	 * @return
	 */
	@RequestMapping("authorize_pay")
	@ResponseBody
	public Object authorize_pay(){
		PageData pd = null;
		pd = this.getPageData();
		if(pd.get("stripe_token") == null || 
				pd.get("user_id") == null || 
				pd.get("index") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("index"))) || 
				StringUtils.isBlank(String.valueOf(pd.get("stripe_token"))) ||
				StringUtils.isBlank(String.valueOf(pd.get("user_id")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		Stripe.apiKey = "sk_live_Lwu7C5n6u3GU1jNHFzXYJO1J";
		String stripe_token = pd.getString("stripe_token");   //token
        try {
        	//根据订单类型和订单编号获取结算价格，单位为分
        	BigDecimal money = new BigDecimal(String.valueOf(Const.RECHARGE_LIST.get(Integer.parseInt(pd.getString("index"))).get("RECHARGE_AMOUNT")));
        	BigDecimal bd = new BigDecimal(100);
    		//调用支付通道进行结算
            Map<String, Object> chargeMap = new HashMap<String, Object>();
            chargeMap.put("amount", money.multiply(bd).intValue());
            chargeMap.put("currency", "usd");
            chargeMap.put("source", stripe_token); // obtained via Stripe.js
        	
            Charge charge = Charge.create(chargeMap);
            pd.put("charge_id", charge.getId());
            //结算成功，保存chargeId
            userwalletlogService.save(pd, ExEnum.WL_TYPE1);
            Map<String, Object> map = new HashMap<String,Object>();
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
}
