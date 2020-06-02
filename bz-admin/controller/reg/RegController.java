package com.flc.controller.reg;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.flc.controller.base.BaseController;
import com.flc.service.merchant.BusinessManager;
import com.flc.service.system.user.UserManager;
import com.flc.util.AppUtil;
import com.flc.util.Const;
import com.flc.util.Jurisdiction;
import com.flc.util.PageData;
import com.flc.util.SmsUtil;
import com.flc.util.Tools;

/** 
 * 说明：
 * 创建人：FLC
 * 创建时间：2017-04-20
 */
@Controller
@RequestMapping(value="/reg")
public class RegController extends BaseController {
	
	@Resource(name="userService")
	private UserManager userService;
	@Resource(name = "businessService")
	private BusinessManager businessService;
	
	/**进入注册页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/index")
	public ModelAndView index() throws Exception{
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("reg/reg");
		return mv;
	}
	
	/**
	 * 获取短信验证码
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/getyzm")
	@ResponseBody
	public Object getyzm() throws Exception {
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		String phone = pd.getString("USERNAME");
		if(StringUtils.isNotBlank(phone) && phone.length() == 10) {
			String yzm = Tools.getRandomNum(4);
			String text = "Your verification code is:"+yzm;
			text = URLEncoder.encode(text, "UTF-8");
			String url = "http://api.paasoo.com/json?key=0cdinh&secret=VU6Bo2GD&from=baozou&to=1" + phone + "&text=" + text;
			String re = SmsUtil.SMS("", url);
			System.out.println(url);
			System.out.println(re);
			this.getRequest().getSession().setAttribute("yzm", yzm);
			map.put("status", "success");
			map.put("msg", "发送成功");
		}else{
			map.put("status", "error");
			map.put("msg", "手机号为空或位数不正确");
		}
		return AppUtil.returnObject(pd, map);
	}
	
	/**注册
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/reg")
	public ModelAndView reg() throws Exception{
		logBefore(logger, "reg新增user");
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		//判断短信验证码
		if(!pd.getString("yzm").equals(this.getRequest().getSession().getAttribute("yzm"))) {
			mv.addObject("msg","注册失败，短信验证码错误");
			mv.setViewName("msg");
			return mv;
		}
		
		pd.put("USER_ID", this.get32UUID());	//ID 主键
		pd.put("LAST_LOGIN", "");				//最后登录时间
		pd.put("IP", "");						//IP
		pd.put("STATUS", "1");					//待审核状态
		pd.put("SKIN", "default");
		pd.put("RIGHTS", Const.STROE_ADMIN_RIGHTS);
		pd.put("ROLE_ID", Const.STROE_ADMIN_ROLE_ID);
		pd.put("PASSWORD", new SimpleHash("SHA-1", pd.getString("USERNAME"), pd.getString("PASSWORD")).toString());	//密码加密
		
		PageData busiPd = new PageData();
		busiPd.put("STORE_NAME_CN", pd.getString("NAME"));
		if(null != businessService.findByName(busiPd)) {
			mv.addObject("msg","注册失败，存在相同的商户名称");
			mv.setViewName("msg");
			return mv;
		}
		if(null == userService.findByUsername(pd)){	//判断用户名是否存在
			userService.saveU(pd); 					//执行保存
			
			//注册商户
			PageData pdStore = new PageData();
			pdStore.put("STORE_ID", this.get32UUID());
			pdStore.put("STORE_NAME_CN", pd.getString("NAME"));
			pdStore.put("UP_FRAME", "0");
			businessService.save(pdStore);
			
			//绑定sys_user与store关联
			PageData saveUserStore = new PageData();
			saveUserStore.put("STOREID", pdStore.getString("STORE_ID"));
			saveUserStore.put("USERID", pd.getString("USER_ID"));
			businessService.saveUserStore(saveUserStore);
			
			mv.addObject("msg","注册成功，请等待审核");
		}else{
			mv.addObject("msg","注册失败，存在相同的手机号");
		}
		mv.setViewName("msg");
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
