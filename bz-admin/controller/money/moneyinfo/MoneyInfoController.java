package com.flc.controller.money.moneyinfo;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.entity.system.User;
import com.flc.util.AppUtil;
import com.flc.util.Const;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.Jurisdiction;
import com.flc.util.Tools;
import com.flc.service.money.moneyinfo.MoneyInfoManager;

/** 
 * 说明：资金流水
 * 创建人：FLC
 * 创建时间：2017-08-22
 */
@Controller
@RequestMapping(value="/moneyinfo")
public class MoneyInfoController extends BaseController {
	
	String menuUrl = "moneyinfo/list.do"; //菜单地址(权限用)
	@Resource(name="moneyinfoService")
	private MoneyInfoManager moneyinfoService;
	
//	/**保存
//	 * @param
//	 * @throws Exception
//	 */
//	@RequestMapping(value="/save")
//	public ModelAndView save() throws Exception{
//		logBefore(logger, Jurisdiction.getUsername()+"新增MoneyInfo");
//		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
//		ModelAndView mv = this.getModelAndView();
//		PageData pd = new PageData();
//		pd = this.getPageData();
//		pd.put("MONEYINFO_ID", this.get32UUID());	//主键
//		moneyinfoService.save(pd);
//		mv.addObject("msg","success");
//		mv.setViewName("save_result");
//		return mv;
//	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	public void delete(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除MoneyInfo");
//		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		moneyinfoService.delete(pd);
		out.write("success");
		out.close();
	}
//	
//	/**修改
//	 * @param
//	 * @throws Exception
//	 */
//	@RequestMapping(value="/edit")
//	public ModelAndView edit() throws Exception{
//		logBefore(logger, Jurisdiction.getUsername()+"修改MoneyInfo");
//		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
//		ModelAndView mv = this.getModelAndView();
//		PageData pd = new PageData();
//		pd = this.getPageData();
//		moneyinfoService.edit(pd);
//		mv.addObject("msg","success");
//		mv.setViewName("save_result");
//		return mv;
//	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表MoneyInfo");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
//		String pay_state = pd.getString("pay_state");
//		if("0".equals(pay_state)){
//			pd.remove("pay_state");
//		} else if("1".equals(pay_state)){
//			pd.put("pay_state", "0");
//		} else if("2".equals(pay_state)){
//			pd.put("pay_state", "1");
//		}
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 用户名
			pd.put("STORE_ID", this.getRequest().getSession().getAttribute("STORE_ID"));
		}
		
		page.setPd(pd);
		List<PageData>	varList = moneyinfoService.list(page);	//列出MoneyInfo列表
		PageData sumMoney = moneyinfoService.sumMoney(pd);

		//统计现金剔除易点
		pd.put("MONEY", "MONEY");
		pd.put("TOTAL_MONEY", "TOTAL_MONEY");
		PageData sumMoney2 = moneyinfoService.sumMoney2(pd);
		sumMoney.putAll(sumMoney2);
		//统计易点剔除现金
		pd.put("MONEY", "POINT_MONEY");
		pd.put("TOTAL_MONEY", "POINT_TOTAL_MONEY");
		sumMoney2 = moneyinfoService.sumMoney2(pd);
		pd.remove("MONEY");
		pd.remove("TOTAL_MONEY");
		sumMoney.putAll(sumMoney2);
		
		mv.setViewName("money/moneyinfo/moneyinfo_list");
		mv.addObject("varList", varList);
		mv.addObject("sumMoney", sumMoney);
		mv.addObject("pd", pd);
		mv.addObject("QX",Jurisdiction.getHC());	//按钮权限
		return mv;
	}
	
