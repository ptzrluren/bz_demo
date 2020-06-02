package com.flc.controller.merchant;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.StringUtils;
import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.entity.system.User;
import com.flc.service.merchant.BusinessManager;
import com.flc.util.AppUtil;
import com.flc.util.Const;
import com.flc.util.Jurisdiction;
import com.flc.util.PageData;
import com.flc.util.PrintUtil;

/**
 * 说明：商家管理-增删改查，上下架 创建人：FLC 创建时间：2017-08-16
 */
@Controller
@RequestMapping(value = "/business")
public class BusinessController extends BaseController {

	String menuUrl = "business/list.do"; // 菜单地址(权限用)
	@Resource(name = "businessService")
	private BusinessManager businessService;

	@Value("${upload.requestPath}")
	private String requestPath;

	@RequestMapping(value = "/upshelves")
	public String updateShelves() throws Exception {
		PageData pd = null;
		pd = this.getPageData();
		businessService.updateShelves(pd);
		return "redirect:/business/list.do";
	}

	/**
	 * 保存
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/save")
	public ModelAndView save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增Business");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "add")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("STORE_ID", this.get32UUID()); // 主键
		String print_no = pd.getString("print_no");
		String print_code = pd.getString("print_code");
		String snlist = print_no + "#" + print_code + "#wu#wu";
		String str = PrintUtil.addprinter(snlist);
		// 将打印机编号注册到官网
		Map<String, Object> obj = (Map<String, Object>) JSONUtils.parse(str);
		String print_re = (String) obj.get("msg");
		// 如果不成功
		if (!"ok".equals(print_re)) {
			mv.addObject("msg", "注册打印机失败");
			mv.setViewName("save_result");
		} else {
			businessService.save(pd);
			mv.addObject("msg", "success");
			mv.setViewName("save_result");
		}
		return mv;
	}

	/**
	 * 删除
	 * 
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value = "/delete")
	public void delete(PrintWriter out) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "删除Business");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return;
		} // 校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		businessService.delete(pd);
		out.write("success");
		out.close();
	}

	/**
	 * 修改
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/edit")
	public ModelAndView edit() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "修改Business");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "edit")) {
			return null;
		} // 校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();

		String print_no = pd.getString("print_no");
		String print_code = pd.getString("print_code");
		if (StringUtils.isEmpty(print_code) || StringUtils.isEmpty(print_no)) {
			businessService.edit(pd);
			mv.addObject("msg", "success");
			mv.setViewName("save_result");
		} else {
			String snlist = print_no + "#" + print_code + "#wu#wu";
			String str = PrintUtil.addprinter(snlist);
			// 将打印机编号注册到官网
			Map<String, Object> obj = (Map<String, Object>) JSONUtils.parse(str);
			String print_re = (String) obj.get("msg");
			// 如果不成功
			if (!"ok".equals(print_re)) {
				mv.addObject("msg", "注册打印机失败");
				mv.setViewName("save_result");
			} else {
				businessService.edit(pd);
				mv.addObject("msg", "success");
				mv.setViewName("save_result");
			}

		}
		return mv;
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value = "/list")
	public ModelAndView list(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Business");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		System.out.println(new Date());
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 商家上下架权限
			pd.put("STORE_ID", this.getRequest().getSession().getAttribute("STORE_ID"));
		}
		
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> varList = businessService.list(page); // 列出Business列表
		mv.setViewName("com/business/business_list");
		mv.addObject("requestPath", requestPath);
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 查询商家的订单流水
	 * 
	 * @param page
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listLiushui")
	public ModelAndView listLiushui(Page page) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "列表Business");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		// //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords"); // 关键词检索条件
		if (null != keywords && !"".equals(keywords)) {
			pd.put("keywords", keywords.trim());
		}
		page.setPd(pd);
		List<PageData> varList = businessService.storeBusniesslist(page); // 列出Business列表
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 商家上下架权限
			pd.put("STORE_ID", this.getRequest().getSession().getAttribute("STORE_ID"));
		}
		
		PageData sumMoney = businessService.sumMoneyById(pd);
		if(sumMoney != null) {
			mv.addObject("sumMoney", sumMoney.get("sumMoney"));
		}
		
		mv.setViewName("com/business/store_business_list");
		mv.addObject("requestPath", requestPath);
		mv.addObject("varList", varList);
		mv.addObject("STORE_ID", pd.getString("STORE_ID"));
		mv.addObject("pd", pd);
		mv.addObject("QX", Jurisdiction.getHC()); // 按钮权限
		return mv;
	}

	/**
	 * 去新增页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goAdd")
	public ModelAndView goAdd() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		mv.setViewName("com/business/business_edit");
		mv.addObject("msg", "save");
		List<PageData> typeList = businessService.findStrotType(pd);
		mv.addObject("pd", pd);
		mv.addObject("typeList", typeList);

		return mv;
	}

	/**
	 * 去修改页面
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/goEdit")
	public ModelAndView goEdit() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd = businessService.findById(pd); // 根据ID读取
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 用户名
			pd.put("STORE_ID", this.getRequest().getSession().getAttribute("STORE_ID"));
		}
		
		mv.setViewName("com/business/business_edit");
		List<PageData> typeList = businessService.findStrotType(pd);
		mv.addObject("typeList", typeList);
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	/**
	 * 批量删除
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/deleteAll")
	@ResponseBody
	public Object deleteAll() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "批量删除Business");
		if (!Jurisdiction.buttonJurisdiction(menuUrl, "del")) {
			return null;
		} // 校验权限
		PageData pd = new PageData();
		Map<String, Object> map = new HashMap<String, Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if (null != DATA_IDS && !"".equals(DATA_IDS)) {
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			businessService.deleteAll(ArrayDATA_IDS);
			pd.put("msg", "ok");
		} else {
			pd.put("msg", "no");
		}
		pdList.add(pd);
		map.put("list", pdList);
		return AppUtil.returnObject(pd, map);
	}

	/**
	 * 导出到excel
	 * 
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value = "/excel")
	@ResponseBody
	public void exportExcel(HttpServletRequest request, HttpServletResponse resp) throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "导出Business到excel");
		// if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Page page = new Page();
		page.setCurrentPage(1);
		page.setShowCount(10000000);
		page.setPd(pd);
		List<PageData> store_list = businessService.storeBusniesslist(page);

		HSSFWorkbook wb = new HSSFWorkbook();

		request.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/x-download");

		String fileName = "商家流水.xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		resp.addHeader("Content-Disposition", "attachment;filename=" + fileName);

		HSSFSheet sheet = wb.createSheet("商家流水");
		sheet.setColumnWidth(0, 50 * 256);
		sheet.setColumnWidth(1, 50 * 256);
		sheet.setColumnWidth(2, 60 * 256);
		sheet.setColumnWidth(3, 40 * 256);
		sheet.setColumnWidth(4, 30 * 256);
		sheet.setColumnWidth(5, 40 * 256);
		sheet.setDefaultRowHeight((short) 300);
		HSSFDataFormat format = wb.createDataFormat(); // --->单元格内容格式
		// 样式1
		HSSFCellStyle style = wb.createCellStyle(); // 样式对象
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		// 设置标题字体格式
		Font font = wb.createFont();
		// 设置字体样式
		font.setFontHeightInPoints((short) 15); // --->设置字体大小
		font.setFontName("Courier New"); // ---》设置字体，是什么类型例如：宋体
		font.setItalic(true); // --->设置是否是加粗
		style.setFont(font); // --->将字体格式加入到style1中
		style.setWrapText(true); // 设置是否能够换行，能够换行为true
		HSSFCellStyle style2 = wb.createCellStyle(); // 样式2，带金额
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 水平
		style2.setDataFormat(format.getFormat("$#,##0")); // --->设置为单元格内容为货币格式
		// 表格第一行
		HSSFRow row1 = sheet.createRow(0); // --->创建一行
		HSSFCell cell1 = row1.createCell(0);
		cell1.setCellStyle(style);
		cell1.setCellValue("商家名称");
		HSSFCell cell2 = row1.createCell(1);
		cell2.setCellStyle(style);
		cell2.setCellValue("日期");
		HSSFCell cell3 = row1.createCell(2);
		cell3.setCellStyle(style);
		cell3.setCellValue("单号");
		HSSFCell cell4 = row1.createCell(3);
		cell4.setCellStyle(style);
		cell4.setCellValue("商品");
		HSSFCell cell5 = row1.createCell(4);
		cell5.setCellStyle(style);
		cell5.setCellValue("数量");
		HSSFCell cell6 = row1.createCell(5);
		cell6.setCellStyle(style);
		cell6.setCellValue("价格");
		HSSFCell cell7 = row1.createCell(6);
		cell7.setCellStyle(style);
		cell7.setCellValue("总价");

		for (int i = 0; i < store_list.size(); i++) {
			PageData store = store_list.get(i);
			HSSFRow rowN = sheet.createRow(i + 1); // 第i+1行
			HSSFCell c1 = rowN.createCell(0);
			c1.setCellStyle(style);
			c1.setCellValue(store.getString("store_name_cn"));

			HSSFCell c2 = rowN.createCell(1);
			c2.setCellStyle(style);
			c2.setCellValue(store.getString("create_time"));

			HSSFCell c3 = rowN.createCell(2);
			c3.setCellStyle(style);
			c3.setCellValue(store.getString("order_id"));

			List<PageData> goods_list = (List<PageData>) store.get("list_foods");

			String goods_name = "";
			String quantity = "";
			String price = "";
			for (int j = 0; j < goods_list.size(); j++) {
				goods_name += goods_list.get(j).get("foods_name_cn");
				quantity += goods_list.get(j).get("quantity");
				price += goods_list.get(j).get("cost_price");
				if (j != goods_list.size() - 1) {
					goods_name += "\r\n";
					quantity += "\r\n";
					price += "\r\n";
				}
			}

			HSSFCell c4 = rowN.createCell(3);
			c4.setCellStyle(style);
			c4.setCellValue(new HSSFRichTextString(goods_name));

			HSSFCell c5 = rowN.createCell(4);
			c5.setCellStyle(style);
			c5.setCellValue(new HSSFRichTextString(quantity));

			HSSFCell c6 = rowN.createCell(5);
			c6.setCellStyle(style);
			c6.setCellValue(new HSSFRichTextString(price));
			
			HSSFCell c7 = rowN.createCell(6);
			c7.setCellStyle(style);
			c7.setCellValue(((BigDecimal)store.get("cost_money")).toString());
			
		}
		try {
			OutputStream out = resp.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e1) {
			logger.info("=====导出excel异常====");
		}
	}

	/**
	 * 商家流水 查看详细菜单
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/caidan")
	public ModelAndView caidan() throws Exception {
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		List<PageData> list = businessService.getCaidan(pd); // 根据ID读取
		mv.setViewName("com/business/caidan");
		mv.addObject("varList", list);
		mv.addObject("msg", "edit");
		mv.addObject("pd", pd);
		return mv;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(format, true));
	}

	
}
