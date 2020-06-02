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
import com.flc.service.order.OrderDetailsManager;
import com.flc.util.AppUtil;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.Jurisdiction;
import com.flc.util.Tools;

/** 
 * 说明：订单详情
 * 创建人：FLC
 * 创建时间：2017-08-24
 */
@Controller
@RequestMapping(value="/orderdetails")
public class OrderDetailsController extends BaseController {
	
	String menuUrl = "orderdetails/list.do"; //菜单地址(权限用)
	@Resource(name="orderdetailsService")
	private OrderDetailsManager orderdetailsService;
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表OrderDetails");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData>	varList = orderdetailsService.list(page);	//列出OrderDetails列表
		mv.setViewName("order/orderpart/orderdetails_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/excel")
	public ModelAndView exportExcel() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出OrderDetails到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("订单编号");	//1
		titles.add("支付方式");	//2
		titles.add("备注");	//3
		titles.add("地址编号");	//4
		titles.add("订单状态");	//5
		titles.add("支付状态");	//6
		titles.add("支付时间");	//7
		titles.add("金额");	//8
		titles.add("快递员编号");	//9
		titles.add("创建时间");	//10
		titles.add("接单时间");	//11
		titles.add("取件时间");	//12
		titles.add("订单完成时间");	//13
		titles.add("用户编号");	//14
		dataMap.put("titles", titles);
		List<PageData> varOList = orderdetailsService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("ORDER_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("PAYMENT_METHOD"));	    //2
			vpd.put("var3", varOList.get(i).getString("REMARK"));	    //3
			vpd.put("var4", varOList.get(i).getString("EXTM_ID"));	    //4
			vpd.put("var5", varOList.get(i).getString("STATE"));	    //5
			vpd.put("var6", varOList.get(i).getString("PAY_STATE"));	    //6
			vpd.put("var7", varOList.get(i).getString("PAY_TIME"));	    //7
			vpd.put("var8", varOList.get(i).getString("MONEY"));	    //8
			vpd.put("var9", varOList.get(i).getString("EXP_ID"));	    //9
			vpd.put("var10", varOList.get(i).getString("CREATE_TIME"));	    //10
			vpd.put("var11", varOList.get(i).getString("ORDER_TIME"));	    //11
			vpd.put("var12", varOList.get(i).getString("PICKUP_TIME"));	    //12
			vpd.put("var13", varOList.get(i).getString("FINISH_TIME"));	    //13
			vpd.put("var14", varOList.get(i).getString("USER_ID"));	    //14
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
