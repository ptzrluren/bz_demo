package com.flc.service.basic.zipcode.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.basic.zipcode.ZipCodeManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** 
 * 说明： 配送范围管理
 * 创建人：FLC
 * 创建时间：2017-08-17
 * @version
 */
@Service("zipcodeService")
public class ZipCodeService implements ZipCodeManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("ZipCodeMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.update("ZipCodeMapper.updateOrderStateByZipcode", pd);
		dao.update("ZipCodeMapper.updateOrderErrandsStateByZipcode", pd);
		dao.delete("ZipCodeMapper.deleteDistributionTimeByZipcode", pd);
		dao.delete("ZipCodeMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("ZipCodeMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("ZipCodeMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ZipCodeMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("ZipCodeMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("ZipCodeMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**配送时间
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> distributionTime(PageData pd)throws Exception {
		return (List<PageData>)dao.findForList("ZipCodeMapper.distributionTime", pd);
	}
	
	/**新增配送时间
	 * @param pd
	 * @throws Exception
	 */
	public void saveDistributionTime(PageData pd)throws Exception{
		dao.save("ZipCodeMapper.saveDistributionTime", pd);
	}
	
	/**删除配送时间
	 * @param pd
	 * @throws Exception
	 */
	public void deleteDistributionTime(PageData pd)throws Exception{
		dao.delete("ZipCodeMapper.deleteDistributionTime", pd);
	}
}

