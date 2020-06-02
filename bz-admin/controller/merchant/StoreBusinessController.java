package com.flc.controller.merchant;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.merchant.BusinessManager;
import com.flc.util.Jurisdiction;
import com.flc.util.PageData;

@Controller
@RequestMapping(value="/storeBusiness")
public class StoreBusinessController extends BaseController{

	String menuUrl = "storeBusiness/list.do"; //菜单地址(权限用)
	@Resource(name="businessService")
	private BusinessManager businessService;
	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	@RequestMapping(value="/upshelves")
	public String updateShelves()throws Exception{
		PageData pd = null;
		pd = this.getPageData();
		businessService.updateShelves(pd);
		return "redirect:/business/list.do";
	}
	
	
	
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Business");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> varList = businessService.storeBusniesslist(page);	//列出Business列表
		mv.setViewName("com/business/business_list");
		mv.addObject("requestPath", requestPath);
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
}
