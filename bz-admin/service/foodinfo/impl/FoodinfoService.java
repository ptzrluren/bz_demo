package com.flc.service.foodinfo.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.foodinfo.FoodinfoManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** 
 * 说明： 商家商品详情
 * 创建人：FLC
 * 创建时间：2017-08-17
 * @version
 */
@Service("foodinfoService")
public class FoodinfoService implements FoodinfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FoodinfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FoodinfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FoodinfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FoodinfoMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FoodinfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FoodinfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FoodinfoMapper.deleteAll", ArrayDATA_IDS);
	}
	/**
	 * 上下架商品
	 */
	public void updateShelves(PageData pd) throws Exception{
		dao.update("FoodinfoMapper.updateShelves", pd);
	}

	@Override
	public List<PageData> findFoodType(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("FoodinfoMapper.findFoodType", pd);
	}
	
}

