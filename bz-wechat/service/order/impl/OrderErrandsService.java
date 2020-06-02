package com.flc.service.order.impl;

import com.flc.config.Constant;
import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.coupon.impl.CouponService;
import com.flc.service.order.OrderErrandsManager;
import com.flc.service.user.UserManager;
import com.flc.service.userwalletlog.impl.UserWalletLogService;
import com.flc.util.DateUtil;
import com.flc.util.JGPushUtil;
import com.flc.util.PageData;
import com.flc.util.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 
 * 说明： 私人订制订单
 * 创建人：FLC
 * 创建时间：2018-03-17
 * @version
 */
@Service("orderErrandsService")
public class OrderErrandsService implements OrderErrandsManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	@Resource(name="couponService")
	private CouponService couponService;
	
	@Resource(name="userwalletlogService")
	private UserWalletLogService userWalletLogService;
	
	@Autowired
	private UserManager userService;
	
	/**私人订制用户下单
	 * @param pd
	 * @throws Exception
	 */
	public PageData saveCreateOrder(PageData pd)throws Exception{
		if(pd.get("VERSION") == null || StringUtils.isBlank(pd.getString("VERSION"))) {
			throw new NjmsException(ExEnum.EX_NO_VERSION_ERROR);
		}
		
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		} else {
			String is_disable = user.getString("is_disable");
			//用户被禁用
			if("1".equals(is_disable)){
				throw new NjmsException(ExEnum.EX_IS_DISABLE);
			}
		}
		
		PageData wallet = (PageData) this.dao.findForObject("WalletMapper.getWallet", pd);
		//如果没有钱包,创建空的钱包
		if(null == wallet){
			dao.save("WalletMapper.saveWallet", pd);
			wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		}
		double point = 0;
		try {
			point = (Double)wallet.get("point");
		} catch (Exception e) {
			throw new NjmsException(ExEnum.EX_INIT_FAILED);
		}
		//饭点不足
		if(point < Double.parseDouble(Constant.ERRANDS_USE_POINT)) {
			throw new NjmsException(ExEnum.EX_POINT_NUMBER_ERROR);
		}
		
		//生成订单编号
		String order_id = DateUtil.getFormatDate(new Date(),
				new int[] { Calendar.HOUR }, new int[] { 0 }, "yyyyMMddHH") + Tools.getRandomNum(6);
		pd.put("order_id", order_id);		//保存订单编号
		pd.put("state", "0");				//下单成功
		pd.put("pay_state", 0);				//未支付
		pd.put("create_time", DateUtil.getFormatDate(new Date(), 
				new int[] { Calendar.HOUR }, new int[] { 0 }, "yyyy-MM-dd HH:mm:ss"));//下单时间
		pd.put("is_del", 0);				//订单状态
		pd.put("remark", ((LinkedHashMap)pd.get("order")).get("remark"));
		pd.put("extm_id", ((LinkedHashMap)pd.get("order")).get("extm_id"));
		pd.remove("order");
		Integer i = (Integer)dao.save("OrderErrandsMapper.save", pd);	//保存订单
		
		if(i == 1) {
			//每单私人订制扣除饭点
			pd.put("money", Constant.ERRANDS_USE_POINT);
			i = (Integer)dao.update("WalletMapper.takeoffPoint", pd);
			if(i != 1) {
				throw new NjmsException(ExEnum.EX_IP_ADDRESS_LOCALHOST);
			}else{
//				//下单成功，扣除饭点成功发送短信
//				String text = "New order, please fill as soon as possible!";
//        		text = URLEncoder.encode(text, "UTF-8");
//        		List<PageData> exp_list = (List<PageData>)dao.findForList("UserMapper.getExpList", pd);
//        		for (int j = 0; j < exp_list.size(); j++) {
//					String phone = exp_list.get(j).getString("phone");
//					String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//	        		String re = SmsUtil.SMS("", url);
//				}
        		
        		//下单成功 极光推送给快递员提醒接单
        		Map<String, String> map = new HashMap<String, String>();
        		map.put("orderId", order_id);
//        		map.put("zip_code", value);
    			pd.put("push_rtn", this.jg_push(map, ExEnum.JG_PUSH_TYPE2, this.getRegId()));
			}
			pd.remove("money");
		}else{
			throw new NjmsException(ExEnum.EX_IP_ADDRESS_LOCALHOST);
		}
		return pd;
	}
	
	/**私人订制用户进入下单-查询饭点
	 * @param pd
	 * @throws Exception
	 */
	public PageData getUserPoint(PageData pd)throws Exception{
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		} else {
			String is_disable = user.getString("is_disable");
			//用户被禁用
			if("1".equals(is_disable)){
				throw new NjmsException(ExEnum.EX_IS_DISABLE);
			}
		}
		
		PageData wallet = (PageData) this.dao.findForObject("WalletMapper.getWallet", pd);
		//如果没有钱包,创建空的钱包
		if(null == wallet){
			dao.save("WalletMapper.saveWallet", pd);
			wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		}
		PageData result = new PageData();
		result.put("use_point", Constant.ERRANDS_USE_POINT);
		result.put("user_point", wallet.get("point"));
		return result;
	}
	
	/**查询私人订制订单列表
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderList(PageData pd)throws Exception{
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		} else {
			String is_disable = user.getString("is_disable");
			//用户被禁用
			if("1".equals(is_disable)){
				throw new NjmsException(ExEnum.EX_IS_DISABLE);
			}
		}
		
		Page page = new Page(pd);
		//分页查询
		List<PageData> list = (List<PageData>)dao.findForList("OrderErrandsMapper.getOrderlistPage", page);
		for(int i = 0; i < list.size(); i++) {
			PageData uploadImg = new PageData();
			uploadImg.put("foreign_key_id", list.get(i).get("order_id"));
			uploadImg.put("table_name", "tb_order_errands");
			List<PageData> uploadImgist = (List<PageData>)dao.findForList("UploadImgMapper.findByForeignKeyId", uploadImg);
			for(PageData pageData : uploadImgist) {
				pageData.put("path", requestPath+pageData.get("path"));
			}
			list.get(i).put("imgs", uploadImgist);
		}
		
		PageData result = new PageData();
		result.put("rows", list);
		result.put("total", page.getTotalResult());
		result.put("total_page", page.getTotalPage());
		return result;
	}
	
	/**查看私人订制订单详情
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderDetail(PageData pd)throws Exception{
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		} else {
			String is_disable = user.getString("is_disable");
			//用户被禁用
			if("1".equals(is_disable)){
				throw new NjmsException(ExEnum.EX_IS_DISABLE);
			}
		}
		
		//根据order_id查询私人订制实体
		PageData result = (PageData) this.dao.findForObject("OrderErrandsMapper.findById", pd);
		//根据oredr_id查询上传图片列表
		PageData uploadImg = new PageData();
		uploadImg.put("foreign_key_id", result.get("order_id"));
		uploadImg.put("table_name", "tb_order_errands");
		List<PageData> uploadImgist = (List<PageData>)dao.findForList("UploadImgMapper.findByForeignKeyId", uploadImg);
		for(PageData pageData : uploadImgist) {
			pageData.put("path", requestPath+pageData.get("path"));
		}
		result.put("imgs", uploadImgist);
		
		return result;
	}
	
	/**修改订单状态
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsState(PageData pd)throws Exception{
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		} else {
			String is_disable = user.getString("is_disable");
			//用户被禁用
			if("1".equals(is_disable)){
				throw new NjmsException(ExEnum.EX_IS_DISABLE);
			}
		}
		
		PageData wallet = (PageData) this.dao.findForObject("WalletMapper.getWallet", pd);
		//如果没有钱包,创建空的钱包
		if(null == wallet){
			dao.save("WalletMapper.saveWallet", pd);
			wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		}
		
		if(!pd.get("state").equals("9")) {
			//用户端该接口不允许直接更改状态为非9：用户取消
			throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
		}
		
		//根据order_id查询私人订制实体
		PageData result = (PageData) this.dao.findForObject("OrderErrandsMapper.findById", pd);
		if(result.get("state").equals("8")){
			//只有当前状态为已反馈的私人订制订单才可允许用户取消
			
			//根据order_id修改私人订制订单状态
			Integer i = (Integer)this.dao.update("OrderErrandsMapper.updateOrderErrandsState", pd);
			if(i == 1) {
				//用户如果取消，退饭点
				pd.put("money", -Double.parseDouble(Constant.RTN_ERRANDS_USE_POINT));
				i = (Integer)dao.update("WalletMapper.takeoffPoint", pd);
				if(i != 1) {
					throw new NjmsException(ExEnum.EX_ORDER_BE_OVERDUE);
				}
				pd.remove("money");
			}else{
				throw new NjmsException(ExEnum.EX_ORDER_BE_OVERDUE);
			}
		}else{
			throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
		}
		return pd;
	}
	
	/**修改支付方式
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsPaymentMethod(PageData pd)throws Exception{
		PageData rtnPd = new PageData();
		String paypal = pd.getString("paypal");
		
		if(StringUtils.isNotBlank(paypal) && paypal.equals("1")) {
			//paypal回调，不查询user
		}else{
			String token = pd.getString("token");
			if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
				throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
			}
			if((pd.get("phone") == null && pd.get("user_id") == null) 
					|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
				throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
			}
			PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

			//登录失效
			if (user == null) {
				throw new NjmsException(ExEnum.EX_NO_USER);
			} else if(!token.equals(user.getString("token"))){
				throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
			} else {
				String is_disable = user.getString("is_disable");
				//用户被禁用
				if("1".equals(is_disable)){
					throw new NjmsException(ExEnum.EX_IS_DISABLE);
				}
			}
			
			PageData wallet = (PageData) this.dao.findForObject("WalletMapper.getWallet", pd);
			//如果没有钱包,创建空的钱包
			if(null == wallet){
				dao.save("WalletMapper.saveWallet", pd);
				wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
			}
		}
		
		//根据order_id查询私人订制实体
		PageData orderErrands = (PageData) this.dao.findForObject("OrderErrandsMapper.findById", pd);
		if(!orderErrands.get("state").equals("8")){
			//私人订制订单不为配送员已反馈的价格，不允许修改订单支付状态
			throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
		}
		
		if(pd.get("payment_method").equals("0")) {
			//visa
			throw new NjmsException(ExEnum.EX_CARD_ERROR);
		}else if(pd.get("payment_method").equals("1")) {
			//paypal
		}else if(pd.get("payment_method").equals("2")) {
			//balance
			//余额支付，暂未用到
			throw new NjmsException(ExEnum.EX_PAY_ERROR);
		}else if(pd.get("payment_method").equals("3")) {
			//信用卡支付
//			throw new NjmsException(ExEnum.EX_CARD_ERROR);
		}else if(pd.get("payment_method").equals("4")) {
			//现金支付
			pd.put("pay_state", "1");
//			pd.put("pay_time", "1");		为了跟线上支付统一，改为用户选择现金支付，默认支付成功
			
//			//支付成功 发短信给快递员提醒接单
//        	String text = "New order, please fill as soon as possible!";
//    		text = URLEncoder.encode(text, "UTF-8");
//    		List<PageData> exp_list = (List<PageData>)dao.findForList("UserMapper.getExpList", pd);
//    		for (int j = 0; j < exp_list.size(); j++) {
//				String phone = exp_list.get(j).getString("phone");
//				String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//        		String re = SmsUtil.SMS("", url);
//			}
			
			//支付成功 极光推送给快递员提醒接单
    		Map<String, String> map = new HashMap<String, String>();
    		map.put("orderId", String.valueOf(pd.get("order_id")));
			rtnPd.put("push_rtn", this.jg_push(map, ExEnum.JG_PUSH_TYPE2, this.getRegId()));
		}else if(pd.get("payment_method").equals("5")) {
			//反馈价格
			BigDecimal total_money = (BigDecimal)orderErrands.get("total_money");
			//饭点余额
			BigDecimal point = new BigDecimal(Double.valueOf((Double)orderErrands.get("point")));
			//判断饭点是否够支付
			if(point.compareTo(total_money) == -1) {
				throw new NjmsException(ExEnum.EX_POINT_NUMBER_ERROR);
			}else{
				//钱包扣除抵扣的饭点
				PageData opd = new PageData();
				opd.put("POINT", total_money);
				opd.put("user_id", pd.getString("user_id"));
				userWalletLogService.save(opd, ExEnum.WL_TYPE5);
				
				//饭点支付
				pd.put("pay_state", "1");
				pd.put("pay_time", "1");		//支付时间
				
//				//支付成功 发短信给快递员提醒接单
//	        	String text = "New order, please fill as soon as possible!";
//	    		text = URLEncoder.encode(text, "UTF-8");
//	    		List<PageData> exp_list = (List<PageData>)dao.findForList("UserMapper.getExpList", pd);
//	    		for (int j = 0; j < exp_list.size(); j++) {
//					String phone = exp_list.get(j).getString("phone");
//					String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//	        		String re = SmsUtil.SMS("", url);
//				}
	    		
				//支付成功 极光推送给快递员提醒接单
	    		Map<String, String> map = new HashMap<String, String>();
	    		map.put("orderId", String.valueOf(pd.get("order_id")));
				rtnPd.put("push_rtn", this.jg_push(map, ExEnum.JG_PUSH_TYPE2, this.getRegId()));
			}
		}
		
		//根据order_id修改私人订制订单支付方式
		Integer i = (Integer)this.dao.update("OrderErrandsMapper.updateOrderErrandsPaymentMethod", pd);
		if(i != 1) {
			throw new NjmsException(ExEnum.EX_ORDER_BE_OVERDUE);
		}else{
			return rtnPd;
		}
	}
	
	/**
	 * 修改订单表，保存支付通道返回的ID
	 */
	@Override
	public PageData updateOrderErrandsChargeId(PageData pd) throws Exception {
		dao.update("OrderErrandsMapper.updateOrderChargeId", pd);
		return pd;
	}
	
	/**信用卡支付成功，修改订单状态
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsStateByCCPay(PageData pd)throws Exception{
		//根据order_id修改私人订制订单状态
		pd.put("payment_method", "3");
		pd.put("pay_state", "1");
		pd.put("pay_time", "1");		//支付时间
		this.dao.update("OrderErrandsMapper.updateOrderErrandsPaymentMethod", pd);
		pd.remove("payment_method");
		pd.remove("pay_state");
		pd.remove("pay_time");
		return pd;
	}
	
	/**根据orderid查询私人定制金额
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderErrandsMoney(PageData pd)throws Exception{
		//根据order_id查询私人订制实体
		PageData result = (PageData) this.dao.findForObject("OrderErrandsMapper.getOrderErrandsMoney", pd);
		return result;
	}
	
	/**
	 * 极光推送快递员
	 * @param order_id
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public String jg_push(Map<String, String> map, ExEnum type, String regId) throws Exception {
		if(regId != null && regId.length() > 0) {
			String[] regids = regId.split(",");
			map.put("push_type", type.getCode());
			return JGPushUtil.jpushAll(map, regids, type.getDes_cn());
		}else{
			return null;
		}
	}
	
	/**
	 * 获取快递员RegId
	 * @return
	 * @throws Exception
	 */
	public String getRegId() throws Exception {
		List<PageData> exp_list = userService.getExpList(new PageData());
		//支付成功 极光推送给快递员提醒接单
		List<String> regIdList = new ArrayList<String>();
		for(int j = 0; j < exp_list.size(); j++) {
			if(exp_list.get(j).get("registration_id") != null && 
					StringUtils.isNotBlank(String.valueOf(exp_list.get(j).get("registration_id")))) {
				regIdList.add(String.valueOf(exp_list.get(j).get("registration_id")));
			}
		}
		if(regIdList != null && regIdList.size() > 0) {
			return StringUtils.join(regIdList.toArray(new String[regIdList.size()]), ",");
		}else{
			return null;
		}
	}
}

