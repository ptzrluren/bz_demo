package com.flc.service.foodinfo;

import com.flc.entity.Page;
import com.flc.util.PageData;

import java.util.List;

/** 
 * 说明： 商家商品详情接口
 * 创建人：FLC
 * 创建时间：2017-08-17
 * @version
 */
public interface FoodinfoManager{

	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception;
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception;
	
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
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception;
	
	/**
	 * 上下架商品
	 */
	public void updateShelves(PageData pd) throws Exception;
	
	public List<PageData> findFoodType(PageData pd) throws Exception;
}

