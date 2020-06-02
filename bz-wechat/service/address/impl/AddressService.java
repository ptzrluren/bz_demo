package com.flc.service.address.impl;

import com.flc.dao.DaoSupport;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.address.AddressManager;
import com.flc.util.GoogleGeocoderUtil;
import com.flc.util.PageData;
import com.flc.util.UuidUtil;
import com.flc.util.beans.GoogleGeocodeJSONBean;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("addressService")
public class AddressService implements AddressManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * 查询配送范围之内的邮编列表
	 */
	@Override
	public PageData getEffZipCode(PageData pd) throws Exception {
		PageData result = new PageData();
		List<PageData> list = (List<PageData>)dao.findForList("AddressMapper.getEffZipCode", pd);
		result.put("list", list);
		return result;
	}

	/**
	 * 判断邮编是否在配送范围之内
	 */
	@Override
	public PageData isEffZipCode(PageData pd) throws Exception {
		PageData result = new PageData();
		PageData code = (PageData)dao.findForObject("AddressMapper.getEffZipCodeByCode", pd);
		if(null == code){
			result.put("in_range", "0");
		} else {
			result.put("in_range", "1");
		}
		return result;
	}
	
	/**
	 * 根据收货ID判断邮编是否在配送范围之内
	 */
	@Override
	public PageData isEffZipCodeByExtmId(PageData pd) throws Exception {
		PageData code = (PageData)dao.findForObject("AddressMapper.isEffZipCodeByExtmId", pd);
		if(null == code){
			code = new PageData();
			code.put("in_range", "0");
		} else {
			code.put("in_range", "1");
		}
		return code;
	}

	/**
	 * 查询收货地址
	 */
	@Override
	public PageData getAddressList(PageData pd) throws Exception {
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
		
		List<PageData> list = (List<PageData>)dao.findForList("AddressMapper.getAddressList", pd);
		result.put("address_list", list);
		return result;
	}
	
	@Override
	public PageData getAddressDetail(PageData pd) throws Exception {
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
		
		
		List<PageData> list = (List<PageData>)dao.findForList("AddressMapper.getAddressDetail", pd);
		result.put("address_list", list);
		return result;
	}
	
	/**
	 * 新增收货地址
	 */
	@Override
	public PageData addAddress(PageData pd) throws Exception {
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
		
		String zip_code = pd.getString("zip_code");
		if(StringUtils.isEmpty(zip_code)){
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
		
		//修改其他地址为非默认
		dao.update("AddressMapper.setUndefault", pd);
		
		String address = pd.getString("address");
		//解析地址
		GoogleGeocoderUtil.getInstance().setType(1);
		GoogleGeocodeJSONBean bean = GoogleGeocoderUtil.getInstance().geocodeByAddress(address);
		String status = bean.getStatus();
		if(!"OK".equals(status)){
			throw new NjmsException(ExEnum.EX_INVALID_ADDRESS);//返回地址解析异常
		}
		//经纬度
		String lng = bean.results[0].geometry.getLocation().getLng();
		String lat = bean.results[0].geometry.getLocation().getLat();
		pd.put("lng", lng);
		pd.put("lat", lat);
		//新增地址 默认地址
		pd.put("is_default", "1");
		String extm_id = UuidUtil.get32UUID();
		pd.put("extm_id", extm_id);
		dao.save("AddressMapper.saveAddress", pd);
		result.put("extm", pd);
		return result;
	}

	/**
	 * 修改收货地址
	 */
	@Override
	public PageData editAddress(PageData pd) throws Exception {
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
		
		
		//修改其他地址为非默认
		dao.update("AddressMapper.setUndefault", pd);
		
		String address = pd.getString("address");
		//解析地址
		GoogleGeocoderUtil.getInstance().setType(1);
		GoogleGeocodeJSONBean bean = GoogleGeocoderUtil.getInstance().geocodeByAddress(address);
		String status = bean.getStatus();
		//无效的地址
		if(!"OK".equals(status)){
			throw new NjmsException(ExEnum.EX_INVALID_ADDRESS);
		}
		String lng = bean.results[0].geometry.getLocation().getLng();
		String lat = bean.results[0].geometry.getLocation().getLat();
		pd.put("lng", lng);
		pd.put("lat", lat);
		
		//修改的地址设置为 默认地址
		pd.put("is_default", "1");
		dao.update("AddressMapper.editAddress", pd);
		result.put("extm", pd);
		return result;
	}

	/**
	 * 删除地址
	 */
	@Override
	public PageData delAddress(PageData pd) throws Exception {
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
		
		
		dao.delete("AddressMapper.delAddress", pd);
		return result;
	}

	/**
	 * 设置地址为默认地址
	 */
	@Override
	public void setdefault(PageData pd) throws Exception {
		//将用户地址设置为非默认
		dao.update("AddressMapper.setUndefault", pd);
		PageData extm = (PageData)dao.findForObject("AddressMapper.getCommonlyUsedAddress", pd);
		dao.update("AddressMapper.setdefault", extm);
	}
	
	/**
	 * 根据用户编号获取默认地址详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<PageData> getDefaultAddressDetailByUserId(PageData pd) throws Exception {
		List<PageData> list = (List<PageData>)dao.findForList("AddressMapper.getDefaultAddress", pd);
		return list;
	}

}
