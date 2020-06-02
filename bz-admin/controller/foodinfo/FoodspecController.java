package com.flc.controller.foodinfo;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;
import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.foodinfo.FoodspecManager;
import com.flc.util.AppUtil;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.Jurisdiction;
import com.flc.util.Tools;

/** 
 * 说明：商品规格
 * 创建人：FLC
 * 创建时间：2017-08-18
 */
@Controller
@RequestMapping(value="/foodspec")
public class FoodspecController extends BaseController {
	
	String menuUrl = "foodspec/list.do"; //菜单地址(权限用)
	@Resource(name="foodspecService")
	private FoodspecManager foodspecService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Foodspec");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
//		pd.put("SPEC_ID", this.get32UUID());	//主键
		foodspecService.save(pd);
		this.getRequest().getSession().setAttribute("SPEC_CLASS_ID", pd.getString("SPEC_CLASS_ID"));
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	public void delete(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除Foodspec");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		foodspecService.delete(pd);
		this.getRequest().getSession().setAttribute("SPEC_CLASS_ID", pd.getString("SPEC_CLASS_ID"));
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改Foodspec");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		foodspecService.edit(pd);
		this.getRequest().getSession().setAttribute("SPEC_CLASS_ID", pd.getString("SPEC_CLASS_ID"));
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Foodspec");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		String specid = pd.getString("SPEC_CLASS_ID");
		if(!StringUtils.isEmpty(specid)){
			this.getRequest().getSession().setAttribute("SPEC_CLASS_ID", pd.getString("SPEC_CLASS_ID"));
		}
		String spec_id = (String) this.getRequest().getSession().getAttribute("SPEC_CLASS_ID");
		if(!StringUtils.isEmpty(spec_id )){
			pd.put("SPEC_CLASS_ID", spec_id);
		}
		page.setPd(pd);
		List<PageData>	varList = foodspecService.list(page);	//列出Foodspec列表
		mv.setViewName("foods/foodinfo/foodspec_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("FOODS_ID",pd.getString("FOODS_ID"));
		mv.addObject("SPEC_CLASS_ID",pd.getString("SPEC_CLASS_ID"));
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
	/**去新增页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goAdd")
	public ModelAndView goAdd()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("foods/foodinfo/foodspec_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**去修改页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goEdit")
	public ModelAndView goEdit()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = foodspecService.findById(pd);	//根据ID读取
		mv.setViewName("foods/foodinfo/foodspec_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"批量删除Foodspec");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			foodspecService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		}else{
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出Foodspec到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("规格编号");	//1
		titles.add("规格中文名");	//2
		titles.add("规格英文名");	//3
		titles.add("商品编号");	//4
		titles.add("规格类别编号");	//5
		titles.add("创建时间");	//6
		dataMap.put("titles", titles);
		List<PageData> varOList = foodspecService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("SPEC_ID").toString());	//1
			vpd.put("var2", varOList.get(i).getString("SPEC_NAME_CN"));	    //2
			vpd.put("var3", varOList.get(i).getString("SPEC_NAME_EN"));	    //3
			vpd.put("var4", varOList.get(i).getString("FOODS_ID"));	    //4
			vpd.put("var5", varOList.get(i).getString("SPEC_CLASS_ID"));	    //5
			vpd.put("var6", varOList.get(i).getString("CREATE_TIME"));	    //6
			varList.add(vpd);
		}
		dataMap.put("varList", varList);
		ObjectExcelView erv = new ObjectExcelView();
		mv = new ModelAndView(erv,dataMap);
		return mv;
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
