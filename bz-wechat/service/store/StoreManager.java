package com.flc.service.store;

import com.flc.util.PageData;

/** 
 * 说明： Banner接口
 * 创建人：FLC
 * 创建时间：2017-03-07
 * @version
 */
public interface StoreManager{

	public PageData getStoreList(PageData pd) throws Exception; 									//查询商家列表
	public PageData getCollectionStoreList(PageData pd) throws Exception;					//查询收藏商家列表
	public PageData collectStore(PageData pd) throws Exception;									//收藏/取消收藏商家
	public PageData getStoreDetail(PageData pd) throws Exception;								//查询商家详情
	public PageData getStoreFoodsList(PageData pd) throws Exception;						//商家商品
	public PageData getFoodsPrice(PageData pd) throws Exception;								//根据规格查询商品价格
	public PageData getFoodsDetail(PageData pd) throws Exception;								//查询商品详情
	public PageData getHomePage(PageData pd) throws Exception;								//查询首页接口
	public PageData getBanner(PageData pd) throws Exception;										//查询banner图 引导页 欢迎页
}

