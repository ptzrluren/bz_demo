package com.flc.service.order.impl;

import com.alibaba.druid.util.StringUtils;
import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.order.OrderManager;
import com.flc.util.Const;
import com.flc.util.PageData;
import com.flc.util.PrintUtil;
import com.flc.util.UuidUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Service("orderService")
public class OrderService implements OrderManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	
	
	/**
	 * 查询有未接订单的邮编列表
	 */
	@Override
	public PageData getOrderCodeList(PageData pd) throws Exception {
		PageData result = new PageData();
		//分页参数
		String page = pd.getString("page");
		int _page = 0;
		String show_count = pd.getString("page_size");
		int _show_count = 0;
		if(StringUtils.isEmpty(page)){
			_page = 1;
		} else {
			_page = Integer.parseInt(page);
		}
		if(StringUtils.isEmpty(show_count)){
			_show_count = 10;
		} else {
			_show_count = Integer.parseInt(show_count);
		}
		
		Page pg = new Page();
		pg.setCurrentPage(_page);
		pg.setShowCount(_show_count);;
		pg.setPd(pd);
		List<PageData> list = (List<PageData>)dao.findForList("OrderMapper.datalistPageZipCode4HomePage", pg);
		result.put("rows", list);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		return result;
	}


	@Override
	public PageData getOrderListByZipCode(PageData pd) throws Exception {
		PageData result = new PageData();
		PageData user = (PageData)dao.findForObject("UserMapper.findOne", pd);
		if(null == user){
			throw new NjmsException(ExEnum.EX_NO_USER);
		}
		
		//分页参数
		String page = pd.getString("page");
		int _page = 0;
		String show_count = pd.getString("page_size");
		int _show_count = 0;
		if(StringUtils.isEmpty(page)){
			_page = 1;
		} else {
			_page = Integer.parseInt(page);
		}
		if(StringUtils.isEmpty(show_count)){
			_show_count = 10;
		} else {
			_show_count = Integer.parseInt(show_count);
		}
				
		Page pg = new Page();
		pg.setCurrentPage(_page);
		pg.setShowCount(_show_count);;
		pg.setPd(pd);
		List<PageData> list = (List<PageData>)dao.findForList("OrderMapper.datalistPageOrderListByZipCode", pg);
		//商家列表
		for (int i = 0; i < list.size(); i++) {
			String order_id = list.get(i).getString("order_id");
			PageData spd = new PageData();
			spd.put("order_id", order_id);
			List<PageData> store_list = (List<PageData>)dao.findForList("OrderMapper.getOrderStoreList", spd);
			//查询订单中的商家商品
			for (int j = 0; j < store_list.size(); j++) {
				int count = 0;
				//商家id
				String store_id = store_list.get(j).getString("store_id");
				//查询商家商品以及取件状态
				PageData gpd = new PageData();
				gpd.put("store_id", store_id);
				gpd.put("order_id", order_id);
				List<PageData> foods_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoodsList", gpd);
				for (int k = 0; k < foods_list.size(); k++) {
					int quantity = (Integer)foods_list.get(k).get("foods_quantity");
					count += quantity;
				}
				store_list.get(j).put("quantity", count);
				 list.get(i).put("store_list", store_list);
			}
		}
		result.put("rows", list);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		//查询快递员上班状态
		String onwork = user.getString("on_work");
		result.put("onwork", onwork);
		return result;
	}

	/**
	 * 快递员接单
	 */
	@Override
	public PageData expOrders(PageData pd) throws Exception {
		PageData result = new PageData();
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		//该订单不存在
		if(null == order){
			throw new NjmsException(ExEnum.EX_NO_ORDER);
		}
		String exp_id = order.getString("exp_id");
		//该订单已经被别人接单了
		if(!StringUtils.isEmpty(exp_id)){
			throw new NjmsException(ExEnum.EX_ORDER_HAS_TAKEN);
		}
		dao.update("OrderMapper.expOrders", pd);
		
		//打印订单
		print(pd);
		return result;
	}

	
	public PageData print(PageData pd) throws Exception {
		
		
		
		//订单编号
		String order_id = pd.getString("order_id");
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		
		//备注
		String remark = order.getString("remark");
		if(null == remark){
			remark = "";
		}
		
		List<PageData> order_store_list = (List<PageData>)dao.findForList("OrderMapper.getOrderStore" , pd);
		for (int i = 0; i < order_store_list.size(); i++) {
			String sn = order_store_list.get(i).getString("print_no");
			//打印编号
			String content;
			content = "<CB>" + remark + "</CB><BR>";
			content += "名称       单价    金额    数量<BR>";
			content += "--------------------------------<BR>";
//			content += "饭　　　　　　 1.0    1   1.0<BR>";
//			content += "炒饭　　　　　 10.0   10  10.0<BR>";
//			content += "蛋炒饭　　　　 10.0   10  100.0<BR>";
//			content += "鸡蛋炒饭　　　 100.0  1   100.0<BR>";
//			content += "番茄蛋炒饭　　 1000.0 1   100.0<BR>";
//			content += "西红柿蛋炒饭　 1000.0 1   100.0<BR>";
//			content += "西红柿鸡蛋炒饭 100.0  10  100.0<BR>";
//			content += "备注：加辣<BR>";
//			content += "--------------------------------<BR>";
//			content += "合计：xx.0元<BR>";
//			content += "送货地点：广州市南沙区xx路xx号<BR>";
//			content += "联系电话：13888888888888<BR>";
//			content += "订餐时间：2016-08-08 08:08:08<BR>";
//			content += "<QR>http://www.dzist.com</QR>";
			
			
			//订单商店
			String store_id = order_store_list.get(i).getString("store_id");
			String store_name = order_store_list.get(i).getString("store_name_cn");
			PageData fpd = new PageData();
			fpd.put("store_id", store_id);
			fpd.put("order_id", order_id);
			List<PageData> foods_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoods" , fpd);
			int foods_count = 0;
			double total_money = 0;
			for (int j = 0; j < foods_list.size(); j++) {
				//当前食物选择的规格
				String spec_id_list = foods_list.get(j).getString("spec_id_list");
				String foods_name = foods_list.get(j).getString("foods_name_cn");
				int foods_quantity = (int)foods_list.get(j).get("foods_quantity");
				BigDecimal foods_price = (BigDecimal)foods_list.get(j).get("price");
				BigDecimal money = (BigDecimal)foods_list.get(j).get("money");
				
				if(null != money){
					total_money += money.doubleValue();
				}
				
				int count = (int)foods_list.get(j).get("foods_quantity");
				foods_count += count;
				PageData spd = new PageData();
				spd.put("spec_id_list", spec_id_list);
				StringBuffer spec_str = new StringBuffer();
				if(!StringUtils.isEmpty(spec_id_list)){
					List<PageData> spec_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoodsSpec" , spd);
					foods_list.get(j).put("spec_list", spec_list);
					if(null != spec_list && spec_list.size() != 0){
						for (int k = 0; k < spec_list.size(); k++) {
							spec_str.append(spec_list.get(k).getString("spec_name_cn"));
							spec_str.append("  ");
						}
					}
				}
				//商品名称
				//               价格  数量  金额
				content += foods_name + "<BR>";
				content += "             " + foods_price + "  " + money + "  " + foods_quantity + "<BR>";
				if(spec_str.length() != 0){
					content += spec_str.toString() + "<BR>";
				}
				content += "--------------------------------<BR>";
			}
			
			content += "商家：" + store_name + "<BR>";
			content += "订单编号：" + order_id + "<BR>";
			content += "合计：" + total_money + "元<BR>";
			content += "送货地点：" + order.getString("cust_address") + "<BR>";
			content += "联系电话：" + order.getString("cust_phone") + "<BR>";
			
			System.out.println(content);
			PrintUtil.print(sn, content);
			PrintUtil.print(sn, content);
			order_store_list.get(i).put("foods_list", foods_list);
			order_store_list.get(i).put("foods_count", foods_count);
		}
		order.put("store_list", order_store_list);
		return order;
	}
	

	@Override
	public PageData getExpOrderCodeList(PageData pd) throws Exception {
		PageData result = new PageData();
		//分页参数
		String page = pd.getString("page");
		int _page = 0;
		String show_count = pd.getString("page_size");
		int _show_count = 0;
		if(StringUtils.isEmpty(page)){
			_page = 1;
		} else {
			_page = Integer.parseInt(page);
		}
		if(StringUtils.isEmpty(show_count)){
			_show_count = 10;
		} else {
			_show_count = Integer.parseInt(show_count);
		}
		
		Page pg = new Page();
		pg.setCurrentPage(_page);
		pg.setShowCount(_show_count);;
		pg.setPd(pd);
		List<PageData> list = (List<PageData>)dao.findForList("OrderMapper.datalistPageZipCode4ExpOrder", pg);
		result.put("rows", list);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		return result;
	}


	/**
	 * 上传位置
	 */
	@Override
	public PageData uploadLocation(PageData pd) throws Exception {
		String lng = pd.getString("lng");
		String lat = pd.getString("lat");
		String code = pd.getString("token");
		/**
		 * 检查Token
		 */
//		if(!checkToken(pd)){
//			throw new NjmsException(ExEnum.EX_TOKEN_INVALID);
//		}
		
		/**
		 * 验证参数
		 */
		if(StringUtils.isEmpty(lng)||StringUtils.isEmpty(lat)||StringUtils.isEmpty(code)){
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
		
		this.dao.update("OrderMapper.uploadLocation", pd);
		PageData result = new PageData();
		return result;
	}
	
	/**
	 * 检查token
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	private Boolean checkToken(PageData pd) throws Exception{
		String tokenName="token", useridName="user_id";
		String _token = pd.getString(tokenName);
		String _key   = pd.getString(useridName);
		if(StringUtils.isEmpty(_token)||StringUtils.isEmpty(_key))return false;
		
		pd.put("_key", _key);
		
		try {
			PageData res = (PageData)this.dao.findForObject("UserMapper.getToken", pd);
			if(_token.equals(res.get("token"))){
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}


	@Override
	public PageData getExpOrderListByZipCode(PageData pd) throws Exception {
		PageData result = new PageData();
		String zip_code = pd.getString("zip_code");
		if(StringUtils.isEmpty(zip_code) || StringUtils.isEmpty(pd.getString("user_id"))){
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
		
		//分页参数
		String page = pd.getString("page");
		int _page = 0;
		String show_count = pd.getString("page_size");
		int _show_count = 0;
		if(StringUtils.isEmpty(page)){
			_page = 1;
		} else {
			_page = Integer.parseInt(page);
		}
		if(StringUtils.isEmpty(show_count)){
			_show_count = 10;
		} else {
			_show_count = Integer.parseInt(show_count);
		}
				
		Page pg = new Page();
		pg.setCurrentPage(_page);
		pg.setShowCount(_show_count);;
		pg.setPd(pd);
		List<PageData> list = (List<PageData>)dao.findForList("OrderMapper.datalistPageExpOrderListByZipCode", pg);
		//商家列表
		for (int i = 0; i < list.size(); i++) {
			String order_id = list.get(i).getString("order_id");
			PageData spd = new PageData();
			spd.put("order_id", order_id);
			List<PageData> store_list = (List<PageData>)dao.findForList("OrderMapper.getOrderStoreList", spd);
			//查询订单中的商家商品
			for (int j = 0; j < store_list.size(); j++) {
				int count = 0;
				//商家id
				String store_id = store_list.get(j).getString("store_id");
				//查询商家商品以及取件状态
				PageData gpd = new PageData();
				gpd.put("store_id", store_id);
				gpd.put("order_id", order_id);
				List<PageData> foods_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoodsList", spd);
				store_list.get(j).put("foods_list", foods_list);
				 list.get(i).put("store_list", store_list);
			}
		}
		result.put("rows", list);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		return result;
	}

	
	/**
	 * 快递员取件
	 */
	@Override
	public PageData pickupOrderFoods(PageData pd) throws Exception {
		PageData result = new PageData();
		//每次取件需要判断是否该订单所有菜都被取件了 如果都被取件了 ，需求修改订单状态
		dao.update("OrderMapper.pickupOrderFoods", pd);
		//有取件了就将订单状态改成取件中
		dao.update("OrderMapper.pickupOrder", pd);
		result.put("order_state", "0");  //修改  这边不改变状态，又在路上按钮同意操作
		//查询该订单没有被取件的商品的数量 如果=0 则修改订单的状态
//		int count = (int)dao.findForObject("OrderMapper.getUnpickupFoodsCount", pd);
//		if(count == 0){
//			dao.update("OrderMapper.orderPickup", pd);
//			result.put("order_state", "1");
//		} else {
//			result.put("order_state", "0");
//		}
		
		return result;
	}

	/**
	 * 地图接单-查询所有已支付的未接订单
	 */
	@Override
	public PageData getOrderList4Map(PageData pd) throws Exception {
		PageData result = new PageData();
		
		PageData user = (PageData)dao.findForObject("UserMapper.findOne", pd);
		if(null == user){
			throw new NjmsException(ExEnum.EX_NO_USER);
		}
		
		//查询所有已支付的未接订单
		List<PageData> list = (List<PageData>)dao.findForList("OrderMapper.getOrderList4Map", pd);
		result.put("rows", list);
		//查询快递员上班状态
		String onwork = user.getString("on_work");
		result.put("onwork", onwork);
		return result;
	}


	/**
	 * 在路上，修改当前所有工单状态为在路上
	 * 
	 */
	@Override
	public PageData onTheWay(PageData pd) throws Exception {
		PageData result = new PageData();
		dao.update("OrderMapper.onTheWay", pd);
		return result;
	}


	/**
	 * 确认送达
	 */
	@Override
	public PageData sendTo(PageData pd) throws Exception {
		//根据orderid查询userid
		PageData orderById = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		//根据userid查询是否有已完成外卖订单
		List<PageData> orderByUserId = (List<PageData>)dao.findForList("OrderMapper.getOrderDetailByUserId", orderById);
		//如果没有，加饭点
		if(orderByUserId != null && orderByUserId.size() == 0) {
			//被邀请人信息
			PageData user = (PageData)dao.findForObject("OrderMapper.getUserByUserId", orderById);
			//找到邀请人的编号，添加饭点
			PageData inviteUser = (PageData)dao.findForObject("OrderMapper.getInviteUserByUserId", orderById);
			if(inviteUser != null) {
				PageData userWalletLogPd = new PageData();
				userWalletLogPd.put("POINT", Const.INVITE_GIVE_POINT);
				userWalletLogPd.put("WL_ID", UuidUtil.get32UUID());
				userWalletLogPd.put("USER_ID", inviteUser.getString("user_id"));
				userWalletLogPd.put("TYPE", ExEnum.WL_TYPE4.getCode());
				userWalletLogPd.put("REMARK", ExEnum.WL_TYPE4.getDes_cn()+"("+user.getString("phone")+")");
				userWalletLogPd.put("REMARK_EN", ExEnum.WL_TYPE4.getDes_en()+"("+user.getString("phone")+")");
				dao.update("OrderMapper.saveUserWalletLog", userWalletLogPd);

				PageData exchangePoint = new PageData();
				exchangePoint.put("user_id", inviteUser.getString("user_id"));
				exchangePoint.put("point_add", Const.INVITE_GIVE_POINT);
				dao.update("WalletMapper.exchangePointNew", exchangePoint);
			}
		}
		dao.update("OrderMapper.sendTo", pd);
		
		//外卖订单完成计算到可兑换金额
		PageData exchangePoint = new PageData();
		if(orderById.getString("payment_method").equals("5")) {
			//易点支付
			exchangePoint.put("money", orderById.get("point_total_money"));
		}else{
			exchangePoint.put("money", orderById.get("total_money"));
		}
		exchangePoint.put("user_id", orderById.getString("user_id"));
		exchangePoint.put("point_add", "0");//仅累计兑换金额，对易点不做改动
		dao.update("WalletMapper.exchangePointNew", exchangePoint);
		
		return new PageData();
	}

	/**
	 * 快递员接单
	 */
	@Override
	public PageData takeOrders(PageData pd) throws Exception {
//		PageData result = new PageData();
//		String order_id = pd.getString("order_id");
//		//订单号不能为空
//		if(StringUtils.isEmpty(order_id)){
//			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
//		}
//		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
//		String exp_id = order.getString("exp_id");
//		//该订单已经被接单了 
//		if(!StringUtils.isEmpty(exp_id)){
//			throw new NjmsException(ExEnum.EX_ORDER_HAS_TAKEN);
//		}
//		dao.update("OrderMapper.tokeOrders", pd);
//		return result;
		PageData result = new PageData();
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		//该订单不存在
		if(null == order){
			throw new NjmsException(ExEnum.EX_NO_ORDER);
		}
		String exp_id = order.getString("exp_id");
		//该订单已经被别人接单了
		if(!StringUtils.isEmpty(exp_id)){
			throw new NjmsException(ExEnum.EX_ORDER_HAS_TAKEN);
		}
		dao.update("OrderMapper.expOrders", pd);
		
		//打印订单
		print(pd);
		return result;
	}


	/**
	 * 快递员查询订单详情
	 */
	@Override
	public PageData getOrderDetail(PageData pd) throws Exception {
		PageData result = new PageData();
		//订单
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		//查询订单商家
		if(null == order){
			throw new NjmsException(ExEnum.EX_NO_ORDER);
		}
		String remark = order.getString("remark");
		if(StringUtils.isEmpty(remark)){
			order.put("remark", "");
		}
		
		//计算订单收货人和配送员的距离
		String lat = order.getString("lat");
		String lng = order.getString("lng");  //订单经纬度
		String exp_lat = order.getString("exp_lat");
		String exp_lng = order.getString("exp_lng");  //快递员经纬度
		if(StringUtils.isEmpty(lat) || 
				StringUtils.isEmpty(lng) || 
				StringUtils.isEmpty(exp_lat) || 
				StringUtils.isEmpty(exp_lng)){
			order.put("distance", "无法计算");
		} else {
			double lat1 = Double.parseDouble(lat);
			double lng1 = Double.parseDouble(lng);
					
			double lat2 = Double.parseDouble(exp_lat);
			double lng2 = Double.parseDouble(exp_lng);
					
			//计算距离
			double d = this.GetDistance(lng1, lat1, lng2, lat2);
			//格式化
			DecimalFormat df   = new DecimalFormat("######0.00");   
			String distance = df.format(d);
			order.put("distance", distance + "km");
		}
		
		
		String order_id = order.getString("order_id");
		PageData spd = new PageData();
		spd.put("order_id", order_id);
		List<PageData> store_list = (List<PageData>)dao.findForList("OrderMapper.getOrderStoreList", spd);
		
		//查询订单中的商家商品
		for (int i = 0; i < store_list.size(); i++) {
			//商家id
			String store_id = store_list.get(i).getString("store_id");
			//查询商家商品以及取件状态
			PageData gpd = new PageData();
			gpd.put("store_id", store_id);
			gpd.put("order_id", order_id);
			List<PageData> foods_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoodsList", gpd);
			for (int j = 0; j < foods_list.size(); j++) {
				String spec_id_list = foods_list.get(j).getString("spec_id_list");
				if(!StringUtils.isEmpty(spec_id_list)){
					PageData specpd = new PageData();
					specpd.put("spec_id_list", spec_id_list);
					List<PageData> spec_list = (List<PageData>)dao.findForList("OrderMapper.getOrderFoodsSpec", specpd);
					foods_list.get(j).put("spec_list", spec_list);
				}
			}
			store_list.get(i).put("foods_list", foods_list);
		}
		order.put("store_list", store_list);
		return order;
	}
	
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


	@Override
	public PageData payOrder(PageData pd) throws Exception {
		PageData result = new PageData();
		String order_id = pd.getString("order_id");
//		String user_id = pd.getString("user_id");
		String token = pd.getString("token");
		//订单
		PageData order = (PageData)dao.findForObject("OrderMapper.getOrderDetail", pd);
		//查询订单商家
		if(null == order){
			throw new NjmsException(ExEnum.EX_NO_ORDER);
		}
		//查询订单状态，如果没有取件完成，不给确认收款
		String state = order.getString("state");
		if(!"4".equals(state) && !"3".equals(state)){
			throw new NjmsException(ExEnum.EX_ORDER_STATE_ERROR);
		}
		
		PageData user = (PageData)dao.findForObject("UserMapper.findOne", pd);
		if(null == user){
			throw new NjmsException(ExEnum.EX_NO_USER);
		}
		
		dao.update("OrderMapper.payOrder", pd);
		
		//根据订单金额判断是否增加饭点
		//用户钱包
				
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
}
