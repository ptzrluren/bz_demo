package com.flc.service.user.impl;

import com.alibaba.druid.util.StringUtils;
import com.flc.dao.DaoSupport;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.user.UserManager;
import com.flc.util.MD5;
import com.flc.util.PageData;
import com.flc.util.SmsUtil;
import com.flc.util.UuidUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;

@Service("userService")
public class UserService implements UserManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/**
	 * 登录
	 */
	@Override
	public PageData login(PageData pd) throws Exception {
		PageData result = new PageData();
		String phone = pd.getString("phone");
		String pwd = pd.getString("pwd");
		pwd = MD5.md5(pwd);

		// 参数不全
		if ((StringUtils.isEmpty(phone)) || (StringUtils.isEmpty(pwd))) {
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}

		// 查询用户
		PageData userPd = new PageData();
		userPd.put("phone", phone);
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOneExp", pd);

		// 该用户不存在
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		}
		

		String password = user.getString("pwd");
		if (password.equals(pwd)) {
			//判断该用户是否通过审核
			//状态  0：未审核  1：后台审核通过    2：审核未通过  3：自动审核成功
			String state = user.getString("state");
			if(!"1".equals(state)&& !"3".equals(state)){
				throw new NjmsException(ExEnum.EX_EXP_AUDIT_FAILED);
			}
			
			PageData re = new PageData();
			String loginToken = UuidUtil.get32UUID();
			re.put("token", loginToken);
			re.put("exp_id", user.getString("exp_id"));

			// 保存token
			this.dao.update("UserMapper.updateLoginToken", re);

			String requestPath = pd.getString("requestPath");
			
			result.put("user_id", user.getString("exp_id"));
			result.put("token", loginToken);
			result.put("head_image",  requestPath + user.getString("exp_image"));
			result.put("nick_name", user.getString("nick_name"));
			result.put("onwork", user.getString("on_work"));
		}
		// 密码错误
		else {
			throw new NjmsException(ExEnum.EX_PWD_ERROR);
		}
		return result;
	}

	/**
	 * 获取验证码
	 */
	public PageData getCerCode(PageData pd) throws Exception {
		PageData result = new PageData();
		String phone = pd.getString("phone");

		if (StringUtils.isEmpty(phone)) {
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}

		// 废弃验证码
		this.dao.update("UserMapper.scrapCode", pd);

		String code = getCodeStr();
		pd.put("code", code);
		this.dao.save("UserMapper.saveCode", pd);
		String text = "Your verification code is  :  " + code;
		text = URLEncoder.encode(text, "UTF-8");
		String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
		String re = SmsUtil.SMS("", url);
		System.out.println(re);
		// String url = "http://sms.253.com/msg/";
		// String un = "N4387306";
		// String pw = "k41H8mxyW";
		// String msg = "【安维宝】您好，你的验证码是" + code;
		// String rd = "1";
		// String ex = null;

		// String returnString = HttpSender.batchSend(url, un, pw, phone, msg,
		// rd, ex);
		// System.out.println(returnString);
//		result.put("code", code);
		return result;
	}

	/**
	 * 用户注册
	 */
	public PageData userReg(PageData pd) throws Exception {
		PageData result = new PageData();
		String phone = pd.getString("phone");
		String pwd = pd.getString("pwd");
		pwd = MD5.md5(pwd);
		String code = pd.getString("code");

		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		// 用户不存在 注册
		if (user == null) {

			// 比对验证码
			String code_ext = (String) dao.findForObject("UserMapper.getCodeByPhone", pd);
			if (!code.equals(code_ext)) {
				throw new NjmsException(ExEnum.EX_CODE_ERROR);
			}
			PageData userPd = new PageData();
			userPd.put("user_id", UuidUtil.get32UUID());
			userPd.put("nick_name", phone);
			userPd.put("pwd", pwd);
			userPd.put("state", "0");
			userPd.put("phone", phone);
			
			this.dao.save("UserMapper.save", userPd);
			this.dao.update("UserMapper.scrapCode", pd);
		}
		// 用户已存在，提示直接登录
		else {
			String approver_state = user.get("state").toString();
			if ("0".equals(approver_state)) {
				throw new NjmsException(ExEnum.EX_PENDING_AUDIT);
			}

			if ("1".equals(approver_state)) {
				throw new NjmsException(ExEnum.EX_REGISTERED);
			}

		}

		return result;
	}

	/**
	 * 获取登录状态
	 * 
	 * @param user_id
	 * @param token
	 * @return 0，成功 1，未登录 2，user不存在 3，异常
	 */
	public int getLoginState(String user_id, String token) {
		PageData pd = new PageData();
		pd.put("user_id", user_id);
		try {
			PageData user = (PageData) dao.findForObject("UserMapper.findOne", pd);
			if (null == user) {
				return 2;
			}
			String token2 = user.getString("token");
			if (!token.equals(token2)) {
				return 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 3;
		}
		return 0;
	}

	private String getCodeStr() {
		StringBuffer sbf = new StringBuffer();
		for (int i = 0; i < 4; i++) {
			int a = (int) Math.floor(Math.random() * 10);
			sbf.append(a);
		}
		return sbf.toString();
	}

	/**
	 * 检查token
	 * @param pd
	 * @return
	 * @throws Exception
	 */
//	private Boolean checkToken(PageData pd) throws Exception{
//		String tokenName="token", useridName="user_id";
//		String _token = pd.getString(tokenName);
//		String _key   = pd.getString(useridName);
//		if(StringUtils.isEmpty(_token)||StringUtils.isEmpty(_key))return false;
//		
//		pd.put("_key", _key);
//		
//		try {
//			PageData res = (PageData)this.dao.findForObject("UserMapper.getToken", pd);
//			if(_token.equals(res.get("token"))){
//				return true;
//			}
//			return false;
//		} catch (Exception e) {
//			return false;
//		}
//	}
	
	/**
	 * 更新密码
	 */
	public PageData updatePWD(PageData pd) throws Exception {
		PageData result = new PageData();
		String user_id = pd.getString("user_id");
		String old_pwd = pd.getString("old_pwd");
		String new_pwd = pd.getString("new_pwd");
		/*
		 * 验证是否传入参数
		 */
		if (StringUtils.isEmpty(user_id) || StringUtils.isEmpty(old_pwd) || StringUtils.isEmpty(new_pwd)) {
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
		/**
		 * 验证token
		 */
//		if(!checkToken(pd)){
//			throw new NjmsException(ExEnum.EX_TOKEN_INVALID);
//		}
		/*
		 * 再次MD5加密
		 */
		old_pwd = MD5.md5(old_pwd);
		new_pwd = MD5.md5(new_pwd);
		pd.put("old_pwd", old_pwd);
		pd.put("new_pwd", new_pwd);
		// 验证原密码
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOneExp", pd);
		// 该用户不存在
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		}
		//原密码不对
		if(!old_pwd.equals(user.getString("pwd"))){
			throw new NjmsException(ExEnum.EX_PWD_ERROR);
		}
		//user.put("pwd", new_pwd);
		this.dao.update("UserMapper.updatePWD", pd);
		
		return result;
	}

	
	
	
	/**
	 * 更新昵称
	 */
	public PageData updateNick(PageData pd) throws Exception {
		PageData result = new PageData();
		String nick_name=pd.getString("nick_name");
		String user_id=pd.getString("user_id");
		//验证参数
		if(StringUtils.isEmpty(nick_name)||StringUtils.isEmpty(user_id)){
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
//		if(!checkToken(pd)){
//			//提示登陆失效
//			throw new NjmsException(ExEnum.EX_TOKEN_INVALID);
//		}
		String res = this.dao.update("UserMapper.updateNcik", pd).toString();
		String code="",code_en="";
		if("0".equals(res)){
			code="修改失败";
			code_en="Update Success";
		}else{
			code="修改成功";
			code_en="Update Error";
		}
		
		return result;
	}

	/**
	 * 更新头像
	 */
	@Override
	public PageData updateHead(PageData pd) throws Exception {
		/**
		 * 验证Token
		 */
//		if(!checkToken(pd)){
//			//提示登陆失效
//			throw new NjmsException(ExEnum.EX_TOKEN_INVALID);
//		}
		String code = pd.getString("code");
//		
//		// 比对验证码
//		String code_ext = (String) dao.findForObject("UserMapper.getCodeByPhone", pd);
//		if (!code.equals(code_ext)) {
//			throw new NjmsException(ExEnum.EX_CODE_ERROR);
//		}
		
		/**
		 * 验证参数
		 */
		String user_id = pd.getString("user_id");
		String head_image = pd.getString("head_image");
		
		if(StringUtils.isEmpty(user_id)||StringUtils.isEmpty(head_image)){
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
		String res = this.dao.update("UserMapper.updateHead", pd).toString();
		String code_en="";
		if("0".equals(res)){
			code="修改失败";
			code_en="Update Success";
		}else{
			code="修改成功";
			code_en="Update Error";
		}
		PageData result = new PageData();
		
		return result;
	}

	@Override
	public PageData forgotPassword(PageData pd) throws Exception {
		
		String phone = pd.getString("phone");
		String code = pd.getString("code");
		String new_pwd = pd.getString("new_pwd");
		new_pwd = MD5.md5(new_pwd);
		pd.put("new_pwd", new_pwd);
		
		if(StringUtils.isEmpty(phone)||StringUtils.isEmpty(code)||StringUtils.isEmpty(new_pwd)){
			throw new NjmsException(ExEnum.EX_SYSTEM_NULL);
		}
		
		/**
		 * 验证COde
		 */
		// 比对验证码
		String code_ext = (String) dao.findForObject("UserMapper.getCodeByPhone", pd);
		if (!code.equals(code_ext)) {
			throw new NjmsException(ExEnum.EX_CODE_ERROR);
		}
		
		this.dao.update("UserMapper.forgotPassword", pd);
		
		
		return new PageData();
	}

	/**
	 * 极光推送-注册id关联配送员手机
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData relationPhone(PageData pd) throws Exception {
		this.dao.update("UserMapper.relationPhone", pd);
		return new PageData();
	}
	
}
