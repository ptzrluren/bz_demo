package com.flc.controller.user.user;

import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.user.user.UserManager;
import com.flc.util.AppUtil;
import com.flc.util.Jurisdiction;
import com.flc.util.MD5;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 说明：4.用户管理
 * 创建人：FLC
 * 创建时间：2017-08-18
 */
@Controller
@RequestMapping(value="/users")
public class UsersController extends BaseController {
	
	String menuUrl = "users/list.do"; //菜单地址(权限用)
	@Resource(name="userNormalService")
	private UserManager userService;
	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增User");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		pd = this.getPageData();
		String PWD = pd.getString("PWD");
		PWD = MD5.md5(MD5.md5(PWD));//两次MD5加密
		pd.put("PWD", PWD);
		
		pd.put("USER_ID", this.get32UUID());	//主键
		userService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除User");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		userService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**
	 * 禁用用户
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/disableUser")
	@ResponseBody
	public Object disableUser(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除User");
		PageData pd = new PageData();
		pd = this.getPageData();
		userService.disableUser(pd);
		PageData result = new PageData();
		result.put("success", "success");
		result.put("data", pd);
		return result;
	}
	
	/**
	 * 取消禁用
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/enableUser")
	@ResponseBody
	public Object enableUser(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除User");
		PageData pd = new PageData();
		pd = this.getPageData();
		userService.enableUser(pd);
		PageData result = new PageData();
		result.put("success", "success");
		result.put("data", pd);
		return result;
	}
	
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改User");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		pd = this.getPageData();
		String PWD = pd.getString("PWD");
		PWD = MD5.md5(MD5.md5(PWD));//两次MD5加密
		pd.put("PWD", PWD);
		
		
		
		userService.edit(pd);
		mv.addObject("msg","success");
		mv.addObject("requestPath",requestPath);
		mv.setViewName("save_result");
		return mv;
	}
	
	/**根据用户手机号获取邀请人列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/getInviter")
	public ModelAndView getInviter(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"根据用户手机号获取邀请人列表User");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		
		String condition = pd.getString("name");				//关键词检索条件
		if(null != condition && !"".equals(condition)){
			pd.put("condition", condition);
		}
		page.setPd(pd);
		List<PageData>	varList = userService.list(page);	//列出User列表
		mv.setViewName("user/user/user_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("requestPath",requestPath);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		mv.addObject("view", "getInviter");
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表User");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		
		String condition = pd.getString("name");				//关键词检索条件
		if(null != condition && !"".equals(condition)){
			pd.put("condition", condition);
		}
		
		page.setPd(pd);
		List<PageData>	varList = userService.list(page);	//列出User列表
		mv.setViewName("user/user/user_list");
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
		mv.setViewName("user/user/user_edit");
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
		pd = userService.findById(pd);	//根据ID读取
		mv.setViewName("user/user/user_edit");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	
	@RequestMapping(value="/showWallet")
	public ModelAndView showWallet()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = userService.findWallet(pd);	//根据ID读取
		mv.setViewName("user/user/user_wallet");
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	@RequestMapping(value="/saveFandian")
	public ModelAndView saveFandian() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改饭点");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		
		
		try {
			userService.saveFandian(pd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mv.addObject("msg","success");
		mv.addObject("requestPath",requestPath);
		mv.setViewName("save_result");
		return mv;
	}
	
	
	/**
	 * 展示饭点兑换记录
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/showExchangeLog")
	public ModelAndView showExchangeLog()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = userService.findWallet(pd);	//根据ID读取
		mv.setViewName("user/user/user_wallet");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除User");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			userService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出User到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("token");	//1
		titles.add("注册时间");	//2
		titles.add("手机号码");	//3
		titles.add("密码");	//4
		titles.add("头像");	//5
		titles.add("账号状态");	//6
		titles.add("修改时间");	//7
		titles.add("备注");	//8
		titles.add("昵称");	//9
		dataMap.put("titles", titles);
		List<PageData> varOList = userService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("TOKEN"));	    //1
			vpd.put("var2", varOList.get(i).getString("CREATE_TIME"));	    //2
			vpd.put("var3", varOList.get(i).getString("PHONE"));	    //3
			vpd.put("var4", varOList.get(i).getString("PWD"));	    //4
			vpd.put("var5", varOList.get(i).getString("HEAD_IMAGE"));	    //5
			vpd.put("var6", varOList.get(i).getString("STATE"));	    //6
			vpd.put("var7", varOList.get(i).getString("UPDATE_TIME"));	    //7
			vpd.put("var8", varOList.get(i).getString("REMARK"));	    //8
			vpd.put("var9", varOList.get(i).getString("NICK_NAME"));	    //9
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
