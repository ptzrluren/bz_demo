package com.flc.controller.app.store;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.util.IosVerify;
import com.flc.util.PageData;

@Controller
@RequestMapping(value = "/app/testcon")
public class TestController extends BaseController {

	@RequestMapping(value = "/ios", method = RequestMethod.POST)
	public Object doIosRequest(HttpServletRequest request, HttpServletResponse response, String receipt)
			throws Exception {

		String verifyResult = IosVerify.buyAppVerify(new PageData());
		if (verifyResult == null) {
			// 苹果服务器没有返回验证结果
			throw new Exception("无订单信息!");//
		} else {
			// 苹果验证有返回结果------------------
			JSONObject job = JSONObject.parseObject(verifyResult);
			String states = job.getString("status");
			if (states.equals("0")) // 验证成功
			{
				String r_receipt = job.getString("receipt");
				JSONObject returnJson = JSONObject.parseObject(r_receipt);
				if (!returnJson.getString("bid").trim().equals("xxxx")) {// 商户的id不匹配
					throw new Exception("订单无效!");//
				}
				// 产品ID
				String product_id = returnJson.getString("product_id");
				// 订单号
				String transaction_id = returnJson.getString("transaction_id");
				// 交易日期
				String purchase_date = returnJson.getString("purchase_date");
				// 保存到数据库
				System.out.println("product_id:" + product_id + "   transaction_id : " + transaction_id
						+ " purchase_date: " + purchase_date);
						/*************************** +自己的业务逻辑 *****************************************///
				/*** 此处要判断是否已经支付过,支付过之后不能重复发货 **/
				// ***************************************/
				// paymentService.doIosPayment(product_id, transaction_id);
				/// ***************************自己的业务逻辑end************************************///
			} else {
				throw new Exception("订单无效!");//
			}
		}

		return getResultMap(ExEnum.EX_SUCCESS, new PageData());

	}

}
