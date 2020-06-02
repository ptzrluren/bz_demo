package com.flc.service.order.impl;

import com.flc.config.Constant;
import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.address.impl.AddressService;
import com.flc.service.coupon.impl.CouponService;
import com.flc.service.order.OrderManager;
import com.flc.service.user.UserManager;
import com.flc.service.userwalletlog.impl.UserWalletLogService;
import com.flc.util.Const;
import com.flc.util.DateUtil;
import com.flc.util.PageData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

@Service("orderService")
public class OrderService implements OrderManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	@Autowired
	private UserManager userService;
	@Resource(name="addressService")
	private AddressService addressService;
	@Resource(name="couponService")
	private CouponService couponService;
	@Resource(name="userwalletlogService")
	private UserWalletLogService userWalletLogService;
	@Resource(name="orderErrandsService")
	private OrderErrandsService orderErrandsService;
	
	/**
	 * 创建订单
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData createOrder(PageData pd) throws Exception {
		if(pd.get("VERSION") == null || StringUtils.isBlank(pd.getString("VERSION"))) {
			throw new NjmsException(ExEnum.EX_NO_VERSION_ERROR);
		}
		
		PageData result = new PageData();
		
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
		
		HashMap<String, Object> order = (HashMap<String, Object>)pd.get("order");
		String payment_method = (String)order.get("payment_method");
		
		//根据当前时间判断配送时间显示列表，只显示当前时间+1小时以后的开始配送时间列表
		if(String.valueOf(order.get("distribution_time")).trim().equalsIgnoreCase("null")
				|| !queryIsDistribution(String.valueOf(order.get("distribution_time")).trim())) {
			throw new NjmsException(ExEnum.EX_NO_DISTRIBUTION_TIME_ERROR);
		}
		//判断收货地址编号是否在配送范围内
		PageData orderPd = new PageData();
		orderPd.put("extm_id", order.get("extm_id"));
		orderPd = addressService.isEffZipCodeByExtmId(orderPd);
		if(orderPd.getString("in_range").equals("0")){
			throw new NjmsException(ExEnum.EX_NO_ZIP_ERROR);
		}
		
		//查询订单总数，如果是首单被邀请注册用户则减3刀
		Boolean isFirst = false;
		int count =  (int)this.dao.findForObject("UserMapper.getOrderCount", pd);
		if(count == 0) {
			isFirst = true;
		}
		
//		PageData order = (PageData)JSONUtils.parse(jsonStr);
		String user_id = pd.getString("user_id");
		Date today = new Date();
		SimpleDateFormat sbf = new SimpleDateFormat("yyyyMMddHH");
		String date = sbf.format(today);
		long time = System.currentTimeMillis();
		String order_id = date + getRandomNum();
		order.put("user_id", user_id);
		order.put("order_id", order_id);
		//订单选中的商家列表
		List<PageData> store_list = (List<PageData>)order.get("store_list");
		
		//判断商家营业时间add by lb at 201808221033 begin
		List<String> storeids = new ArrayList<String>();
		String store_name_cn = "";
		String store_name_en = "";
		for (int i = 0; i < store_list.size(); i++) {
			HashMap<String, Object> store = store_list.get(i);
			String store_id = (String)store.get("store_id");
			//查询商家的营业时间
			PageData sspd = new PageData();
			sspd.put("store_id", store_id);
			PageData storepd = (PageData)dao.findForObject("StoreMapper.getStoreBusinessHours", sspd);
			//判断 当前商家是否在营业时间之内，如果不在  直接返回异常
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
			df.setTimeZone(TimeZone.getTimeZone(Const.SHIQU));
		    Date now =null;
		    Date beginTime = null;
		    Date endTime = null;
		    try {
		        now = df.parse(df.format(new Date()));
		        String business_hours_start = storepd.getString("business_hours_start");
		        String business_hours_end = storepd.getString("business_hours_end");
		        if(StringUtils.isEmpty(business_hours_start)){
		        	business_hours_end = "00:00:00";
		        }
		        if(StringUtils.isEmpty(business_hours_end)){
		        	business_hours_end = "23:59:59";
		        }
		        beginTime = df.parse(business_hours_start);
		        endTime = df.parse(business_hours_end);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    Boolean flag = DateUtil.isEffectiveDate(now, beginTime, endTime);
		    if(!flag){
		    	storeids.add(storepd.getString("store_id"));
		    	store_name_cn += storepd.getString("store_name_cn") + ",";
		    	store_name_en += storepd.getString("store_name_en") + ",";
		    }
		}
		if(storeids != null && storeids.size() > 0) {
			result.put("store_ids", storeids.toArray());
			result.put("store_name_cn", store_name_cn.substring(0, store_name_cn.length() - 1));
			result.put("store_name_en", store_name_en.substring(0, store_name_en.length() - 1));
			return result;
		}
		//判断商家营业时间add by lb at 201808221033 end
		
		//实际支付总金额=订单金额+订单金额*0.0875 + 订单金额*0.2 + (商店数量-1) * 2    0.0875是税   后面的是配送费
		DecimalFormat df = new DecimalFormat("#0.00");
		double[] countPrice = (double[])this.countPrice(store_list, order_id, user_id).get("price");
		double money = countPrice[0];  //订单金额
		double point = countPrice[1];  //饭点金额
		double cost = countPrice[2];   //成本金额
		
		//不足指定的金额  则按照指定算(税和运费也是按照该金额算)
		double limit_money = 15;
		if(orderPd.get("money_limit") != null && !String.valueOf(orderPd.get("money_limit")).trim().equalsIgnoreCase("null")) {
			limit_money = Double.parseDouble(String.valueOf(orderPd.get("money_limit")));
		}else{
			limit_money = (double)dao.findForObject("OrderMapper.getLimitMoney", pd);
		}
		
		if(pd.get("skip_limit_money") != null) { 
			if(pd.getString("skip_limit_money").equals("1")) {
				money = limit_money;
				point = limit_money;
			}else{
				if("5".equals(payment_method)){
					if(point < limit_money){
						point = limit_money;
						ExEnum.EX_MIN_AMOUNT_ERROR.setDes_cn("您的购物车总额不满" + limit_money + "易点，继续支付则按" + limit_money + "易点收取费用，返回可添加更多商品至购物车");
						ExEnum.EX_MIN_AMOUNT_ERROR.setDes_en("The total amount of your shopping cart is less than " + limit_money + " point, and the continuing payment is charged at " + limit_money + "point, and more goods can be added to the shopping cart.");
						throw new NjmsException(ExEnum.EX_MIN_AMOUNT_ERROR);
					}
				}else{
					if(money < limit_money){
						money = limit_money;
						ExEnum.EX_MIN_AMOUNT_ERROR.setDes_cn("您的购物车总额不满" + limit_money + "USD，继续支付则按" + limit_money + "USD收取费用，返回可添加更多商品至购物车");
						ExEnum.EX_MIN_AMOUNT_ERROR.setDes_en("The total amount of your shopping cart is less than " + limit_money + " yuan, and the continuing payment is charged at " + limit_money + "USD, and more goods can be added to the shopping cart.");
						throw new NjmsException(ExEnum.EX_MIN_AMOUNT_ERROR);
					}
				}
			}
		}else{
			if(money < limit_money){
				money = limit_money;
				ExEnum.EX_MIN_AMOUNT_ERROR.setDes_cn("您的购物车总额不满" + limit_money + "USD，继续支付则按" + limit_money + "USD收取费用，返回可添加更多商品至购物车");
				ExEnum.EX_MIN_AMOUNT_ERROR.setDes_en("The total amount of your shopping cart is less than " + limit_money + " yuan, and the continuing payment is charged at " + limit_money + "USD, and more goods can be added to the shopping cart.");
				throw new NjmsException(ExEnum.EX_MIN_AMOUNT_ERROR);
			}
		}
		
		BigDecimal yunfei_init = new BigDecimal(money).multiply(new BigDecimal("0.18"));
		double yunfei_store = 0;
		if(null != store_list && store_list.size() != 0){
			yunfei_store = (store_list.size() - 1) * 3.99;
		}
		BigDecimal shui = new BigDecimal(money).multiply(new BigDecimal("0.08875"));
		BigDecimal yunfei = new BigDecimal(yunfei_init.doubleValue() + yunfei_store);
		
		BigDecimal total_money = new BigDecimal(0);  
		total_money = total_money.add(new BigDecimal(money));
		total_money = total_money.add(yunfei);
		total_money = total_money.add(shui);
		
		BigDecimal point_money = new BigDecimal(0);  
		point_money = point_money.add(new BigDecimal(point));
		point_money = point_money.add(yunfei);
		point_money = point_money.add(shui);
		
		//饭点支付
//		if("5".equals(payment_method)){
		order.put("point_money", point);
		order.put("point_total_money", df.format(point_money));
		order.put("nodiscount_point", df.format(point_money));		//未使用优惠券金额
//		}else{
		order.put("money", money);
		order.put("total_money", df.format(total_money));
		order.put("nodiscountamount", df.format(total_money));		//未使用优惠券金额
//		}
		
		//保存订单
		order.put("cost_money", cost);
		order.put("state", "0");
		order.put("yunfei", df.format(yunfei));
		order.put("tax", shui);
		
		//判断优惠券是否有效
		PageData coupon = null;
		if(StringUtils.isNotBlank(pd.getString("COUPON_NUM"))) {
			coupon = this.couponService.findByCouponNum(pd);
			//查询是否使用
			if(coupon != null && coupon.getString("STATUS").equals("0")) {
				//查询客户端传来的优惠金额与实际是否一致
				if(new BigDecimal(coupon.getString("COUPON_MONEY")).compareTo(new BigDecimal(pd.getString("COUPON_MONEY"))) != 0) {
					throw new NjmsException(ExEnum.EX_COUPON_NOT_MATCH_ERROR);
				}
			}else{
				throw new NjmsException(ExEnum.EX_NO_COUPON_ERROR);
			}
			//修改优惠券为已使用
			coupon.put("STATUS", "1");
			coupon.put("USER_ID", user.getString("user_id"));
			this.couponService.edit(coupon);
		}
		//判断优惠券是否有效
		
		//优惠券信息添加到订单表中
		if(coupon != null) {
			order.put("coupon_id", coupon.getString("COUPON_ID"));
			order.put("coupon_money", coupon.getString("COUPON_MONEY"));
			
			//如果总金额-优惠券金额小于等于0
			if(total_money.subtract(new BigDecimal(coupon.getString("COUPON_MONEY"))).compareTo(BigDecimal.ZERO) <= 0) {
				order.put("total_money", "0");
				total_money = new BigDecimal("0");
			}else{
				order.put("total_money", total_money.subtract(new BigDecimal(pd.getString("COUPON_MONEY")))+"");
				total_money = total_money.subtract(new BigDecimal(pd.getString("COUPON_MONEY")));
			}
			if(point_money.subtract(new BigDecimal(coupon.getString("COUPON_MONEY"))).compareTo(BigDecimal.ZERO) <= 0) {
				order.put("point_total_money", "0");
				point_money = new BigDecimal("0");
			}else{
				order.put("point_total_money", point_money.subtract(new BigDecimal(pd.getString("COUPON_MONEY")))+"");
				point_money = point_money.subtract(new BigDecimal(pd.getString("COUPON_MONEY")));
			}
		}
		
		if(isFirst) {
			//判断是否首单
			if(user.get("invite") != null && StringUtils.isNotBlank(user.getString("invite"))) {
				//判断是否被邀请注册，被邀请注册首单减3刀
				//如果总金额-优惠券金额小于等于0
				if(total_money.subtract(new BigDecimal(Const.REG_GIVE_USD)).compareTo(BigDecimal.ZERO) <= 0) {
					order.put("total_money", "0");
					total_money = new BigDecimal("0");
				}else{
					order.put("total_money", total_money.subtract(new BigDecimal(Const.REG_GIVE_USD))+"");
					total_money = total_money.subtract(new BigDecimal(Const.REG_GIVE_USD));
				}
				if(point_money.subtract(new BigDecimal(Const.REG_GIVE_USD)).compareTo(BigDecimal.ZERO) <= 0) {
					order.put("point_total_money", "0");
					point_money = new BigDecimal("0");
				}else{
					order.put("point_total_money", point_money.subtract(new BigDecimal(Const.REG_GIVE_USD))+"");
					point_money = point_money.subtract(new BigDecimal(Const.REG_GIVE_USD));
				}
			}
		}
		
		if("5".equals(payment_method)){
			//饭点支付
			PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
			Double fandian = (Double)wallet.get("point");
			if(point_money.doubleValue() > fandian.intValue()){
				throw new NjmsException(ExEnum.EX_POINT_NUMBER_ERROR);
			} else {
				//扣除抵扣掉的饭点
				order.put("pay_state", 1);
				//钱包扣除抵扣的饭点
				PageData opd = new PageData();
				opd.put("POINT", point_money);
				opd.put("user_id", user_id);
				userWalletLogService.save(opd, ExEnum.WL_TYPE5);
				
				//支付成功 极光推送给快递员提醒接单
				Map<String, String> map = new HashMap<String, String>();
	    		map.put("orderId", order_id);
				result.put("push_rtn", orderErrandsService.jg_push(map, ExEnum.JG_PUSH_TYPE1, orderErrandsService.getRegId()));
			}
		} else {
			order.put("pay_state", 0);
		}
		dao.save("OrderMapper.saveOrder", order);
		//被邀请人首单赠送2饭点
		if(isFirst) {
			//判断是否首单
			if(user.get("invite") != null && StringUtils.isNotBlank(user.getString("invite"))) {
				PageData opd = new PageData();
				opd.put("invite", user.getString("invite"));
				opd.put("user_id", user_id);
				userWalletLogService.save(opd, ExEnum.WL_TYPE3);
			}
		}
		if("4".equals(payment_method)){
			//下单成功 极光推送给快递员提醒接单
			Map<String, String> map = new HashMap<String, String>();
    		map.put("orderId", order_id);
			result.put("push_rtn", orderErrandsService.jg_push(map, ExEnum.JG_PUSH_TYPE1, orderErrandsService.getRegId()));
			
//			new Thread(){
//	            public void run() {
//	            	//支付成功 发短信给快递员提醒接单
//	            	String text = "New order, please fill as soon as possible!";
//	            	List<PageData> exp_list = null;
//	        		try {
//	        			exp_list = userService.getExpList(new PageData());
//						text = URLEncoder.encode(text, "UTF-8");
//		        		for (int i = 0; i < exp_list.size(); i++) {
//		    				String phone = exp_list.get(i).getString("phone");
//		    				String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
//		            		String re = SmsUtil.SMS("", url);
//		            		System.out.println(re);
//		    			}
//					} catch (UnsupportedEncodingException e) {
//						e.printStackTrace();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//	            }
//	         }.start();
		} 
		
		order.put("money",  df.format(total_money));
		order.put("point_total_money",  df.format(point_money));
		result.put("order", order);
		
		//设置常用地址为默认地址
		PageData setdefaultAddrPd = new PageData();
		setdefaultAddrPd.put("user_id", pd.getString("user_id"));
		this.addressService.setdefault(setdefaultAddrPd);
		return result;
	}
	
	private String getRandomNum(){
		Random rand = new Random();
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < 6; i++) {
			int num = rand.nextInt(9);
			sbf.append(num);
		}
		return sbf.toString();
	}
	
	public static void main(String[] args) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
		df.setTimeZone(TimeZone.getTimeZone(Const.SHIQU));
	    Date now =null;
	    Date beginTime = null; 
	    Date endTime = null;
	    try {
	        now = df.parse(df.format(new Date()));
	        beginTime = df.parse("11:00:00");
	        endTime = df.parse("03:00:00");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    Boolean flag = DateUtil.isEffectiveDate(now, beginTime, endTime);
	    System.out.println(flag);
	    System.out.println(beginTime.getTime());
	    System.out.println(endTime.getTime());
	}
	
	/**
	 * 统计订单
	 */
	@Override
	public PageData countOrderAmount(PageData pd) throws Exception {
		PageData result = new PageData();
		
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		
		String token = pd.getString("token");
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);
		
		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE); 
		}
		
		//查询订单总数，如果是首单被邀请注册用户则减3刀
		Boolean isFirst = false;
		int count =  (int)this.dao.findForObject("UserMapper.getOrderCount", pd);
		if(count == 0) {
			isFirst = true;
		}
		
		HashMap<String, Object> order = (HashMap<String, Object>)pd.get("order");
