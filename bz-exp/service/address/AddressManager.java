package com.flc.service.address;

import com.flc.util.PageData;

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
	
	
	public PageData getAddressDetail(PageData pd) throws Exception;
}
