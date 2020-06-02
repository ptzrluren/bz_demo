package com.flc.controller.user.user_exp;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
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
import com.flc.util.AppUtil;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.Jurisdiction;
import com.flc.util.MD5;
import com.flc.util.Tools;
import com.flc.service.user.user_exp.User_expManager;

/** 
 * 说明：3.快递员管理
 * 创建人：FLC
 * 创建时间：2017-08-18
 */
@Controller
@RequestMapping(value="/user_exp")
public class User_expController extends BaseController {
	
	String menuUrl = "user_exp/list.do"; //菜单地址(权限用)
	@Resource(name="user_expService")
	private User_expManager user_expService;
	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增User_exp");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		String NICK_NAME = pd.getString("NICK_NAME");
		//String CREATE_TIME = pd.getString("CREATE_TIME");
		String STATE = "3";
		String PWD = pd.getString("PWD");
		
		PWD = MD5.md5(MD5.md5(PWD));//两次MD5加密
		
		String PHONE = pd.getString("PHONE");
		
		pd.put("EXP_ID", this.get32UUID());	//主键
		pd.put("NICK_NAME", NICK_NAME);
		pd.put("STATE", STATE);
		pd.put("PWD", PWD);
		pd.put("PHONE", PHONE);
		
		user_expService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除User_exp");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		user_expService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改User_exp");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		user_expService.edit(pd);
		mv.addObject("msg","success");
		mv.addObject("requestPath",requestPath);
		mv.setViewName("save_result");
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表User_exp");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		pd.put("requestPath", requestPath);
		
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		String condition = pd.getString("name");				//关键词检索条件
		if(null != condition && !"".equals(condition)){
			pd.put("condition", condition);
		}
		if(StringUtils.isEmpty(condition)){
			
		}else{
		switch (condition) {
			case "state":
				switch (keywords) {
					case "未审核":
						keywords="0";
					break;
					case "后台审核通过":
						keywords="1";
					break;
					case "审核未通过":
						keywords="2";
					break;
					case "自动审核成功":
						keywords="3";
						break;
					default:
						keywords="";
					break;
				}
			break;
			case "on_work":
				if(keywords.contains("未")){
					keywords="0";
				}else{
					keywords="1";
				}
			break;
			default:
				keywords="";
				break;
		}
		}
		page.setPd(pd);
		List<PageData>	varList = user_expService.list(page);	//列出User_exp列表
		mv.setViewName("user/user_exp/user_exp_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("requestPath",requestPath);
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
		mv.setViewName("user/user_exp/user_exp_edit");
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		mv.addObject("requestPath",requestPath);
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
		pd = user_expService.findById(pd);	//根据ID读取
		mv.setViewName("user/user_exp/user_exp_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		mv.addObject("requestPath",requestPath);
		return mv;
	}	
	
	 /**批量删除
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"批量删除User_exp");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			user_expService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出User_exp到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("编号");	//1
		titles.add("昵称");	//2
		titles.add("头像");	//3
		titles.add("注册时间");	//4
		titles.add("状态");	//5
		titles.add("上下班状态");	//6
		titles.add("密码");	//7
		titles.add("token");	//8
		titles.add("手机号");	//9
		titles.add("经度");	//10
		titles.add("纬度");	//11
		dataMap.put("titles", titles);
		List<PageData> varOList = user_expService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).get("EXP_ID").toString());	//1
			vpd.put("var2", varOList.get(i).getString("NICK_NAME"));	    //2
			vpd.put("var3", varOList.get(i).getString("EXP_IMAGE"));	    //3
			vpd.put("var4", varOList.get(i).getString("CREATE_TIME"));	    //4
			vpd.put("var5", varOList.get(i).getString("STATE"));	    //5
			vpd.put("var6", varOList.get(i).getString("ON_WORK"));	    //6
			vpd.put("var7", varOList.get(i).getString("PWD"));	    //7
			vpd.put("var8", varOList.get(i).getString("TOKEN"));	    //8
			vpd.put("var9", varOList.get(i).getString("PHONE"));	    //9
			vpd.put("var10", varOList.get(i).getString("LNG"));	    //10
			vpd.put("var11", varOList.get(i).getString("LAT"));	    //11
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