//		PageData order = (PageData)JSONUtils.parse(jsonStr);
		//订单选中的商家列表
		List<PageData> store_list = (List<PageData>)order.get("store_list");
		
		//判断商家营业时间add by lb at 201808221033 begin
		List<String> storeids = new ArrayList<String>();
		String store_name_cn = "";
		String store_name_en = "";
		for (int i = 0; i < store_list.size(); i++) {
			HashMap<String, Object> store = store_list.get(i);
			String store_id = (String)store.get("store_id");
			//查询商家的营业时间
			PageData sspd = new PageData();
			sspd.put("store_id", store_id);
			PageData storepd = (PageData)dao.findForObject("StoreMapper.getStoreBusinessHours", sspd);
			//判断 当前商家是否在营业时间之内，如果不在  直接返回异常
			SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
			df.setTimeZone(TimeZone.getTimeZone(Const.SHIQU));
		    Date now =null;
		    Date beginTime = null;
		    Date endTime = null;
		    try {
		        now = df.parse(df.format(new Date()));
		        String business_hours_start = storepd.getString("business_hours_start");
		        String business_hours_end = storepd.getString("business_hours_end");
		        if(StringUtils.isEmpty(business_hours_start)){
		        	business_hours_end = "00:00:00";
		        }
		        if(StringUtils.isEmpty(business_hours_end)){
		        	business_hours_end = "23:59:59";
		        }
		        beginTime = df.parse(business_hours_start);
		        endTime = df.parse(business_hours_end);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    Boolean flag = DateUtil.isEffectiveDate(now, beginTime, endTime);
		    if(!flag){
		    	storeids.add(storepd.getString("store_id"));
				store_name_cn += storepd.getString("store_name_cn") + ",";
		    	store_name_en += storepd.getString("store_name_en") + ",";
		    }
		}
		if(null != storeids && storeids.size() > 0) {
			result.put("store_ids", storeids.toArray());
	    	result.put("store_name_cn", store_name_cn.substring(0, store_name_cn.length() - 1));
			result.put("store_name_en", store_name_en.substring(0, store_name_en.length() - 1));
			return result;
		}
		//判断商家营业时间add by lb at 201808221033 end
		
//		for (int i = 0; i < store_list.size(); i++) {
//			HashMap<String, Object> store = store_list.get(i);
//			//每个商家中的商品列表
//			List<HashMap<String, Object>> foods_list =  (List<HashMap<String, Object>>)store.get("foods_list");
//			for (int j = 0; j < foods_list.size(); j++) {
//				HashMap<String, Object> foods = foods_list.get(j);
//				String spec_id_list = (String)foods.get("spec_id_list");
//				//根据规格查询价格，如果没有该规格的价格，则查询该商品的默认价格
//				PageData fpd = new PageData();
//				fpd.put("spec_id_list", spec_id_list);
//				String price_obj = (String)dao.findForObject("StoreMapper.getFoodsPrice", fpd);
//				double price = 0;
//				//如果用户选择的规格有价格， 则使用该规格计算
//				if(null != price_obj){
//					price = Double.parseDouble(price_obj);
//				} else {
//					//如果规格没有价格  用商品的默认价格
//					price =  Double.parseDouble( (String)foods.get("price"));
//				}
//				//购买的份数
//				int quantity = (Integer)foods.get("quantity");
//				BigDecimal price_bd = new BigDecimal(price);
//				BigDecimal quantity_bd = new BigDecimal(quantity);
//				//该商品的金额
//				BigDecimal foods_money = price_bd.multiply(quantity_bd);
//				money += foods_money.doubleValue();
//			}
//		}
		
		//首单免运费
//		if(count == 0){
//			yunfei_store = 0;
//			yunfei_init = new BigDecimal("0");
//		}
		
		//实际支付总金额=订单金额+订单金额*0.08875 + 订单金额*0.2 + (商店数量-1) * 2    0.08875是税   后面的是配送费
		DecimalFormat df = new DecimalFormat("#0.00");
		double[] countPrice = (double[])this.countPrice(store_list, null, null).get("price");
		double money = countPrice[0];  //订单金额
		double point = countPrice[1];  //饭点金额
		
		double limit_money = (double)dao.findForObject("OrderMapper.getLimitMoney", pd);
		boolean money_limit = true;
		
		if(money < limit_money){
			money = limit_money;
			money_limit = false;
		}
		if(point < limit_money){
			point = limit_money;
		}
		
		BigDecimal yunfei_init = new BigDecimal(money).multiply(new BigDecimal("0.18"));
		double yunfei_store = 0;
		if(null != store_list && store_list.size() != 0){
			yunfei_store = (store_list.size() - 1) * 3.99;
		}
		BigDecimal shui = new BigDecimal(money).multiply(new BigDecimal("0.08875"));
		BigDecimal yunfei = new BigDecimal(yunfei_init.doubleValue() + yunfei_store);
		
		BigDecimal total_money = new BigDecimal(0);  
		total_money = total_money.add(new BigDecimal(money));
		total_money = total_money.add(yunfei);
		total_money = total_money.add(shui);
		
		BigDecimal point_money = new BigDecimal(0);  
		point_money = point_money.add(new BigDecimal(point));
		point_money = point_money.add(yunfei);
		point_money = point_money.add(shui);
		
		if(isFirst) {
			//判断是否首单
			if(user.get("invite") != null && StringUtils.isNotBlank(user.getString("invite"))) {
				//判断是否被邀请注册，被邀请注册首单减3刀
				//如果总金额-优惠券金额小于等于0
				if(total_money.subtract(new BigDecimal(Const.REG_GIVE_USD)).compareTo(BigDecimal.ZERO) <= 0) {
					total_money = new BigDecimal("0");
				}else{
					total_money = total_money.subtract(new BigDecimal(Const.REG_GIVE_USD));
				}
				if(point_money.subtract(new BigDecimal(Const.REG_GIVE_USD)).compareTo(BigDecimal.ZERO) <= 0) {
					point_money = new BigDecimal("0");
				}else{
					point_money = point_money.subtract(new BigDecimal(Const.REG_GIVE_USD));
				}
				result.put("isFirst", true);
			}
		}else{
			result.put("isFirst", false);
		}
		
		String str = "";
		if(money_limit){
//			str += "合计：$";
//			str += df.format(total_money);
//			str += "（包含";
////			if(count == 0){
////				str+= "首单免配送费；";
////			} else {
////				str += "配送费$" +  df.format(yunfei_init.doubleValue() + yunfei_store) + "+";
////			}
//			str += "配送费$" +  df.format(yunfei_init.doubleValue() + yunfei_store) + "+";
//			str += "税费$" + df.format(shui) + "）";
//			
//			String str_en = "Total:$";
//			str_en += df.format(total_money);
//			str_en += "（Contain ";
//			if(count == 0){
//				str_en+= "First free delivery fee;";
//			} else {
//				str_en += "Delivery fee$" +  df.format(yunfei_init.doubleValue() + yunfei_store) + "+";
//			}
//			str_en += "Tax$" + df.format(shui) + "）";
//			
//			result.put("count_words_cn", str);
//			result.put("count_words_en", str_en);
			result.put("money_limit", "1");
		} else {
			str = "您的购物车总额不满" + limit_money + "USD，继续支付则按" + limit_money + "USD收取费用，返回可添加更多商品至购物车";
			String str_en = "The total amount of your shopping cart is less than " + limit_money + " yuan, and the continuing payment is charged at " + limit_money + "USD, and more goods can be added to the shopping cart.";
			result.put("count_words_cn", str);
			result.put("count_words_en", str_en);
			result.put("money_limit", "0");
		}
		result.put("yunfei", df.format(yunfei));									//配送费
		result.put("tax", df.format(shui));											//税
		result.put("total_money", df.format(total_money));							//合计
		result.put("point_total_money", df.format(point_money));					//饭点合计
		result.put("money", String.format("%.2f", money));							//菜品总额
		result.put("point", String.format("%.2f", point));							//菜品饭点总额
		result.put("DESCRIPTION_STR_CN", Constant.ORDER_DESCRIPTION_CN);
		result.put("DESCRIPTION_STR_EN", Constant.ORDER_DESCRIPTION_EN);
		
		//查询饭点余额
		PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		if(null == wallet){
			result.put("point_balance", "0");
		} else {
			result.put("point_balance",wallet.get("point"));
		}
		List<PageData> default_address = this.addressService.getDefaultAddressDetailByUserId(pd);
		if(default_address != null && default_address.size() > 0) {
			result.put("default_address", default_address.get(0));
		}else{
			result.put("default_address", null);
		}
		return result;
	}
	
	/**
	 * 计算价格
	 * @param store_list
	 * @param order_id
	 * @param user_id
	 * @return double[] 0订单金额，1饭点金额，2成本金额
	 * @throws Exception
	 */
	private PageData countPrice(List<PageData> store_list, String order_id, String user_id) throws Exception {
		PageData pd = new PageData();
		double[] rtn = new double[3];
		double money = 0;  //订单金额
		double pointMoney = 0; //饭点金额
		double costMoney = 0; //成本金额
		for (int i = 0; i < store_list.size(); i++) {
			HashMap<String, Object> store = store_list.get(i);
			//每个商家中的商品列表
			List<HashMap<String, Object>> foods_list =  (List<HashMap<String, Object>>)store.get("foods_list");
			for (int j = 0; j < foods_list.size(); j++) {
				HashMap<String, Object> foods = foods_list.get(j);
				String spec_id_list = (String)foods.get("spec_id_list");
				//根据规格查询价格，如果没有该规格的价格，则查询该商品的默认价格
				PageData fpd = new PageData();
				fpd.put("spec_id_list", spec_id_list);
				String price_obj = (String)dao.findForObject("StoreMapper.getFoodsPrice", fpd);
				double price = 0;
				double point = 0;
				double cost = 0;
				//如果用户选择的规格有价格， 则使用该规格计算
				if(null != price_obj){
					price = Double.parseDouble(price_obj);
				} else {
					//如果规格没有价格  用商品的默认价格
					//客户端提交价格为缓存购物车之前价格,如先加入购物车，后变更价格，支付或统计将使用原来价格，存在少收或多收金额
					//根据客户端提供食品编号查询食品单价
//					price =  Double.parseDouble( (String)foods.get("price"));
					fpd.put("foods_id", (String)foods.get("foods_id"));
					price = Double.parseDouble((String)dao.findForObject("StoreMapper.getDefaultPrice", fpd));
					PageData point_cost = (PageData)dao.findForObject("StoreMapper.getPointCostPrice", fpd);
					point = Double.parseDouble(point_cost.get("point").toString());
					cost = Double.parseDouble(point_cost.get("cost").toString());
				}
				//购买的份数
				int quantity = (Integer)foods.get("quantity");
				BigDecimal price_bd = new BigDecimal(price);
				BigDecimal quantity_bd = new BigDecimal(quantity);
				//该商品的金额
				BigDecimal foods_money = price_bd.multiply(quantity_bd);
				money += foods_money.doubleValue();
				pointMoney += new BigDecimal(point).multiply(quantity_bd).doubleValue();
				costMoney += new BigDecimal(cost).multiply(quantity_bd).doubleValue();
				
				//仅在生成订单中执行
				if(order_id != null) {
					//将foods保存到订单食品表里面
					fpd.put("foods_id", foods.get("foods_id"));					//食品编号
					fpd.put("order_id", order_id);								//订单编号
					fpd.put("store_id", store.get("store_id"));					//商家编号
					fpd.put("quantity", quantity);								//份数
					fpd.put("price", price);									//单价
					fpd.put("point_price", point);								//饭点单价
					fpd.put("cost_price", cost);								//成本单价
					dao.save("OrderMapper.saveOrderFoods", fpd);				//规格在前面已经放到fpd里面了
				}
				//仅在生成订单中执行
			}
			//仅在生成订单中执行
			if(order_id != null) {
				PageData spd = new PageData();
				spd.put("order_id", order_id);
				spd.put("store_id", store.get("store_id"));
				spd.put("user_id", user_id);
				dao.save("OrderMapper.saveOrderStore", spd);
			}
			//仅在生成订单中执行
		}
		rtn[0] = money;
		rtn[1] = pointMoney;
		rtn[2] = costMoney;
		pd.put("price", rtn);
		return pd;
	}
	
	//计算距离相关
	private static final double EARTH_RADIUS = 6378.137;
	
	private static double rad(double d){
        return d * Math.PI / 180.0;
    }
	
	private static double GetDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, d, sa2, sb2;
        lat1 = rad(lat1);
        lat2 = rad(lat2);
        a = lat1 - lat2;
        b = rad(long1 - long2);

        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2   * EARTH_RADIUS
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }
	
	/**
	 * 查询订单详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData getOrderDetail(PageData pd) throws Exception {
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
		}
		
		//订单编号
		String order_id = pd.getString("order_id");
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		
		//计算订单距离
		//计算订单收货人和配送员的距离
//		String exp_id = order.getString("exp_id");
//		if(!StringUtils.isEmpty(exp_id)){
//			String lat = order.getString("lat");
//			String lng = order.getString("lng");  //订单经纬度
//			String exp_lat = order.getString("exp_lat");
//			String exp_lng = order.getString("exp_lng");  //快递员经纬度
//			
//			if(StringUtils.isEmpty(lat) ||
//					StringUtils.isEmpty(lng) ||
//					StringUtils.isEmpty(exp_lat) ||
//					StringUtils.isEmpty(exp_lng)){
//				order.put("distance", "无法计算");
//			} else {
//				double lat1 = Double.parseDouble(lat);
//				double lng1 = Double.parseDouble(lng);
//						
//				double lat2 = Double.parseDouble(exp_lat);
//				double lng2 = Double.parseDouble(exp_lng);
//						
//				//计算距离
//				double d = this.GetDistance(lng1, lat1, lng2, lat2);
//				//格式化
//				DecimalFormat df   = new DecimalFormat("######0.00");   
//				String distance = df.format(d);
//				order.put("distance", distance + "km");
//			}
//		}
		
		List<PageData> order_store_list = (List<PageData>)dao.findForList("OrderMapper.getOrderStore" , pd);
		for (int i = 0; i < order_store_list.size(); i++) {
			//订单商店
			String store_id = order_store_list.get(i).getString("store_id");
			PageData fpd = new PageData();
			fpd.put("store_id", store_id);
			fpd.put("order_id", order_id);
			List<PageData> foods_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoods" , fpd);
			int foods_count = 0;
			for (int j = 0; j < foods_list.size(); j++) {
				//当前食物选择的规格
				String spec_id_list = foods_list.get(j).getString("spec_id_list");
				int count = (int)foods_list.get(j).get("foods_quantity");
				foods_count += count;
				PageData spd = new PageData();
				spd.put("spec_id_list", spec_id_list);
				if(!StringUtils.isEmpty(spec_id_list)){
					List<PageData> spec_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoodsSpec" , spd);
					foods_list.get(j).put("spec_list", spec_list);
				}
				
			}
			order_store_list.get(i).put("foods_list", foods_list);
			order_store_list.get(i).put("foods_count", foods_count);
		}
		order.put("store_list", order_store_list);
		return order;
	}

	/**
	 * 查询订单列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData getOrderList(PageData pd) throws Exception {
		PageData result = new PageData();
		
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
		}
		
		Page page = new Page();
		page.setPd(pd);
		String index = pd.getString("page");
		String page_size = pd.getString("page_size");
		int pg = 0;
		int size = 0;
		if(StringUtils.isEmpty(index)){
			pg = 1;
		} else {
			pg = Integer.parseInt(index);
		}
		if(StringUtils.isEmpty(page_size)){
			size = 5;
		}  else {
			size = Integer.parseInt(page_size);
		}
		page.setCurrentPage(pg);
		page.setShowCount(size);
		//分页查询
		List<PageData> list = (List<PageData>)dao.findForList("OrderMapper.datalistPage", page);
		for (int i = 0; i < list.size(); i++) {
			PageData order = list.get(i);
			//订单商店信息
			PageData ospd = new PageData();
			ospd.put("order_id", order.getString("order_id"));
			List<PageData> order_store_list = (List<PageData>)dao.findForList("OrderMapper.getOrderStore" , ospd);
			for (int j = 0; j < order_store_list.size(); j++) {
				//订单商品
				String store_id = order_store_list.get(j).getString("store_id");
				PageData fpd = new PageData();
				fpd.put("store_id", store_id);
				fpd.put("order_id", order.getString("order_id"));
				List<PageData> foods_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoods" , fpd);
				int quantity = 0;
				for (int k = 0; k < foods_list.size(); k++) {
					int fen = (Integer)foods_list.get(k).get("foods_quantity");
					quantity += fen;
				}
				order_store_list.get(j).put("quantity", quantity);
			}
			list.get(i).put("store_list", order_store_list);
		}
		result.put("rows", list);
		result.put("total", page.getTotalResult());
		result.put("total_page", page.getTotalPage());
		return result;
	}

	/**
	 * 修改订单状态为已支付
	 */
	@Override
	public PageData updateOrderPayState(PageData pd) throws Exception {
		PageData result = new PageData();
		dao.update("OrderMapper.updateOrderPayState", pd);
		//根据订单金额判断是否增加饭点
		//用户钱包
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		
		String user_id = order.getString("user_id");
		pd.put("user_id", user_id);
		PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		//如果没有钱包,创建空的钱包
		if(null == wallet){
			dao.save("WalletMapper.saveWallet", pd);
			wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		}
		//饭点兑换比例
		String rewardpoint = (String)dao.findForObject("WalletMapper.getWalletParam", pd);
		int point = 0;
		if(StringUtils.isEmpty(rewardpoint)){
			point = 50;
		} else {
			point = Integer.parseInt(rewardpoint);
		}
		
		BigDecimal money = (BigDecimal)order.get("money");
		PageData ppd = new PageData();
		ppd.put("user_id", user_id);
		if(money.doubleValue() >= point){
			//饭点+1并且保存到累计金额
			ppd.put("point", 1);
			ppd.put("money", money.doubleValue());
			dao.update("WalletMapper.addPoint", ppd);
		} 
		//如果不到point 并且大于20 则保存到累计金额
		else if(money.doubleValue() >= 20){
			ppd.put("point", 0);
			ppd.put("money", money.doubleValue());
			dao.update("WalletMapper.addPoint", ppd);
		}
		return result;
	}
	
	/**
	 * applepay 支付回调
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData updateOrderPayState2(PageData pd) throws Exception {
		PageData result = new PageData();
		
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
		}
		
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		if(null == order){
			throw new NjmsException(ExEnum.EX_NO_ORDER);
		}
		
		Object obj = dao.update("OrderMapper.updateOrderPayState", pd);
		
		String user_id = order.getString("user_id");
		pd.put("user_id", user_id);
		PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		//如果没有钱包,创建空的钱包
		if(null == wallet){
			dao.save("WalletMapper.saveWallet", pd);
			wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		}
		//饭点兑换比例
		String rewardpoint = (String)dao.findForObject("WalletMapper.getWalletParam", pd);
		int point = 0;
		if(StringUtils.isEmpty(rewardpoint)){
			point = 50;
		} else {
			point = Integer.parseInt(rewardpoint);
		}
		
		BigDecimal money = (BigDecimal)order.get("money");
		PageData ppd = new PageData();
		ppd.put("user_id", user_id);
		if(money.doubleValue() >= point){
			//饭点+1并且保存到累计金额
			ppd.put("point", 1);
			ppd.put("money", money.doubleValue());
			dao.update("WalletMapper.addPoint", ppd);
		} 
		//如果不到point 并且大于20 则保存到累计金额
		else if(money.doubleValue() >= 20){
			ppd.put("point", 0);
			ppd.put("money", money.doubleValue());
			dao.update("WalletMapper.addPoint", ppd);
		}
		result.put("status", "success");
		return result;
	}

	@Override
	public PageData getExpLngLat(PageData pd) throws Exception {
		PageData result = (PageData)dao.findForObject("OrderMapper.getExpLngLat", pd);
		return result;
	}

	@Override
	public PageData getOrderMoney(PageData pd) throws Exception {
		PageData result = (PageData)dao.findForObject("OrderMapper.getOrderMoney", pd);
		return result;
	}

	@Override
	public String getRealTime(PageData pd) throws Exception {
		String result = (String)dao.findForObject("OrderMapper.getRealTime", pd);
		return result;
	}

	/**
	 * 根据邮编查询配送时间列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData getDistributionTimeListByZipCode(PageData pd) throws Exception {
		PageData pageData = new PageData();
		List<PageData> distributionTime = (List<PageData>)dao.findForList("OrderMapper.getDistributionTimeListByZipCode", pd);
		if(distributionTime!=null && distributionTime.size()<=0) {
			PageData defaultPd = new PageData();
			defaultPd.put("distribution_time", Constant.ALL_DAY_DISTRIBUTION_TIME);
			distributionTime.add(defaultPd);
		}else{
			//根据当前时间判断配送时间显示列表，只显示当前时间+1小时以后的开始配送时间列表
			for(int i = distributionTime.size()-1; i >= 0; i--) {
				if(!queryIsDistribution(distributionTime.get(i).getString("distribution_time"))) {
					distributionTime.remove(i);
				}
			}
			//当天没有配送时间可选
			if(distributionTime.size() <= 0){
				throw new NjmsException(ExEnum.EX_NO_DISTRIBUTION_TIME_ERROR);
			}
		}
		pageData.put("rows", distributionTime);
		return pageData;
	}
	
	/**
	 * 根据当前时间+1小时判断是否大于配送时间，小于与全天配送返回true
	 * @param time
	 * @return
	 */
	private boolean queryIsDistribution(String time) {
		//19:00-20:30
		if(!time.equals(Constant.ALL_DAY_DISTRIBUTION_TIME)){
			time = time.split("-")[0];
			TimeZone.setDefault(TimeZone.getTimeZone(Const.SHIQU));
			Calendar ca = Calendar.getInstance(TimeZone.getTimeZone(Const.SHIQU));
			ca.add(Calendar.HOUR, 1);
			Date date = ca.getTime();
			return DateUtil.compareDate(time, DateUtil.getFormatDate(date, null, null, "HH:mm"), "HH:mm");
		}
		return true;
	}
	
	/**
	 * 修改订单表，保存支付通道返回的ID
	 */
	@Override
	public PageData updateOrderChargeId(PageData pd) throws Exception {
		dao.update("OrderMapper.updateOrderChargeId", pd);
		return pd;
	}
	
	/**
	 * 检查优惠券
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData useCoupon(PageData pd) throws Exception {
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
		}
		PageData coupon = this.couponService.findByCouponNum(pd);
		if(coupon != null && coupon.getString("STATUS").equals("0")) {
			
		}else{
			throw new NjmsException(ExEnum.EX_NO_COUPON_ERROR);
		}
		
		//根据优惠券修改订单金额
		BigDecimal yunfei = new BigDecimal(pd.getString("yunfei"));
		BigDecimal tax = new BigDecimal(pd.getString("tax"));
		BigDecimal total_money = new BigDecimal(pd.getString("total_money"));
		BigDecimal point_total_money = new BigDecimal(pd.getString("point_total_money"));
		BigDecimal coupon_money = new BigDecimal(coupon.getString("COUPON_MONEY"));
		//如果总金额-优惠券金额小于等于0
		if(total_money.subtract(coupon_money).compareTo(BigDecimal.ZERO) <= 0) {
			coupon.put("yunfei", "0");
			coupon.put("tax", "0");
			coupon.put("total_money", "0");
		}else{
			coupon.put("yunfei", yunfei+"");
			coupon.put("tax", tax+"");
			coupon.put("total_money", total_money.subtract(coupon_money)+"");
		}
		if(point_total_money.subtract(coupon_money).compareTo(BigDecimal.ZERO) <= 0) {
			coupon.put("point_total_money", "0");													//饭点总金额
		}else{
			coupon.put("point_total_money", point_total_money.subtract(coupon_money)+"");			//饭点总金额
		}
		return coupon;
	}
	
}
