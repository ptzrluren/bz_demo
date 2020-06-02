package com.flc.service.money.moneyinfo.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.money.moneyinfo.MoneyInfoManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** 
 * 说明： 资金流水
 * 创建人：FLC
 * 创建时间：2017-08-22
 * @version
 */
@Service("moneyinfoService")
public class MoneyInfoService implements MoneyInfoManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("MoneyInfoMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.update("MoneyInfoMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("MoneyInfoMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		List<PageData> list = (List<PageData>)dao.findForList("MoneyInfoMapper.datalistPage", page);
		for (int i = 0; i < list.size(); i++) {
			String order_id = list.get(i).getString("ORDER_ID");
			PageData pd = new PageData();
			pd.put("order_id", order_id);
			List<PageData> store_list = (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderStoreList", pd);
			for (int j = 0; j < store_list.size(); j++) {
				String store_id = store_list.get(j).getString("store_id");
				PageData ppd = new PageData();
				ppd.put("order_id", order_id);
				ppd.put("store_id", store_id);
				List<PageData> foods_list = (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderFoodsList", ppd);
				store_list.get(j).put("foods_list", foods_list);
			}
			list.get(i).put("store_list", store_list);
		}
		return list;
	}
	
	/**统计金额
	 * @param pd
	 * @throws Exception
	 */
	public PageData sumMoney(PageData pd)throws Exception{
		return (PageData)dao.findForObject("MoneyInfoMapper.sumMoney", pd);
	}
	
	/**统计金额2
	 * @param pd
	 * @throws Exception
	 */
	public PageData sumMoney2(PageData pd)throws Exception{
		return (PageData)dao.findForObject("MoneyInfoMapper.sumMoney2", pd);
	}
	
	/**
	 * 根据订单编号查询订单的消费详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getOrderDetailInfo(PageData pd) throws Exception{
		String order_id = pd.getString("order_id");
//		PageData pd = new PageData();
//		pd.put("order_id", order_id);
		List<PageData> store_list = (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderStoreList", pd);
		for (int j = 0; j < store_list.size(); j++) {
			String store_id = store_list.get(j).getString("store_id");
			PageData ppd = new PageData();
			ppd.put("order_id", order_id);
			ppd.put("store_id", store_id);
			List<PageData> foods_list = (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderFoodsList", ppd);
			if(null != foods_list){
				store_list.get(j).put("foods_list", foods_list);
				store_list.get(j).put("foods_count", foods_list.size());
			}
		}
		return store_list;
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("MoneyInfoMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("MoneyInfoMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("MoneyInfoMapper.deleteAll", ArrayDATA_IDS);
	}

	@Override
	public List<PageData> listAll4Excel(PageData pd) throws Exception {
		List<PageData> list = (List<PageData>)dao.findForList("MoneyInfoMapper.listAll4Excel", pd);
//		for (int i = 0; i < list.size(); i++) {
//			String order_id = list.get(i).getString("order_id");
//			PageData opd = new PageData();
//			opd.put("order_id", order_id);
//			List<PageData> store_list = (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderStoreList", opd);
//			for (int j = 0; j < store_list.size(); j++) {
//				String store_id = store_list.get(j).getString("store_id");
//				PageData ppd = new PageData();
//				ppd.put("order_id", order_id);
//				ppd.put("store_id", store_id);
//				List<PageData> foods_list = (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderFoodsList", ppd);
//				store_list.get(j).put("foods_list", foods_list);
//			}
//			list.get(i).put("store_list", store_list);
//		}
		return list;
	}

	@Override
	public List<PageData> getOrderStore4Excel(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("MoneyInfoMapper.getOrderStore4Excel", pd);
	}
	
}

