package com.flc.controller.app.store;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.store.StoreManager;
import com.flc.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/app/store")
public class StoreController extends BaseController {
	
	
	@Autowired
	private StoreManager storeService;
	
	
	
	/**
	 * 查询首页接口
	 * @return
	 */
	@RequestMapping("getHomePage")
	@ResponseBody
	public Object getHomePage(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.getHomePage(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	
	/**
	 * 查询商家列表
	 * @return
	 */
	@RequestMapping("getStoreList")
	@ResponseBody
	public Object getStoreList(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.getStoreList(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	
	/**
	 * 查询收藏的商家列表
	 * @return
	 */
	@RequestMapping("getCollectionStoreList")
	@ResponseBody
	public Object getCollectionStoreList(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.getCollectionStoreList(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	/**
	 * 收藏/取消收藏商家
	 * @return
	 */
	@RequestMapping("collectStore")
	@ResponseBody
	public Object collectStore(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.collectStore(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	/**
	 * 查询商家详情
	 * @return
	 */
	@RequestMapping("getStoreDetail")
	@ResponseBody
	public Object getStoreDetail(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.getStoreDetail(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
//	/**
//	 * 查询商家的食物列表
//	 * @return
//	 */
//	@RequestMapping("getStoreFoodsList")
//	@ResponseBody
//	public Object getStoreFoodsList(){
//		PageData pd = null;
//		pd = this.getPageData();
//		PageData result = new PageData();
//		try {
//			result = storeService.getStoreFoodsList(pd);
//		} catch (NjmsException e){
//			e.printStackTrace();
//			return getResultMap(e.getEx(), new PageData());
//		} catch (Exception e){
//			e.printStackTrace();
//			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
//		} 
//		
//		return getResultMap(ExEnum.EX_SUCCESS, result);
//	}
	
	
	/**
	 * 根据规格查询食物的价格
	 * @return
	 */
	@RequestMapping("getFoodsPrice")
	@ResponseBody
	public Object getFoodsPrice(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.getFoodsPrice(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 查询食物价格
	 * @return
	 */
	@RequestMapping("getFoodsDetail")
	@ResponseBody
	public Object getFoodsDetail(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = storeService.getFoodsDetail(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
}
