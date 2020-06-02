package com.flc.controller.app.appuser;

import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.appuser.AppuserManager;
import com.flc.util.AppUtil;
import com.flc.util.PageData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/appuser")
public class IntAppuserController extends BaseController{
   
	@Resource(name="appuserService")
	private AppuserManager appuserService;
	
	/**@author FLC
	 * 测试接口（支持ajax跨域请求）
	 * return json
	 */
	@RequestMapping(value="/getAppuserByUm")
	@ResponseBody
	public Object getAppuserByUsernmae(){
		Map<String,Object> map = new HashMap<String,Object>();
		PageData pd = this.getPageData();
		PageData pdreturn = null;
		try {
			pdreturn = appuserService.findByUiId(pd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("status", "10001");
		map.put("message", "成功！");
		map.put("data", pdreturn);
		return AppUtil.returnObject(pd, map);
	}
	
	/**
	 * @author FLC
	 * 测试传值跳转到页面
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/listUsers")
	public ModelAndView listUsers(Page page){
		ModelAndView mv = this.getModelAndView();
		PageData pd = null;
		try{
			pd = this.getPageData();
			String keywords = pd.getString("keywords");
			if(null != keywords && !"".equals(keywords)){
				pd.put("keywords", keywords.trim());
			}
			page.setPd(pd);
			List<PageData>	userList = appuserService.listPdPageUser(page);
			mv.setViewName("test");
			mv.addObject("userList", userList);
			mv.addObject("pd", pd);
		} catch(Exception e){
			logger.error(e.toString(), e);
		}
		return mv;
	}
}
	
 