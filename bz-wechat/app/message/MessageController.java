package com.flc.controller.app.message;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.message.MessageManager;
import com.flc.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/app/message")
public class MessageController extends BaseController {
	
	
	@Autowired
	private MessageManager msgService;
	
	/**
	 * 查询消息列表
	 * @return
	 */
	@RequestMapping("getMessageList")
	@ResponseBody
	public Object login(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = msgService.getMessageList(pd);
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
	 * 阅读消息
	 * @return
	 */
	@RequestMapping("readMessage")
	@ResponseBody
	public Object readMessage(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = msgService.readMessage(pd);
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
	 * 删除消息
	 * @return
	 */
	@RequestMapping("delMessage")
	@ResponseBody
	public Object delMessage(){
		PageData pd = null;
		pd = this.getPageData();
		PageData result = new PageData();
		try {
			result = msgService.delMessage(pd);
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
