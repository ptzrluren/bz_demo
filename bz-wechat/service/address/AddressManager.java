package com.flc.service.address;

import com.flc.util.PageData;

import java.util.List;

public interface AddressManager {

	/**
	 * 查询有效的邮编
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getEffZipCode(PageData pd) throws Exception;

	/**
	 * 判断某邮编是否是在配送范围之内
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData isEffZipCode(PageData pd) throws Exception;
	
	/**
	 * 设置常用地址为默认地址
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public void setdefault(PageData pd) throws Exception;
	
	/**
	 * 根据收货ID判断邮编是否在配送范围之内
	 */
	public PageData isEffZipCodeByExtmId(PageData pd) throws Exception;
	
	/**
	 * 查询用户收货地址列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getAddressList(PageData pd) throws Exception;
	
	/**
	 * 新增收货地址
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData addAddress(PageData pd) throws Exception;
	
	
	/**
	 * 修改收货地址
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData editAddress(PageData pd) throws Exception;
	
	
	/**
	 * 删除收货地址
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData delAddress(PageData pd) throws Exception;
	
	/**
	 * 根据地址编号获取地址详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getAddressDetail(PageData pd) throws Exception;
	
	/**
	 * 根据用户编号获取默认地址详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getDefaultAddressDetailByUserId(PageData pd) throws Exception;
}
