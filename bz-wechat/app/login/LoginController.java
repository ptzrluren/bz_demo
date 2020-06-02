package com.flc.controller.app.login;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.user.UserManager;
import com.flc.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/app/login")
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
	@RequestMapping("userReg")
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
	 * 忘记密码
	 * @return
	 */
	@RequestMapping("forgetPwd")
	@ResponseBody
	public Object forgetPwd(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.forgetPwd(pd);
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
	 * 修改密码
	 * @return
	 */
	@RequestMapping("updatePwd")
	@ResponseBody
	public Object updatePwd(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.updatePwd(pd);
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
	public Object updateHead (){
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
	 * 修改昵称
	 * @return
	 */
	@RequestMapping("updateNickName")
	@ResponseBody
	public Object updateNickName (){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = userService.updateNickName(pd);
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
