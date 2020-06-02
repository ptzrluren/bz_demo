package com.flc.service.message.impl;

import com.alibaba.druid.util.StringUtils;
import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.service.message.MessageManager;
import com.flc.util.PageData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/** 
 * 说明： Banner
 * 创建人：FLC
 * 创建时间：2017-03-07
 * @version
 */
@Service("messageService")
public class MessageService implements MessageManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;

	/**
	 * 查询消息列表
	 */
	@Override
	public PageData getMessageList(PageData pd) throws Exception {
		String user_id = pd.getString("user_id");
		PageData result = new PageData();
		Page page = new Page();
		page.setPd(pd);
		String index = pd.getString("page");
		String page_size = pd.getString("page_size");
		int pg = 0;
		int size = 0;
		if(StringUtils.isEmpty(index)){
			pg = 1;
		} else {
			pg = Integer.parseInt(index);
		}
		if(StringUtils.isEmpty(page_size)){
			size = 5;
		}  else {
			size = Integer.parseInt(page_size);
		}
		page.setCurrentPage(pg);
		page.setShowCount(size);
		//分页查询
		List<PageData> list = (List<PageData>)dao.findForList("MessageMapper.datalistPage", page);
		for (int i = 0; i < list.size(); i++) {
			PageData lpd = new PageData();
			lpd.put("message_id", list.get(i).get("message_id"));
			lpd.put("user_id", user_id);
			//查询该信息该用户是否已读
			int count = (Integer)dao.findForObject("MessageMapper.getReadInfo", lpd);
			if(count > 0){
				list.get(i).put("state", "1");
			} else {
				list.get(i).put("state", "0");
			}
		}
		result.put("rows", list);
		result.put("total", page.getTotalResult());
		result.put("total_page", page.getTotalPage());
		return result;
	}

	
	/**
	 * 已读
	 */
	@Override
	public PageData readMessage(PageData pd) throws Exception {
		PageData result = new PageData();
		dao.save("MessageMapper.readMessage", pd);
		return result;
	}

	
	/**
	 * 删除
	 */
	@Override
	public PageData delMessage(PageData pd) throws Exception {
		PageData result = new PageData();
		dao.save("MessageMapper.delMessage", pd);
		return result;
	}
	
	
}

