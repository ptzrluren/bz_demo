package com.flc.service.limitmoney.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.limitmoney.LimitMoneyManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** 
 * 说明： 订单最低金额
 * 创建人：FLC
 * 创建时间：2017-12-07
 * @version
 */
@Service("limitmoneyService")
public class LimitMoneyService implements LimitMoneyManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("LimitMoneyMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("LimitMoneyMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("LimitMoneyMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("LimitMoneyMapper.findById", pd);
	}
	
}

