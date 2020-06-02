package com.flc.service.user;

import com.flc.exception.NjmsException;
import com.flc.util.PageData;

import java.util.List;

/** 
 * 说明： Banner接口
 * 创建人：FLC
 * 创建时间：2017-03-07
 * @version
 */
public interface UserManager{


	/**
	 * 登录
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData login(PageData pd) throws Exception;
	/**
	 * 获取验证码
	 * @param pd
	 * @return
	 * @throws NjmsException
	 */
	public PageData getCerCode(PageData pd)throws Exception;
	
	/**
	 * 注册
	 * @param pd
	 * @return
	 * @throws NjmsException
	 */
	public PageData userReg(PageData pd)throws Exception;
	
	/**
	 * 获取用户登录状态
	 * @param user_id
	 * @param token
	 * @return
	 */
	public int getLoginState(String user_id, String token);
	
	/**
	 * 忘记密码
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData forgetPwd(PageData pd)throws Exception;
	public PageData updatePwd(PageData pd)throws Exception;  //修改密码
	public PageData updateHead(PageData pd)throws Exception;  //修改头像
	public PageData updateNickName(PageData pd)throws Exception;  //修改头像
	
	
	
	/**
	 * 查询用户有效的银行卡列表
	 * @param pd
	 * @return
	 * @throws NjmsException
	 */
	public List<PageData> getCardList(PageData pd) throws Exception;	
	
	/**
	 * 删除
	 * @param pd
	 * @throws Exception
	 */
	public void delCard(PageData pd) throws Exception;	
	
	
	/**
	 * 新增
	 * @param pd
	 * @throws Exception
	 */
	public void saveCard(PageData pd) throws Exception;	
	
	
	/**
	 * 根据卡号查询卡信息
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getCardByNo(PageData pd) throws Exception;	
	
	public List<PageData> getExpList(PageData pd) throws Exception;	
	
	/**
	 * 获取所有有效期接口
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public List<PageData> getExpiration(PageData pd) throws Exception;	

}

