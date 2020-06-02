package com.flc.controller.app.common;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.util.Const;
import com.flc.util.PageData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 公告
 * 
 * @author RUING
 *
 */
@Controller
@RequestMapping("/app/bulletin")
public class BulletinController extends  BaseController {

	/**
	 * 私人订制订单首页公告
	 * @return
	 */
	@RequestMapping("getBulletin")
	@ResponseBody
	public Object getBulletin(){
		PageData result = new PageData();
		result.put("bulletin_cn", "尚未支付的订单不算作有效订单，请等待快递员反馈价格后尽快支付");
		result.put("bulletin_en", "We dont process unpaid order,you can pay for your order after we calculate the price");
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 私人订制订单如何获取饭点
	 * @return
	 */
	@RequestMapping("howToGetPoint")
	@ResponseBody
	public Object howToGetPoint(){
		PageData result = new PageData();
		result.put("instructions_cn", "易点是用于支付外卖订单的代币，可以通过易点平台的合作商家下单获得，外卖订单累计达到500USD后可以兑换"+Const.MIN_EXCHANGE+"个");
		result.put("instructions_en", "Reward E-point is a convenient way to pay your order at Etopia, Each order larger than 20 dollars subtotal will be record in your account and you can get "+Const.MIN_EXCHANGE+" point once your total order amount reach 500.");
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
}