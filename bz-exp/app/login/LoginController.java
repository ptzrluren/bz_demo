package com.flc.controller.app.login;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.user.UserManager;
import com.flc.util.PageData;

@Controller
@RequestMapping(value="/app/expLogin")
public class LoginController extends BaseController {
	
	
	@Autowired
	private UserManager userService;
	@Value("${upload.requestPath}")
	private String requestPath;
	/**
	 * 登录
	 * @return
	 */
	@RequestMapping("login")
	@ResponseBody
	public Object login(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			pd.put("requestPath", requestPath);
			result = userService.login(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 注册
	 * @return
	 */
	@RequestMapping("expReg")
	@ResponseBody
	public Object userReg(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.userReg(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	/**
	 * 获取验证码
	 * @return
	 */
	@RequestMapping("getCerCode")
	@ResponseBody
	public Object getCerCode(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.getCerCode(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 更新密码
	 * @return
	 */
	@RequestMapping("updatePwd")
	@ResponseBody
	public Object updatePWD(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.updatePWD(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 更新nick
	 * @return
	 */
	@RequestMapping("updateNickName")
	@ResponseBody
	public Object updateNick(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.updateNick(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 修改头像
	 * @return
	 */
	@RequestMapping("updateHead")
	@ResponseBody
	public Object updateHead(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.updateHead(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 *  忘记密码
	 * @return
	 */
	@RequestMapping("forgetPwd")
	@ResponseBody
	public Object forgotPassword(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.forgotPassword(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
//	@RequestMapping("forgetPwd")
//	@ResponseBody
//	public Object forgetPwd(){
//		PageData pd = null;
//		pd = this.getPageData();
//		PageData result = new PageData();
//		try {
//			result = userService.forgetPwd(pd);
//		} catch (NjmsException e){
//			e.printStackTrace();
//			return getResultMap(e.getEx(), new PageData());
//		} catch (Exception e){
//			e.printStackTrace();
//			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
//		} 
//		
//		return getResultMap(ExEnum.EX_SUCCESS, result);
//	}
//	@RequestMapping("forgetPwd")
//	@ResponseBody
//	public Object forgetPwd(){
//		PageData pd = null;
//		pd = this.getPageData();
//		PageData result = new PageData();
//		try {
//			result = userService.forgetPwd(pd);
//		} catch (NjmsException e){
//			e.printStackTrace();
//			return getResultMap(e.getEx(), new PageData());
//		} catch (Exception e){
//			e.printStackTrace();
//			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
//		} 
//		
//		return getResultMap(ExEnum.EX_SUCCESS, result);
//	}
	
	/**
	 * 极光推送-注册id关联配送员手机
	 * @return
	 */
	@RequestMapping("relationPhone")
	@ResponseBody
	public Object relationPhone(@RequestBody PageData pd){
		//判断入参是否为空
		if(pd.get("phone") == null || 
				StringUtils.isBlank(String.valueOf(pd.get("phone")))) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		String type = "0";		//类型：1关联0取消关联
		if(pd.get("type") == null || StringUtils.isBlank(String.valueOf(pd.get("type")))) {
			type = "1";			//类型默认1
		}else{
			type = pd.getString("type");
		}
		if(type.equals("1")) {
			//关联时，注册ID不能为空
			if(pd.get("registration_id") == null || StringUtils.isBlank(String.valueOf(pd.get("registration_id")))) {
				return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
			}
		}
		PageData result = new PageData();
		try {
			if(type.equals("1")) {
				//关联
				result = userService.relationPhone(pd);
			}else{
				//取消关联
				pd.put("registration_id", "");
				result = userService.relationPhone(pd);
			}
			
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
}
