package com.flc.service.user.impl;

import com.flc.dao.DaoSupport;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.user.UserManager;
import com.flc.util.MD5;
import com.flc.util.PageData;
import com.flc.util.SmsUtil;
import com.flc.util.UuidUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.List;

@Service("userService")
public class UserService implements UserManager {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * 登录
	 */
	@Override
	public PageData login(PageData pd) throws Exception {
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
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);
		// 该用户不存在
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else {
			String is_disable = user.getString("is_disable");
			//用户被禁用
			if("1".equals(is_disable)){
				throw new NjmsException(ExEnum.EX_IS_DISABLE);
			}
		}
		String password = user.getString("pwd");
		if (password.equals(pwd)) {
			PageData re = new PageData();
			String loginToken = UuidUtil.get32UUID();
			re.put("token", loginToken);
			re.put("user_id", user.getString("user_id"));
			// 保存token
			this.dao.update("UserMapper.updateLoginToken", re);
			String requestPath = pd.getString("requestPath");
			user.put("token", loginToken);
			user.put("head_image", requestPath + user.getString("head_image"));
		}
		// 密码错误
		else {
			throw new NjmsException(ExEnum.EX_PWD_ERROR);
		}
		return user;
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
//		String url = "http://sms.253.com/msg/";
//		String un = "N4387306";
//		String pw = "k41H8mxyW";
//		String msg = "【安维宝】您好，你的验证码是" + code;
//		String rd = "1";
//		String ex = null;

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
		
		//用户不存在 注册
		if (user == null) {
			//比对验证码
			String code_ext = (String)dao.findForObject("UserMapper.getCodeByPhone", pd);
			if(!code.equals(code_ext)){
				throw new NjmsException(ExEnum.EX_CODE_ERROR);
			}
			PageData userPd = new PageData();
			userPd.put("user_id", UuidUtil.get32UUID());
			userPd.put("phone", phone);
			userPd.put("pwd", pwd);
			userPd.put("nick_name", phone);
			userPd.put("state", "0");
			if(pd.get("invite") != null && StringUtils.isNotBlank(pd.getString("invite"))) {
				PageData invitePd = new PageData();
				invitePd.put("phone", pd.getString("invite"));
				PageData inviteUser = (PageData) this.dao.findForObject("UserMapper.findOne", invitePd);
				if(inviteUser == null) {
					//邀请人不存在
					throw new NjmsException(ExEnum.EX_NO_INVITE_USER_ERROR);
				}
				userPd.put("invite", pd.getString("invite"));
			}
			this.dao.save("UserMapper.save", userPd);
			
			//钱包内容:饭点,累计金额,兑换比例
			PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", userPd);
			//如果没有钱包,创建空的钱包
			if(null == wallet) {
				dao.save("WalletMapper.saveWallet", userPd);
			}
			
			this.dao.update("UserMapper.scrapCode", pd);
		}
		//用户已存在，提示直接登录
		else {
			throw new NjmsException(ExEnum.EX_REGISTERED);
		}

		return result;
	}
	
	
	/**
	 * 获取登录状态  
	 * @param user_id
	 * @param token
	 * @return  0，成功    1，未登录    2，user不存在   3，异常
	 */
	public int getLoginState(String user_id, String token){
		PageData pd = new PageData();
		pd.put("user_id", user_id);
		try {
			PageData user = (PageData) dao.findForObject("UserMapper.findOne", pd);
			if(null == user){
				return 2;
			}
			String token2 = user.getString("token");
			if(!token.equals(token2)){
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
	 * 忘记密码
	 */
	@Override
	public PageData forgetPwd(PageData pd) throws Exception {
		PageData result = new PageData();
		String phone = pd.getString("phone");
		String pwd = pd.getString("new_pwd");
		pwd = MD5.md5(pwd);
		String code = pd.getString("code");

		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//用户不存在 注册
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		}
		//用户已存在，提示直接登录
		else {
			//比对验证码
			String code_ext = (String)dao.findForObject("UserMapper.getCodeByPhone", pd);
			if(!code.equals(code_ext)){
				throw new NjmsException(ExEnum.EX_CODE_ERROR);
			}
			PageData userPd = new PageData();
			userPd.put("phone", phone);
			userPd.put("pwd", pwd);
			this.dao.update("UserMapper.forgetPwd", userPd);
			this.dao.update("UserMapper.scrapCode", pd);
		}

		return result;
	}

	@Override
	public PageData updatePwd(PageData pd) throws Exception {
		PageData result = new PageData();
		String phone = pd.getString("phone");
		String old_pwd = pd.getString("old_pwd");
		String new_pwd = pd.getString("new_pwd");
		new_pwd = MD5.md5(new_pwd);
		old_pwd = MD5.md5(old_pwd);
		String code = pd.getString("code");

		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);
		String token = pd.getString("token");
		//用户不存在 注册
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		else {
			if(!old_pwd.equals(user.getString("pwd"))){
				throw new NjmsException(ExEnum.EX_OLD_PWD_WRONG);
			}
			PageData userPd = new PageData();
			userPd.put("phone", phone);
			userPd.put("pwd", new_pwd);
			this.dao.update("UserMapper.forgetPwd", userPd);
			this.dao.update("UserMapper.scrapCode", pd);
		}

		return result;
	}

	
	/**
	 * 修改头像
	 */
	@Override
	public PageData updateHead(PageData pd) throws Exception {
		PageData result = new PageData();
		
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		
		dao.update("UserMapper.updateHead", pd);
		return result;
	}

	@Override
	public PageData updateNickName(PageData pd) throws Exception {
		PageData result = new PageData();
		
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		
		dao.update("UserMapper.updateNickName", pd);
		return result;
	}
	
	
	
	
	
	@Override
	public List<PageData>  getCardList(PageData pd) throws Exception {
		List<PageData> list = (List<PageData>)dao.findForList("UserMapper.getCardList", pd);
		//客户端需要卡号，*号去除，由客户端打*
//		for (int i = 0; i < list.size(); i++) {
//			String card_no = list.get(i).getString("card_no");
//			if(card_no.length() >= 4){
//				String hou4 = card_no.substring(card_no.length() - 4);
//				
//				int len = card_no.length() - 4;
//				StringBuffer sbf = new StringBuffer();
//				for (int j = 0; j < len; j++) {
//					sbf.append("*");
//				}
//				sbf.append(hou4);
//				list.get(i).put("card_no", sbf.toString());
//			}
//		}
		return list;
	}
	


	@Override
	public void delCard(PageData pd) throws Exception {
		
		String token = pd.getString("token");
		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		dao.delete("UserMapper.delCard", pd);
	}


	@Override
	public void saveCard(PageData pd) throws Exception {
		PageData card = (PageData)dao.findForObject("UserMapper.getCardByNo", pd);
		if(card == null){
			dao.save("UserMapper.saveCard", pd);
		} else {
			throw new NjmsException(ExEnum.EX_CARD_ALREADY_EXISTS);
		}
		
	}


	@Override
	public PageData getCardByNo(PageData pd) throws Exception {
		return (PageData)dao.findForObject("UserMapper.getCardByNo", pd);
	}

	@Override
	public List<PageData> getExpList(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("UserMapper.getExpList", pd);
	}
	
	@Override
	public List<PageData> getExpiration(PageData pd) throws Exception {
		return (List<PageData>)dao.findForList("UserMapper.getExpiration", pd);
	}
	public static void main(String[] args) {
		System.out.println(MD5.md5("111111"));
	}
}
