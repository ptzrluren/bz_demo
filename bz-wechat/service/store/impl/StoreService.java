package com.flc.service.store.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.store.StoreManager;
import com.flc.service.user.UserManager;
import com.flc.util.PageData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("storeService")
public class StoreService implements StoreManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	@Autowired
	private UserManager userService;
	
	/**
	 * 查询商家列表
	 */
	@Override
	public PageData getStoreList(PageData pd) throws Exception {
		PageData result = new PageData();
		String requestPath = pd.getString("requestPath");
		String page = pd.getString("page");
		String page_size = pd.getString("page_size");
		int _page = 0;
		if(null != page && !"".equals(page)){
			_page  = Integer.parseInt(page);
		}
		
		//add by liubin at 20181230 ios bug 临时注释
//		int _page_size = 9;
//		if(null != page_size && !"".equals(page_size)){
//			_page_size  = Integer.parseInt(page_size);
//		}
		int _page_size = 99999999;
		
		Page pg = new Page();
		pg.setPd(pd);
		pg.setCurrentPage(_page);
		pg.setShowCount(_page_size);
		
		//mod by liubin at 20180322,客户要求修改首页商店列表展示全部
//		List<PageData> storeList = (List<PageData>)dao.findForList("StoreMapper.datalist", pg);
//		for (int i = 0; i < storeList.size(); i++) {
//			String store_image = requestPath + storeList.get(i).getString("store_image");
//			storeList.get(i).put("store_image", store_image);
//		}
//		if(_page == 1) {
//			result.put("rows", storeList);
//			result.put("total", storeList.size());
//			result.put("total_page", 1);
//		}else{
//			List<PageData> nullList = new ArrayList<PageData>();
//			result.put("rows", nullList);
//			result.put("total", storeList.size());
//			result.put("total_page", 1);
//		}
		
		//mod by liubin at 20180816,客户要求修改首页商店列表,每页展示9个商店
		List<PageData> storeList = (List<PageData>)dao.findForList("StoreMapper.datalistPage", pg);
		for (int i = 0; i < storeList.size(); i++) {
			String store_image = requestPath + storeList.get(i).getString("store_image");
			storeList.get(i).put("store_image", store_image);
		}
//		for (int i = 0; i < storeList.size(); i++) {
//			String store_image = requestPath + storeList.get(i).getString("store_image");
//			storeList.get(i).put("store_image", store_image);
//			String format = "HH:mm:ss";
//			SimpleDateFormat sdf2 = new SimpleDateFormat(format);
//			if(DateUtil.isEffectiveDate(new SimpleDateFormat(format).parse(sdf2.format(new Date())), 
//					new SimpleDateFormat(format).parse(storeList.get(i).getString("business_hours_start")), 
//					new SimpleDateFormat(format).parse(storeList.get(i).getString("business_hours_end")))) {
//				storeList.get(i).put("onwork", "1");
//			}else{
//				storeList.get(i).put("onwork", "0");
//			}
//		}
//		Collections.sort(storeList, new Comparator<PageData>() {
//			public int compare(PageData o1, PageData o2) {
//				Integer name1 = Integer.valueOf(o1.get("onwork").toString());// name1是从你list里面拿出来的一个
//				Integer name2 = Integer.valueOf(o2.get("onwork").toString()); // name1是从你list里面拿出来的第二个name
//				return name2.compareTo(name1);
//			}
//		});
		result.put("rows", storeList);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		return result;
	}

	/**
	 * 查询收藏的商家列表
	 */
	@Override
	public PageData getCollectionStoreList(PageData pd) throws Exception {
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
		
		String page = pd.getString("page");
		String page_size = pd.getString("page_size");
		int _page = 0;
		if(null != page && !"".equals(page)){
			_page  = Integer.parseInt(page);
		}
		
		int _page_size = 10;
		if(null != page_size && !"".equals(page_size)){
			_page_size  = Integer.parseInt(page_size);
		}
		
		Page pg = new Page();
		pg.setPd(pd);
		pg.setCurrentPage(_page);
		pg.setShowCount(_page_size);
		String requestPath = pd.getString("requestPath");
		List<PageData> storeList = (List<PageData>)dao.findForList("StoreMapper.datalistPageCollectionStore", pg);
		for (int i = 0; i < storeList.size(); i++) {
			String store_image = requestPath + storeList.get(i).getString("store_image");
			storeList.get(i).put("store_image", store_image);
			
			
		}
		result.put("rows", storeList);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		return result;
	}

	
	/**
	 * 收藏/取消收藏商家
	 */
	@Override
	public PageData collectStore(PageData pd) throws Exception {
		PageData result = new PageData();
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		String user_id = pd.getString("user_id");
		String token = pd.getString("token");
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		//查询是否收藏了该店
		PageData store_collection = (PageData)dao.findForObject("StoreMapper.getCollectionStore", pd);
		if(null == store_collection){
			dao.update("StoreMapper.collectStore", pd);
			result.put("collection_state", "1");
		} else {
			dao.update("StoreMapper.unCollectStore", pd);
			result.put("collection_state", "0");
		}
		return result;
	}

	
	/**
	 * 查询商家详情
	 */
	@Override
	public PageData getStoreDetail(PageData pd) throws Exception {
//		PageData result = new PageData();
		PageData store = (PageData)dao.findForObject("StoreMapper.findOne", pd);
		
		String requestPath = pd.getString("requestPath");
		if(StringUtils.isEmpty(requestPath)){
			requestPath = "http://47.90.202.220:9092/upload";
		}
		String store_image = requestPath + store.getString("store_image");
		store.put("store_image", store_image);
		
		String user_id = pd.getString("user_id");
		if(StringUtils.isEmpty(user_id)){
			store.put("has_collect", "0");
		} else {
			//查询是否收藏了该店
			PageData store_collection = (PageData)dao.findForObject("StoreMapper.getCollectionStore", pd);
			if(null == store_collection){
				store.put("has_collect", "0");
			} else {
				store.put("has_collect", "1");
			}
		}
		return store;
	}

	/**
	 * 查询商家商品
	 */
	@Override
	public PageData getStoreFoodsList(PageData pd) throws Exception {
		PageData result = new PageData();
		String store_id = pd.getString("store_id");
		String requestPath = pd.getString("requestPath");
		//查询商家商品的分类
		List<PageData> type_list = (List<PageData>)dao.findForList("StoreMapper.getTypeList", pd);
		//查询商品的list
		for (int i = 0; i < type_list.size(); i++) {
			PageData fpd = new PageData();
			fpd.put("store_id", store_id);
			String type_id = type_list.get(i).getString("type_id");
			fpd.put("type_id", type_id);
			//食物列表
			List<PageData> foods_list = (List<PageData>)dao.findForList("StoreMapper.getFoodsByType", fpd);
			//查询食物下面的规格分类
			for (int j = 0; j < foods_list.size(); j++) {
				//设置食物的图片
				String foods_image_cn = requestPath + foods_list.get(j).getString("foods_image_cn");
				String foods_image_en = requestPath + foods_list.get(j).getString("foods_image_en");
				foods_list.get(j).put("foods_image_cn", foods_image_cn);
				foods_list.get(j).put("foods_image_en", foods_image_en);
				
				PageData cpd = new PageData();
				String foods_id = foods_list. get(j).getString("foods_id");
				cpd.put("foods_id", foods_id);
				List<PageData> spec_class_list = (List<PageData>)dao.findForList("StoreMapper.getSpecClass", cpd);
				if(null == spec_class_list || spec_class_list.size() == 0){
					foods_list. get(j).put("has_spec", "0");
				} else {
					foods_list. get(j).put("has_spec", "1");
					//根据规格分类查询规格列表
					for (int k = 0; k < spec_class_list.size(); k++) {
						PageData spd = new PageData();
						String spec_class_id = spec_class_list.get(k).getString("spec_class_id");
						spd.put("spec_class_id", spec_class_id);
						List<PageData> spec_list = (List<PageData>)dao.findForList("StoreMapper.getSpecList", spd);
						spec_class_list.get(k).put("spec_list", spec_list);
					}
					foods_list.get(j).put("spec_class_list", spec_class_list);
					//如果有价格分类查询食物的默认价格
//					String price = (String) dao.findForObject("StoreMapper.getSpecDefaultPrice", cpd);
//					if(null == price){
//						price = (String) dao.findForObject("StoreMapper.getDefaultPrice", cpd);
//					}
					String price = (String) dao.findForObject("StoreMapper.getDefaultPrice", cpd);
					PageData pointPd = (PageData) dao.findForObject("StoreMapper.getPointCostPrice", cpd);
					String point = pointPd.get("point").toString();
					foods_list.get(j).put("price", price);
					foods_list.get(j).put("point", point);
				}
			}
			type_list.get(i).put("foods_list", foods_list);
		}
		result.put("type_list", type_list);
		return result;
	}
	
	
	/**
	 * 感觉商品规格查询相应的价格
	 */
	@Override
	public PageData getFoodsPrice(PageData pd) throws Exception {
		PageData result = new PageData();
		String price = (String)dao.findForObject("StoreMapper.getFoodsPrice", pd);
		if(null == price || "".equals(price)){
			price = (String) dao.findForObject("StoreMapper.getDefaultPrice", pd);
		}
		String point = ((PageData) dao.findForObject("StoreMapper.getPointCostPrice", pd)).get("point").toString();
		result.put("price", price);
		result.put("point", point);
		return result;
	}
	
	
	/**
	 * 查询商品详情
	 */
	@Override
	public PageData getFoodsDetail(PageData pd) throws Exception {
//		PageData result = new PageData();
		//商品
		PageData foods = (PageData)dao.findForObject("StoreMapper.finFoodsById", pd);
		String requestPath = pd.getString("requestPath");
		String foods_image_cn = requestPath + foods.getString("foods_image_cn");
		String foods_image_en = requestPath + foods.getString("foods_image_en");
		foods.put("foods_image_cn", foods_image_cn);
		foods.put("foods_image_en", foods_image_en);
		//商品规格分类+规格
		String foods_id = foods.getString("foods_id");
		PageData fpd = new PageData();
		fpd.put("foods_id", foods_id);
		List<PageData> spec_class_list = (List<PageData>)dao.findForList("StoreMapper.getSpecClass", fpd);
		if(null == spec_class_list || spec_class_list.size() == 0){
			foods.put("has_spec", "0");
		} else {
			foods.put("has_spec", "1");	
			for (int i = 0; i < spec_class_list.size(); i++) {
				String spec_class_id = spec_class_list.get(i).getString("spec_class_id");
				PageData spd = new PageData();
				spd.put("spec_class_id", spec_class_id);
				List<PageData> spec_list = (List<PageData>)dao.findForList("StoreMapper.getSpecList", spd);
				spec_class_list.get(i).put("spec_list", spec_list);
			}
		}
		foods.put("spec_class_list", spec_class_list);
		//商品详情（图文+图文。。。	。）
		List<PageData> foods_detail = (List<PageData>)dao.findForList("StoreMapper.getFoodsDetail", fpd);
		for(int i = 0; i < foods_detail.size(); i++) {
			foods_detail.get(i).put("content_image", requestPath+foods_detail.get(i).get("content_image"));
		}
		foods.put("foods_detail", foods_detail);
		
		//查询一些商家信息
		String store_id = foods.getString("store_id");
		PageData spd = new PageData();
		spd.put("store_id", store_id);
		PageData store = (PageData)dao.findForObject("StoreMapper.findOne", pd);
		PageData store_2 = new PageData();
		store_2.put("store_id", store.get("store_id"));
		store_2.put("store_name_cn", store.get("store_name_cn"));
		store_2.put("store_name_en", store.get("store_name_en"));
		store_2.put("store_image", requestPath+store.get("store_image"));
		foods.put("store", store_2);
		return foods;
	}

	
	/**
	 * 查询首页接口
	 */
	@Override
	public PageData getHomePage(PageData pd) throws Exception {
		PageData result = new PageData();
		String requestPath = pd.getString("requestPath");
		//头部banner
		//0:首页banner 1:手机欢迎页 2：手机引导页
		pd.put("type", "0");
		List<PageData> list = (List<PageData>)dao.findForList("BannerMapper.getBanner", pd);
		for (int i = 0; i < list.size(); i++) {
			String image = list.get(i).getString("image");
			list.get(i).put("image", requestPath + image);
		}
		//分类信息
		List<PageData> store_type_list = (List<PageData>)dao.findForList("StoreMapper.getStoreTypeList", pd);
		for (int i = 0; i < store_type_list.size(); i++) {
			String image = store_type_list.get(i).getString("type_image");
			store_type_list.get(i).put("type_image", requestPath + image);
		}
		result.put("banner_list", list);
		result.put("type_list", store_type_list);
		return result;
	}

	@Override
	public PageData getBanner(PageData pd) throws Exception {
		PageData result = new PageData();
		String requestPath = pd.getString("requestPath");
		//头部banner
		//0:首页banner 1:手机欢迎页 2：手机引导页
		List<PageData> list = (List<PageData>)dao.findForList("BannerMapper.getBanner", pd);
		for (int i = 0; i < list.size(); i++) {
			String image = list.get(i).getString("image");
			list.get(i).put("image", requestPath + image);
		}
		result.put("rows", list);
		return result;
	}

}
