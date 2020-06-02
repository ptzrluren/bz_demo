package com.flc.controller.user.user;

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
import com.flc.service.user.user.UserPartManager;
import com.flc.util.AppUtil;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.Jurisdiction;
import com.flc.util.MD5;
import com.flc.util.Tools;

/** 
 * 说明：用户详情
 * 创建人：FLC
 * 创建时间：2017-08-23
 */
@Controller
@RequestMapping(value="/userpart")
public class UserPartController extends BaseController {
	
	String menuUrl = "userpart/list.do"; //菜单地址(权限用)
	@Resource(name="userpartService")
	private UserPartManager userpartService;
	/**
	 * 修改用户状态   显示是否禁用
	 * @throws Exception
	 */
	@RequestMapping(value="/enable")
	public String updateEnAble() throws Exception{
		PageData pd = null;
		pd = this.getPageData();
		userpartService.updateEnAble(pd);
		return "redirect:/userpart/list.do?USER_ID="+pd.getString("USER_ID");
	}
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增UserPart");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("USERPART_ID", this.get32UUID());	//主键
		userpartService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除UserPart");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		userpartService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改UserPart");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String PWD = pd.getString("PWD");
		String old_pwd = pd.getString("old_pwd");
		//新旧密码不一致,说明修改过密码
		if(!PWD.equals(old_pwd)){
			PWD = MD5.md5(PWD);
			PWD = MD5.md5(PWD);
			pd.put("PWD", PWD);
		}
		userpartService.edit(pd);
		this.getRequest().getSession().setAttribute("USER_ID", pd.getString("USER_ID"));
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
		logBefore(logger, Jurisdiction.getUsername()+"列表UserPart");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		String user_id = pd.getString("USER_ID");
		if(!StringUtils.isEmpty(user_id)){
			this.getRequest().getSession().setAttribute("USER_ID", pd.getString("USER_ID"));
		}
		
		String USER_ID = (String)this.getRequest().getSession().getAttribute("USER_ID");
		if(!StringUtils.isEmpty(USER_ID)){
			pd.put("USER_ID", USER_ID);
		}
		page.setPd(pd);
		List<PageData>	varList = userpartService.list(page);	//列出UserPart列表
		mv.setViewName("user/user/userpart_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
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
		mv.setViewName("user/user/userpart_edit");
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
		pd = userpartService.findById(pd);	//根据ID读取
		mv.setViewName("user/user/userpart_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除UserPart");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			userpartService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出UserPart到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("用户编号");	//1
		titles.add("用户象征");	//2
		titles.add("创建时间");	//3
		titles.add("电话");	//4
		titles.add("头像");	//5
		titles.add("状态");	//6
		titles.add("修改时间");	//7
		titles.add("备注");	//8
		titles.add("用后昵称");	//9
		titles.add("是否禁用");	//10
		dataMap.put("titles", titles);
		List<PageData> varOList = userpartService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("USER_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("TOKEN"));	    //2
			vpd.put("var3", varOList.get(i).getString("CREATE_TIME"));	    //3
			vpd.put("var4", varOList.get(i).getString("PHONE"));	    //4
			vpd.put("var5", varOList.get(i).getString("HEAD_IMAGE"));	    //5
			vpd.put("var6", varOList.get(i).getString("STATE"));	    //6
			vpd.put("var7", varOList.get(i).getString("UPDATE_TIME"));	    //7
			vpd.put("var8", varOList.get(i).getString("REMARK"));	    //8
			vpd.put("var9", varOList.get(i).getString("NICK_NAME"));	    //9
			vpd.put("var10", varOList.get(i).getString("ON_WORK"));	    //10
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