//	/**去新增页面
//	 * @param
//	 * @throws Exception
//	 */
//	@RequestMapping(value="/goAdd")
//	public ModelAndView goAdd()throws Exception{
//		ModelAndView mv = this.getModelAndView();
//		PageData pd = new PageData();
//		pd = this.getPageData();
//		mv.setViewName("money/moneyinfo/moneyinfo_edit");
//		mv.addObject("msg", "save");
//		mv.addObject("pd", pd);
//		return mv;
//	}	
//	
	 /**去详情页面
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/goDetail")
	public ModelAndView goEdit()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		//订单消费详情
		List<PageData> store_list = moneyinfoService.getOrderDetailInfo(pd);
		//订单
		PageData detail = moneyinfoService.findById(pd);
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 用户名
			pd.put("STORE_ID", this.getRequest().getSession().getAttribute("STORE_ID"));
		}
		
		mv.setViewName("money/moneyinfo/moneyinfo_edit");
		mv.addObject("store_list", store_list);
		mv.addObject("detail", detail);
		mv.addObject("msg", "detail");
		mv.addObject("pd", pd);
		return mv;
	}	
//	
//	 /**批量删除
//	 * @param
//	 * @throws Exception
//	 */
//	@RequestMapping(value="/deleteAll")
//	@ResponseBody
//	public Object deleteAll() throws Exception{
//		logBefore(logger, Jurisdiction.getUsername()+"批量删除MoneyInfo");
//		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
//		PageData pd = new PageData();		
//		Map<String,Object> map = new HashMap<String,Object>();
//		pd = this.getPageData();
//		List<PageData> pdList = new ArrayList<PageData>();
//		String DATA_IDS = pd.getString("DATA_IDS");
//		if(null != DATA_IDS && !"".equals(DATA_IDS)){
//			String ArrayDATA_IDS[] = DATA_IDS.split(",");
//			moneyinfoService.deleteAll(ArrayDATA_IDS);
//			pd.put("msg", "ok");
//		}else{
//			pd.put("msg", "no");
//		}
//		pdList.add(pd);
//		map.put("list", pdList);
//		return AppUtil.returnObject(pd, map);
//	}
	
	 /**导出到excel
	 * @param
	 * @throws Exception
	 */
