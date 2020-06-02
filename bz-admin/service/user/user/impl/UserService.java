package com.flc.service.user.user.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.user.user.UserManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** 
 * 说明： 4.用户管理
 * 创建人：FLC
 * 创建时间：2017-08-18
 * @version
 */
@Service("userNormalService")
public class UserService implements UserManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd)throws Exception{
		dao.save("UsersMapper.save", pd);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("UsersMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("UsersMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> list(Page page)throws Exception{
		return (List<PageData>)dao.findForList("UsersMapper.datalistPage", page);
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("UsersMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UsersMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("UsersMapper.deleteAll", ArrayDATA_IDS);
	}

	@Override
	public void disableUser(PageData pd) throws Exception {
		dao.update("UsersMapper.disableUser", pd);
	}

	@Override
	public void enableUser(PageData pd) throws Exception {
		dao.update("UsersMapper.enableUser", pd);
	}

	@Override
	public PageData findWallet(PageData pd) throws Exception {
		return (PageData)dao.findForObject("UsersMapper.findWallet", pd);
	}

	@Override
	public void saveFandian(PageData pd) throws Exception {
		dao.update("UsersMapper.saveFandian", pd);
	}
	
}

