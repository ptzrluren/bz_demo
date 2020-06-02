package com.flc.service.user;

import com.flc.util.PageData;

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
	 * 更新密码
	 * @param pd
	 * @return
	 */
	public PageData updatePWD(PageData pd)throws Exception;
	
	/**
	 * 更新昵称
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData updateNick(PageData pd)throws Exception;
	
	/**
	 * 更新头像
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData updateHead(PageData pd)throws Exception;
	
	/**
	 * 忘记密码
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData forgotPassword(PageData pd)throws Exception;
	
	/**
	 * 极光推送-注册id关联配送员手机
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData relationPhone(PageData pd)throws Exception;
	
}