//	@RequestMapping(value="/excel")
//	public ModelAndView exportExcel() throws Exception{
//		logBefore(logger, Jurisdiction.getUsername()+"导出MoneyInfo到excel");
//		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
//		ModelAndView mv = new ModelAndView();
//		PageData pd = new PageData();
//		pd = this.getPageData();
//		Map<String,Object> dataMap = new HashMap<String,Object>();
//		List<String> titles = new ArrayList<String>();
//		titles.add("编号");	//1
//		titles.add("状态");	//2
//		titles.add("流水事件");	//3
//		titles.add("用户ID");	//4
//		titles.add("用户名");	//5
//		dataMap.put("titles", titles);
//		List<PageData> varOList = moneyinfoService.listAll(pd);
//		List<PageData> varList = new ArrayList<PageData>();
//		for(int i=0;i<varOList.size();i++){
//			PageData vpd = new PageData();
//			vpd.put("var1", varOList.get(i).get("ORDER_ID").toString());	//1
//			vpd.put("var2", varOList.get(i).get("PAY_STATE").toString());	//2
//			vpd.put("var3", varOList.get(i).getString("PAY_TIME"));	    //3
//			vpd.put("var4", varOList.get(i).get("USER_ID").toString());	//4
//			vpd.put("var5", varOList.get(i).getString("NICK_NAME"));	    //5
//			varList.add(vpd);
//		}
//		dataMap.put("varList", varList);
//		ObjectExcelView erv = new ObjectExcelView();
//		mv = new ModelAndView(erv,dataMap);
//		return mv;
//	}
	
	
	
	@RequestMapping(value="/excel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request, HttpServletResponse resp) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"导出Business到excel");
//		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData>	varList = moneyinfoService.listAll4Excel(pd);
		
		HSSFWorkbook wb = new HSSFWorkbook();
		
		request.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/x-download");

		String fileName = "订单流水.xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		resp.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		
		
		
		HSSFSheet sheet = wb.createSheet("商家流水");
		sheet.setColumnWidth(0,20 * 256);
		sheet.setColumnWidth(1,40 * 256);
		sheet.setColumnWidth(2,20 * 256);
		sheet.setColumnWidth(3,50 * 256);
		sheet.setColumnWidth(4,10 * 256);
		sheet.setColumnWidth(5,10 * 256);
		sheet.setColumnWidth(6,10 * 256);
		sheet.setColumnWidth(7,10 * 256);
		sheet.setColumnWidth(8,20 * 256);
		sheet.setColumnWidth(9,20 * 256);
		sheet.setColumnWidth(10,15 * 256);
		sheet.setColumnWidth(11,10 * 256);
		sheet.setColumnWidth(12,20 * 256);
		sheet.setColumnWidth(13,40 * 256);
		sheet.setColumnWidth(14,150 * 256);
		sheet.setDefaultRowHeight((short)300);
		HSSFDataFormat format= wb.createDataFormat();   //--->单元格内容格式    
        //样式1    
        HSSFCellStyle style = wb.createCellStyle(); // 样式对象    
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直    
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平    
        //设置标题字体格式    
        Font font = wb.createFont();       
        //设置字体样式     
        font.setFontHeightInPoints((short)12);   //--->设置字体大小    
        font.setFontName("Courier New");   //---》设置字体，是什么类型例如：宋体    
        font.setItalic(true);     //--->设置是否是加粗    
        style.setFont(font);     //--->将字体格式加入到style1中       
        style.setWrapText(true);   //设置是否能够换行，能够换行为true    
        HSSFCellStyle style2 = wb.createCellStyle(); //样式2，带金额
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直    
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平  
        style2.setDataFormat(format.getFormat("$#,##0"));    //--->设置为单元格内容为货币格式    
        //表格第一行    
        HSSFRow row1 = sheet.createRow(0);   //--->创建一行    
        HSSFCell cell1 = row1.createCell(0);
        cell1.setCellStyle(style);
        cell1.setCellValue("日期");
        HSSFCell cell2 = row1.createCell(1);
        cell2.setCellStyle(style);
        cell2.setCellValue("单号");   
        HSSFCell cell3 = row1.createCell(2);
        cell3.setCellStyle(style);
        cell3.setCellValue("电话");   
        HSSFCell cell4 = row1.createCell(3);
        cell4.setCellStyle(style);
        cell4.setCellValue("地址");   
        HSSFCell cell5 = row1.createCell(4);
        cell5.setCellStyle(style);
        cell5.setCellValue("金额");   
        HSSFCell cell6 = row1.createCell(5);
        cell6.setCellStyle(style);
        cell6.setCellValue("运费");   
        HSSFCell cell7 = row1.createCell(6);
        cell7.setCellStyle(style);
        cell7.setCellValue("税"); 
        HSSFCell cell8 = row1.createCell(7);
        cell8.setCellStyle(style);
        cell8.setCellValue("总金额"); 
        HSSFCell cell9 = row1.createCell(8);
        cell9.setCellStyle(style);
        cell9.setCellValue("提交时间"); 
        HSSFCell cell10 = row1.createCell(9);
        cell10.setCellStyle(style);
        cell10.setCellValue("完成时间"); 
        HSSFCell cell11 = row1.createCell(10);
        cell11.setCellStyle(style);
        cell11.setCellValue("快递员"); 
        HSSFCell cell12 = row1.createCell(11);
        cell12.setCellStyle(style);
        cell12.setCellValue("支付方式"); 
        HSSFCell cell13 = row1.createCell(12);
        cell13.setCellStyle(style);
        cell13.setCellValue("支付状态"); 
        HSSFCell cell14 = row1.createCell(13);
        cell14.setCellStyle(style);
        cell14.setCellValue("备注"); 
        HSSFCell cell15 = row1.createCell(14);
        cell15.setCellStyle(style);
        cell15.setCellValue("订单详情"); 
        
        for (int i = 0; i < varList.size(); i++) {
        	PageData order = varList.get(i);
        	HSSFRow rowN = sheet.createRow(i + 1);  //第i+1行
        	HSSFCell c1 = rowN.createCell(0);    
        	c1.setCellStyle(style);    
        	c1.setCellValue(order.getString("order_day"));
        	
        	HSSFCell c2 = rowN.createCell(1);    
        	c2.setCellStyle(style);    
        	c2.setCellValue(order.getString("order_id"));
        	
        	HSSFCell c3 = rowN.createCell(2);    
        	c3.setCellStyle(style);    
        	c3.setCellValue(order.getString("extm_phone"));
        	
        	HSSFCell c4 = rowN.createCell(3);    
        	c4.setCellStyle(style);    
        	c4.setCellValue(order.getString("address"));
        	
        	HSSFCell c5 = rowN.createCell(4);    
        	c5.setCellStyle(style);    
        	c5.setCellValue(order.get("money") + "");
        	
        	HSSFCell c6 = rowN.createCell(5);    
        	c6.setCellStyle(style);    
        	c6.setCellValue(order.get("yunfei") + "");
        	
        	HSSFCell c7 = rowN.createCell(6);    
        	c7.setCellStyle(style);    
        	c7.setCellValue(order.get("tax") + "");
        	
        	HSSFCell c8 = rowN.createCell(7);    
        	c8.setCellStyle(style);    
        	c8.setCellValue(order.get("total_money") + "");
        	
        	HSSFCell c9 = rowN.createCell(8);    
        	c9.setCellStyle(style);    
        	c9.setCellValue(order.get("pay_time") + "");
        	
        	HSSFCell c10 = rowN.createCell(9);    
        	c10.setCellStyle(style);    
        	c10.setCellValue(order.get("finish_time") + "");
        	
        	HSSFCell c11 = rowN.createCell(10);    
        	c11.setCellStyle(style);    
        	c11.setCellValue(order.getString("nick_name"));
        	
        	String payment_method = order.getString("payment_method");
        	HSSFCell c12 = rowN.createCell(11);    
        	c12.setCellStyle(style);    
        	if("1".equals(payment_method)){
        		c12.setCellValue("paypal");
        	} else if("2".equals(payment_method)){
        		c12.setCellValue("applypay");
        	} else if("3".equals(payment_method)){
        		c12.setCellValue("刷卡支付");
        	}
        	
        	String pay_state = order.getString("pay_state");
        	HSSFCell c13 = rowN.createCell(12);    
        	c13.setCellStyle(style);    
        	if("1".equals(pay_state)){
        		c13.setCellValue(order.getString("已支付"));
        	} else {
        		c13.setCellValue(order.getString("未支付"));
        	}
        	
        	
        	HSSFCell c14 = rowN.createCell(13);    
        	c14.setCellStyle(style);    
        	c14.setCellValue(order.getString("remark"));
        	
        	PageData fpd = new PageData();
        	String order_id = order.getString("order_id");
        	fpd.put("order_id", order_id);
        	List<PageData> foods_list = moneyinfoService.getOrderStore4Excel(fpd);
        	
        	
        	HSSFCell c15 = rowN.createCell(14);    
        	c15.setCellStyle(style);    
        	
        	String str = "";
        	for (int j = 0; j < foods_list.size(); j++) {
				PageData foods = foods_list.get(j);
				String store_name = foods.getString("store_name_cn");
				int quantity = (Integer)foods.get("quantity");
				double price = ((BigDecimal)foods.get("price")).doubleValue();
				str += store_name + "   ---  " + foods.getString("foods_name_cn") + "   :   " + price + " * " + quantity + " = " + quantity * price;
				if(j != foods_list.size() - 1){
					str += "\r\n";
				}
			}
        	c15.setCellValue(new HSSFRichTextString(str));
        	
		}
        try {
			OutputStream out = resp.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e1) {
			logger.info("=====导出excel异常====");
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format,true));
	}
}
