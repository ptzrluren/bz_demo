package com.flc.service.merchant;

import com.flc.entity.Page;
import com.flc.util.PageData;

import java.util.List;

/** 
 * 说明： 商家管理-增删改查，上下架接口
 * 创建人：FLC
 * 创建时间：2017-08-16
 * @version
 */
public interface BusinessManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void saveUserStore(PageData pd)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception;
	
	/**删除关联表
	 * @param pd
	 * @throws Exception
	 */
	public void deleteUserStore(PageData pd)throws Exception;
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> list(Page page)throws Exception;
	
	/**通过USERID获取数据STOREID
	 * @param page
	 * @throws Exception
	 */
	public List<PageData> findStoreByUserId(PageData pd)throws Exception;
	
	/**
	 * 商家资金流水列表
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public List<PageData> storeBusniesslist(Page page)throws Exception;
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	public List<PageData> listAll(PageData pd)throws Exception;
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception;
	
	/**通过id获取流水综合
	 * @param pd
	 * @throws Exception
	 */
	public PageData sumMoneyById(PageData pd)throws Exception;
	
	/**通过name获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByName(PageData pd)throws Exception;
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
	/**
	 * 修改商家状态   实现上下架
	 * @param pd
	 * @throws Exception
	 */
	public void updateShelves(PageData pd)throws Exception;
	
	/**
	 * 获取所有商家类型
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> findStrotType(PageData pd) throws Exception;
	
	/**
	 * 查询商家的资金流水
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getStoreIncome(PageData pd) throws Exception;
	
	/**
	 * 查看商家流水的菜单
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getCaidan(PageData pd) throws Exception;
	
	public void exportExcel(List<PageData> list)throws Exception;
	
}

