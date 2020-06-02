package com.flc.controller.app.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.address.AddressManager;
import com.flc.service.store.StoreManager;
import com.flc.service.user.UserManager;
import com.flc.util.PageData;

@Controller
@RequestMapping(value="/app/address")
public class AddressController extends BaseController {
	
	
	@Autowired
	private AddressManager addressService;
	
	/**
	 * 查询配送范围的邮编
	 * @return
	 */
	@RequestMapping("getEffZipCode")
	@ResponseBody
	public Object getEffZipCode(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.getEffZipCode(pd);
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
	 * 判断邮编是否在配送范围之内
	 * @return
	 */
	@RequestMapping("isEffZipCode")
	@ResponseBody
	public Object isEffZipCode(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.isEffZipCode(pd);
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
	 * 查询用户收货地址列表
	 * @return
	 */
	@RequestMapping("getAddressDetail")
	@ResponseBody
	public Object getAddressDetail(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.getAddressDetail(pd);
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
	 * 查询用户收货地址列表
	 * @return
	 */
	@RequestMapping("getAddressList")
	@ResponseBody
	public Object getAddressList(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.getAddressList(pd);
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
	 * 用户新增收货地址
	 * @return
	 */
	@RequestMapping("addAddress")
	@ResponseBody
	public Object addAddress(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.addAddress(pd);
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
	 * 用户修改收货地址
	 * @return
	 */
	@RequestMapping("editAddress")
	@ResponseBody
	public Object editAddress(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.editAddress(pd);
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
	 * 用户删除收货地址
	 * @return
	 */
	@RequestMapping("delAddress")
	@ResponseBody
	public Object delAddress(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = addressService.delAddress(pd);
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
