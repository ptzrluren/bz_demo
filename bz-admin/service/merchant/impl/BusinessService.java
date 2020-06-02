package com.flc.service.merchant.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.merchant.BusinessManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/** 
 * 说明： 商家管理-增删改查，上下架
 * 创建人：FLC
 * 创建时间：2017-08-16
 * @version
 */
@Service("businessService")
public class BusinessService implements BusinessManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("BusinessMapper.save", pd);
	}
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void saveUserStore(PageData pd)throws Exception{
		dao.save("BusinessMapper.saveUserStore", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("BusinessMapper.delete", pd);
	}
	
	/**删除关联表
	 * @param pd
	 * @throws Exception
	 */
	public void deleteUserStore(PageData pd)throws Exception{
		dao.delete("BusinessMapper.deleteUserStore", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("BusinessMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		List<PageData> list =  (List<PageData>)dao.findForList("BusinessMapper.datalistPage", page);
		return list;
	}
	
	/**通过USERID获取数据STOREID
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> findStoreByUserId(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BusinessMapper.findStoreByUserId", pd);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BusinessMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("BusinessMapper.findById", pd);
	}
	
	/**通过id获取流水综合
	 * @param pd
	 * @throws Exception
	 */
	public PageData sumMoneyById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("BusinessMapper.sumMoneyById", pd);
	}
	
	/**通过name获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findByName(PageData pd)throws Exception{
		return (PageData)dao.findForObject("BusinessMapper.findByName", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("BusinessMapper.deleteAll", ArrayDATA_IDS);
	}
	
	/**
	 * 上架，下架上架
	 * @throws Exception 
	 * 
	 */
	public void updateShelves(PageData pd) throws Exception{
		dao.update("BusinessMapper.updateShelves", pd);
	}

	@Override
	public List<PageData> findStrotType(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("BusinessMapper.findStrotType", pd);
	}
	
	
	@Override
	public List<PageData> getStoreIncome(PageData pd) throws Exception {
		
		return (List<PageData>)dao.findForList("BusinessMapper.datalistPageStoreIncome", pd);
	}

	@Override
	public List<PageData> storeBusniesslist(Page page) throws Exception {
		List<PageData> list = (List<PageData>)dao.findForList("BusinessMapper.datalistPageStoreBusniesslist", page);
		DecimalFormat df = new DecimalFormat("0.00");
		for (int i = 0; i < list.size(); i++) {
			//查询订单里面该商家的菜品
			String order_id = list.get(i).getString("order_id");
			String store_id = list.get(i).getString("store_id");
			PageData pd = new PageData();
			pd.put("store_id", store_id);
			pd.put("order_id", order_id);
			List<PageData> list_foods = (List<PageData>)dao.findForList("BusinessMapper.getOrderFoodsList", pd);
			double money = 0;
			double point_money = 0;
			for (int j = 0; j < list_foods.size(); j++) {
				BigDecimal quantity = new BigDecimal((Integer)list_foods.get(j).get("quantity"));	
				BigDecimal price = (BigDecimal)list_foods.get(j).get("cost_price");	
				if(price != null) {		
					BigDecimal m = price.multiply(quantity);
					money += m.doubleValue();
				}
				
				BigDecimal point_price = (BigDecimal)list_foods.get(j).get("point_price");
				if(point_price != null) {
					BigDecimal pm = point_price.multiply(quantity);
					point_money += pm.doubleValue();
				}
			}
			list.get(i).put("list_foods", list_foods);
			list.get(i).put("money", df.format(money));
			list.get(i).put("point_money", df.format(point_money));
		}
		return list;
	}

	@Override
	public List<PageData> getCaidan(PageData pd) throws Exception {
		List<PageData> list_foods = (List<PageData>)dao.findForList("BusinessMapper.getOrderFoodsList", pd);
		return list_foods;
	}

	@Override
	public void exportExcel(List<PageData> list) throws Exception {
		dao.batchSave("BusinessMapper.saveExport", list);
	}
	
}

