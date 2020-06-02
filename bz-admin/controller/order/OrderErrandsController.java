package com.flc.controller.order;

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

import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.order.OrderErrandsManager;
import com.flc.util.AppUtil;
import com.flc.util.Jurisdiction;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;

/** 
 * 说明：私人订制订单
 * 创建人：FLC
 * 创建时间：2018-03-17
 */
@Controller
@RequestMapping(value="/ordererrands")
public class OrderErrandsController extends BaseController {
	
	String menuUrl = "ordererrands/list.do"; //菜单地址(权限用)
	@Resource(name="ordererrandsService")
	private OrderErrandsManager ordererrandsService;
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增OrderErrands");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("ORDERERRANDS_ID", this.get32UUID());	//主键
		ordererrandsService.save(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"删除OrderErrands");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		ordererrandsService.delete(pd);
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改OrderErrands");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		ordererrandsService.edit(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"列表OrderErrands");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = ordererrandsService.list(page);	//列出OrderErrands列表
		mv.setViewName("order/ordererrands/ordererrands_list");
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
		mv.setViewName("order/ordererrands/ordererrands_edit");
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
		pd = ordererrandsService.findById(pd);	//根据ID读取
		mv.setViewName("order/ordererrands/ordererrands_edit");
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除OrderErrands");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			ordererrandsService.deleteAll(ArrayDATA_IDS);
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
	public ModelAndView exportExcel(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出OrderErrands到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("订单编号");	//1
		titles.add("订单状态");	//2
		titles.add("支付方式");	//3
		titles.add("支付状态");	//4
		titles.add("支付金额");	//5
		titles.add("下单时间");	//6
		titles.add("接单时间");	//7
		titles.add("取件时间");	//8
		titles.add("支付时间");	//9
		titles.add("订单完成时间");	//10
		titles.add("商品描述");	//11
		titles.add("配送员昵称");	//12
		titles.add("配送员电话");	//13
		titles.add("用户昵称");	//14
		titles.add("收货人名称");	//15
		titles.add("收货人电话");	//16
		titles.add("收货邮编");	//17
		titles.add("收货地址");	//18
		dataMap.put("titles", titles);
		List<PageData> varOList = ordererrandsService.listAll(page);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("order_id"));	    //1
			vpd.put("var2", varOList.get(i).getString("state_str"));	    //2
			vpd.put("var3", varOList.get(i).getString("payment_method_str"));	    //3
			vpd.put("var4", varOList.get(i).getString("pay_state_str"));	    //4
			vpd.put("var5", String.valueOf(varOList.get(i).get("total_money")));	    //5
			vpd.put("var6", varOList.get(i).getString("create_time"));	    //6
			vpd.put("var7", varOList.get(i).getString("order_time"));	    //7
			vpd.put("var8", varOList.get(i).getString("pickup_time"));	    //8
			vpd.put("var9", String.valueOf(varOList.get(i).get("pay_time")));	    //9
			vpd.put("var10", varOList.get(i).getString("finish_time"));	    //10
			vpd.put("var11", varOList.get(i).getString("remark"));	    //11
			vpd.put("var12", varOList.get(i).getString("exp_nick_name"));	    //12
			vpd.put("var13", varOList.get(i).getString("exp_phone"));	    //13
			vpd.put("var14", varOList.get(i).getString("nick_name"));	    //14
			vpd.put("var15", varOList.get(i).getString("extm_name"));	    //15
			vpd.put("var16", varOList.get(i).getString("extm_phone"));	    //16
			vpd.put("var17", varOList.get(i).getString("zip_code"));	    //17
			vpd.put("var18", varOList.get(i).getString("address"));	    //18
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
