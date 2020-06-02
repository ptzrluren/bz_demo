package com.flc.service.foodinfo.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.foodinfo.FoodtypeManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/** 
 * 说明： 商品规格类型
 * 创建人：FLC
 * 创建时间：2017-08-18
 * @version
 */
@Service("foodtypeService")
public class FoodtypeService implements FoodtypeManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("FoodtypeMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("FoodtypeMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("FoodtypeMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("FoodtypeMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("FoodtypeMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("FoodtypeMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("FoodtypeMapper.deleteAll", ArrayDATA_IDS);
	}

	@Override
	public Double getFoodsSpecPrice(PageData pd) throws Exception {
		//如果该规格没有价格，则查询商品价格
		Double price = (Double)dao.findForObject("FoodtypeMapper.getFoodsSpecPrice", pd);
		if(null == price){
			price = (Double)dao.findForObject("FoodtypeMapper.getFoodsDefaultPrice", pd);
		}
		return price;
	}

	@Override
	public void delSpecPrice(PageData pd) throws Exception {
		dao.delete("FoodtypeMapper.delSpecPrice", pd);
	}

	@Override
	public void savePriceList(List<Map<String, Object>> list) throws Exception {
		dao.batchSave("FoodtypeMapper.savePriceList", list);
	}
	
}

