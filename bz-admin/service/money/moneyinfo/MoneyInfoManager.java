package com.flc.service.money.moneyinfo;

import com.flc.entity.Page;
import com.flc.util.PageData;

import java.util.List;

/** 
 * 说明： 资金流水接口
 * 创建人：FLC
 * 创建时间：2017-08-22
 * @version
 */
public interface MoneyInfoManager{

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
	
	/**统计金额
	 * @param pd
	 * @throws Exception
	 */
	public PageData sumMoney(PageData pd)throws Exception;
	
	/**统计金额2
	 * @param pd
	 * @throws Exception
	 */
	public PageData sumMoney2(PageData pd)throws Exception;
	
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
	
	public List<PageData> listAll4Excel(PageData pd)throws Exception;
	public List<PageData> getOrderStore4Excel(PageData pd)throws Exception;
	/**
	 * 根据订单不好查询订单消费详情
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getOrderDetailInfo(PageData pd) throws Exception;
}

