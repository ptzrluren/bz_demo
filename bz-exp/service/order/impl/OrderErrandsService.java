package com.flc.service.order.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.order.OrderErrandsManager;
import com.flc.util.AppUtil;
import com.flc.util.PageData;
import com.flc.util.SmsUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;

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
	
	/**根据条件查询邮编列表及私人订制订单数量
	 * @param pd
	 * @throws Exception
	 */
	public PageData getExpOrderCodeList(PageData pd)throws Exception{
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
		List<PageData> list = (List<PageData>)dao.findForList("OrderErrandsMapper.getExpOrderCodelistPage", page);
		
		PageData result = new PageData();
		result.put("rows", list);
		result.put("total", page.getTotalResult());
		result.put("total_page", page.getTotalPage());
		return result;
	}
	
	/**根据条件查询私人订制订单列表
	 * @param pd
	 * @throws Exception
	 */
	public PageData getOrderListByZipCode(PageData pd)throws Exception{
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
		List<PageData> list = (List<PageData>)dao.findForList("OrderErrandsMapper.getOrderListByZipCodelistPage", page);
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
		
		//计算快递员当前位置到送达地点的距离
		//计算订单收货人和配送员的距离
		String addr_lat = result.getString("addr_lat");
		String addr_lng = result.getString("addr_lng");  //订单经纬度
		String exp_lat = result.getString("exp_lat");
		String exp_lng = result.getString("exp_lng");  //快递员经纬度
		if(StringUtils.isEmpty(addr_lat) || 
				StringUtils.isEmpty(addr_lng) || 
				StringUtils.isEmpty(exp_lat) || 
				StringUtils.isEmpty(exp_lng)){
			result.put("distance", "无法计算");
		} else {
			double lat1 = Double.parseDouble(addr_lat);
			double lng1 = Double.parseDouble(addr_lng);
					
			double lat2 = Double.parseDouble(exp_lat);
			double lng2 = Double.parseDouble(exp_lng);
					
			//计算距离
			double d = AppUtil.GetDistance(lng1, lat1, lng2, lat2);
			//格式化
			DecimalFormat df   = new DecimalFormat("######0.00");   
			String distance = df.format(d);
			result.put("distance", distance + "km");
		}
		
		return result;
	}
	
	/**接单-快递端
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateTakeOrders(PageData pd)throws Exception{
		PageData order = (PageData)dao.findForObject("OrderErrandsMapper.findById", pd);
		//该订单不存在
		if(null == order){
			throw new NjmsException(ExEnum.EX_NO_ORDER);
		}
		String exp_id = order.getString("exp_id");
		//该订单已经被别人接单了
		if(!StringUtils.isEmpty(exp_id)){
			throw new NjmsException(ExEnum.EX_ORDER_HAS_TAKEN);
		}
		if(!order.get("state").equals("0")) {
			throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
		}
		Integer i = (Integer)this.dao.update("OrderErrandsMapper.updateTakeOrders", pd);
		if(i != 1) {
			throw new NjmsException(ExEnum.EX_ORDER_BE_OVERDUE);
		}
		return pd;
	}
	
	/**反馈订单价格
	 * @param pd
	 * @throws Exception
	 */
	public PageData updateOrderErrandsTotalMoney(PageData pd)throws Exception{
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
		if(result.get("state").equals("1") && pd.get("user_id").equals(result.get("exp_id"))) {
			//快递端反馈价格只允许修改状态为1外卖员接单
		}else{
			//0下单成功8反馈9用户取消,不允许直接修改
			throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
		}
		pd.put("state", "8");	//修改订单状态为已反馈
		//根据order_id修改私人订制订单价格
		Integer i = (Integer)this.dao.update("OrderErrandsMapper.updateOrderErrandsTotalMoney", pd);
		if(i != 1) {
			throw new NjmsException(ExEnum.EX_ORDER_BE_OVERDUE);
		}else{
			//配送员价格反馈成功，发送短信提醒用户付款
        	String text = "you can pay your delivery order now.";
    		text = URLEncoder.encode(text, "UTF-8");
			String phone = result.get("user_phone") + "";	//用户注册手机，非用户收货地址手机
			String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
    		String re = SmsUtil.SMS("", url);
		}
		return pd;
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
		
		if(pd.get("state").equals("3") || pd.get("state").equals("4")) {
			//快递端只允许修改状态为3在路上4订单完成的订单
		}else{
			//0下单成功1外卖员接单8反馈9用户取消,不允许直接修改
			throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
		}
		
		//根据order_id查询私人订制实体
		PageData result = (PageData) this.dao.findForObject("OrderErrandsMapper.findById", pd);
		if(result.get("payment_method") == null) {
			//未选择支付方式
			throw new NjmsException(ExEnum.EX_NOT_CHOSEN_PAY_ERROR);
		}
//		if(pd.get("state").equals("2")) {
//			//2取件中
//			if(result.get("state").equals("1") && pd.get("user_id").equals(result.get("exp_id"))) {
//				//状态必须为以接单且接单人为当前登录用户
//			}else{
//				throw new NjmsException(ExEnum.EX_OPERATION_ABNORMALITY_ERROR);
//			}
//			pd.put("pickup_time", "1");
//		}else 
		if(pd.get("state").equals("3")) {
			//3在路上
			if(!result.get("state").equals("8")) {
				//订单状态为8已反馈可修改
				throw new NjmsException(ExEnum.EX_CANCELLED_PAID_ERROR);
			}
			if(result.get("payment_method").equals("1") && result.get("pay_state").equals("0")) {
				//paypal未支付
				throw new NjmsException(ExEnum.EX_CANCELLED_PAID_ERROR);
			}
			if(result.get("payment_method").equals("4")) {
				//现金，不处理
			}
			if(result.get("payment_method").equals("5") && result.get("pay_state").equals("0")) {
				//饭点未支付
				throw new NjmsException(ExEnum.EX_CANCELLED_PAID_ERROR);
			}
		}else if(pd.get("state").equals("4")) {
			//4订单完成
			if(!result.get("state").equals("3")) {
				//订单状态为3在路上可修改
				throw new NjmsException(ExEnum.EX_CANCELLED_PAID_ERROR);
			}
			if(result.get("payment_method").equals("4")) {
				//现金支付
				pd.put("pay_state", "1");
				pd.put("pay_time", "1");
			}
			pd.put("finish_time", "1");
		}
		
		//根据order_id修改私人订制订单状态
		Integer i = (Integer)this.dao.update("OrderErrandsMapper.updateOrderErrandsState", pd);
		if(i == 1) {
			//私人订制计算到可兑换金额
			PageData exchangePoint = new PageData();
			exchangePoint.put("money", result.get("total_money"));
			exchangePoint.put("user_id", result.getString("user_id"));
			exchangePoint.put("point_add", "0");//仅累计兑换金额，对易点不做改动
			dao.update("WalletMapper.exchangePointNew", exchangePoint);
			return new PageData();
		}else{
			throw new NjmsException(ExEnum.EX_ORDER_BE_OVERDUE);
		}
	}
	
}

