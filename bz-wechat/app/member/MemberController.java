package com.flc.controller.app.member;

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

import java.util.List;

@Controller
@RequestMapping(value="/app/member")
public class MemberController extends BaseController {
	
	
	@Autowired
	private UserManager userService;
	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	/**
	 * 查询订单列表
	 * @return
	 */
	@RequestMapping("getCardList")
	@ResponseBody
	public Object getCardList(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			pd.put("requestPath", requestPath);
			List<PageData> list = userService.getCardList(pd);
			result.put("list", list);
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
	 * 新增卡
	 * @return
	 */
	@RequestMapping("saveCard")
	@ResponseBody
	public Object saveCard(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			pd.put("requestPath", requestPath);
			userService.saveCard(pd);
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
	 * 删除卡
	 * @return
	 */
	@RequestMapping("delCard")
	@ResponseBody
	public Object delCard(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			pd.put("requestPath", requestPath);
			userService.delCard(pd);
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
