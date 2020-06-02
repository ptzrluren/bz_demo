package com.flc.service.store.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.store.StoreManager;
import com.flc.service.user.UserManager;
import com.flc.util.PageData;
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
		
		List<PageData> storeList = (List<PageData>)dao.findForList("StoreMapper.datalistPage", pg);
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
		
		List<PageData> storeList = (List<PageData>)dao.findForList("StoreMapper.datalistPageCollectionStore", pg);
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
		String user_id = pd.getString("user_id");
		String token = pd.getString("token");
		int re = userService.getLoginState(user_id, token);
		//未登录
		if(re != 0){
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
		//查询是否收藏了该店
		PageData store_collection = (PageData)dao.findForObject("StoreMapper.getCollectionStore", pd);
		if(null == store_collection){
			store.put("has_collect", "0");
		} else {
			store.put("has_collect", "1");
		}
		return store;
	}

	/**
	 * 查询商家商品
	 */
	@Override
	public PageData getStoreFoodsList(PageData pd) throws Exception {
		PageData result = new PageData();
//		String store_id = pd.getString("store_id");
//		//查询商家商品的分类
//		List<PageData> type_list = (List<PageData>)dao.findForList("StoreMapper.getTypeList", pd);
//		//查询商品的list
//		for (int i = 0; i < type_list.size(); i++) {
//			PageData fpd = new PageData();
//			fpd.put("store_id", store_id);
//			String type_id = type_list.get(i).getString("type_id");
//			fpd.put("type_id", type_id);
//			//食物列表
//			List<PageData> foods_list = (List<PageData>)dao.findForList("StoreMapper.getFoodsByType", fpd);
//			//查询食物下面的规格分类
//			for (int j = 0; j < foods_list.size(); j++) {
//				PageData cpd = new PageData();
//				String foods_id = foods_list. get(j).getString("foods_id");
//				cpd.put("foods_id", foods_id);
//				List<PageData> spec_class_list = (List<PageData>)dao.findForList("StoreMapper.getSpecClass", cpd);
//				if(null == spec_class_list || spec_class_list.size() == 0){
//					foods_list. get(j).put("has_spec", "0");
//				} else {
//					foods_list. get(j).put("has_spec", "1");
//					//根据规格分类查询规格列表
//					for (int k = 0; k < spec_class_list.size(); k++) {
//						PageData spd = new PageData();
//						String spec_class_id = spec_class_list.get(k).getString("spec_class_id");
//						spd.put("spec_class_id", spec_class_id);
//						List<PageData> spec_list = (List<PageData>)dao.findForList("StoreMapper.getSpecList", spd);
//						spec_class_list.get(k).put("spec_list", spec_list);
//					}
//					foods_list.get(j).put("spec_class_list", spec_class_list);
//					//如果有价格分类查询食物的默认价格
//					String price = (String) dao.findForObject("StoreMapper.getSpecDefaultPrice", cpd);
//					if(null == price){
//						price = (String) dao.findForObject("StoreMapper.getDefaultPrice", cpd);
//					}
//					foods_list.get(j).put("price", price);
//				}
//			}
//			type_list.get(i).put("foods_list", foods_list);
//		}
//		result.put("type_list", type_list);
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
		result.put("price", price);
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
		store_2.put("store_image", store.get("store_image"));
		foods.put("store", store_2);
		return foods;
	}

	
	/**
	 * 查询首页接口
	 */
	@Override
	public PageData getHomePage(PageData pd) throws Exception {
		PageData result = new PageData();
		//头部banner
		pd.put("type", "0");
		List<PageData> list = (List<PageData>)dao.findForList("BannerMapper.getBanner", pd);
		//分类信息
		List<PageData> store_type_list = (List<PageData>)dao.findForList("StoreMapper.getStoreTypeList", pd);
		result.put("banner_list", list);
		result.put("type_list", store_type_list);
		return result;
	}

}
