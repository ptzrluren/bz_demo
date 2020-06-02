package com.flc.service.message;

import com.flc.util.PageData;

public interface MessageManager {

	/**
	 * 查询消息列表
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getMessageList(PageData pd) throws Exception;
	
	
	public PageData readMessage(PageData pd) throws Exception;
	
	
	
	public PageData delMessage(PageData pd) throws Exception;
	
}
