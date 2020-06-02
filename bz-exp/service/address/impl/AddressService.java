package com.flc.service.address.impl;

import com.flc.dao.DaoSupport;
import com.flc.service.address.AddressManager;
import com.flc.util.PageData;
import com.flc.util.UuidUtil;
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
	 * 查询收货地址
	 */
	@Override
	public PageData getAddressList(PageData pd) throws Exception {
		PageData result = new PageData();
		List<PageData> list = (List<PageData>)dao.findForList("AddressMapper.getAddressList", pd);
		result.put("address_list", list);
		return result;
	}
	
	@Override
	public PageData getAddressDetail(PageData pd) throws Exception {
		PageData result = new PageData();
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
		//修改其他地址为非默认
		dao.update("AddressMapper.setUndefault", pd);
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
		//修改其他地址为非默认
		dao.update("AddressMapper.setUndefault", pd);
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
		dao.delete("AddressMapper.delAddress", pd);
		return result;
	}

}
