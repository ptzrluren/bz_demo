package com.flc.controller.foodinfo;

import com.alibaba.druid.util.StringUtils;
import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.foodinfo.StoretypeManager;
import com.flc.service.merchant.BusinessManager;
import com.flc.util.AppUtil;
import com.flc.util.ExcelUtil;
import com.flc.util.Jurisdiction;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.UuidUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 * 说明：商品类型
 * 创建人：FLC
 * 创建时间：2017-08-21
 */
@Controller
@RequestMapping(value="/storetype")
public class StoretypeController extends BaseController {
	
	String menuUrl = "storetype/list.do"; //菜单地址(权限用)
	@Resource(name="storetypeService")
	private StoretypeManager storetypeService;
	@Resource(name = "businessService")
	private BusinessManager businessService;
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Storetype");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("TYPE_ID", this.get32UUID());	//主键
		storetypeService.save(pd);
		this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
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
		logBefore(logger, Jurisdiction.getUsername()+"删除Storetype");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		storetypeService.delete(pd);
		this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
		out.write("success");
		out.close();
	}
	
	/**修改
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/edit")
	public ModelAndView edit() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"修改Storetype");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		storetypeService.edit(pd);
		this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
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
		logBefore(logger, Jurisdiction.getUsername()+"列表Storetype");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		String store_id = pd.getString("STORE_ID");
		if(!StringUtils.isEmpty(store_id)){
			this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
		}
		
		String STORE_ID = (String)this.getRequest().getSession().getAttribute("STORE_ID");
		if(!StringUtils.isEmpty(STORE_ID)){
			pd.put("STORE_ID", STORE_ID);
		}
		page.setPd(pd);
		List<PageData>	varList = storetypeService.list(page);	//列出Storetype列表
		mv.setViewName("foods/foodinfo/storetype_list");
		mv.addObject("STORE_ID",pd.getString("STORE_ID"));
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
		mv.setViewName("foods/foodinfo/storetype_edit");
		mv.addObject("STORE_ID",pd.getString("STORE_ID"));
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
		pd = storetypeService.findById(pd);	//根据ID读取
		mv.setViewName("foods/foodinfo/storetype_edit");
		mv.addObject("STORE_ID",pd.getString("STORE_ID"));
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除Storetype");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			storetypeService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出Storetype到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("商品类型编号");	//1
		titles.add("商品编号");	//2
		titles.add("商品类型名称");	//3
		titles.add("英文类型名称");	//4
		titles.add("类型编号");	//5
		dataMap.put("titles", titles);
		List<PageData> varOList = storetypeService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("TYPE_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("STORE_ID"));	    //2
			vpd.put("var3", varOList.get(i).getString("TYPE_NAME_CN"));	    //3
			vpd.put("var4", varOList.get(i).getString("TYPE_NAME_EN"));	    //4
			vpd.put("var5", varOList.get(i).getString("INDEX"));	    //5
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
	
	
	
	/**
	 * 
	 * @param file
	 * @param request
	 * @param session
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/readExcel")
	public ModelAndView readExcel(@RequestParam(value = "excelFile") MultipartFile file, 
			@RequestParam(value = "store_id") String store_id,
			@RequestParam(value = "type_id") String type_id,
			HttpServletRequest request,
			HttpSession session) throws IOException {
		ModelAndView mv = new ModelAndView();
		PageData pd = this.getPageData();
		// 判断文件是否为空
		if (file == null) {
			mv.addObject("msg", "failed");
			mv.setViewName("excel_result");
			return mv;
		}
		String name = file.getOriginalFilename();
		long size = file.getSize();
		if (name == null || ExcelUtil.EMPTY.equals(name) && size == 0) {
			mv.addObject("msg", "failed");
			mv.setViewName("excel_result");
			return mv;
		}
		// 读取Excel数据到List中
		List<PageData> list = new ArrayList<PageData>();
		//读取excel   固定格式 第一行标题 数据从第二行开始
		//第一列 商品中文名，第二列商品英文名，第三列价格
		 List<ArrayList<String>> foods_list = new ArrayList<ArrayList<String>>();  
	     // IO流读取文件  
	     InputStream input = null;  
	     XSSFWorkbook wb = null;  
	     List<PageData>  rowList = new ArrayList<PageData>();  
	     try {  
	    	 input = file.getInputStream();  
	    	 // 创建文档  
	    	 wb = new XSSFWorkbook(input);                         
	    	 XSSFSheet xssfSheet = wb.getSheetAt(0);  
	    	 int totalRows = xssfSheet.getLastRowNum();                
	    	 //读取Row,从第二行开始  
	    	 for(int rowNum = 1;rowNum <= totalRows;rowNum++){  
	    		 XSSFRow xssfRow = xssfSheet.getRow(rowNum);  
	    		 if(xssfRow!=null){  
	    			
	    			 PageData row = new PageData();
	    			 
	    			 XSSFCell cell1 = xssfRow.getCell(0);//中文菜名
	    			 XSSFCell cell2 = xssfRow.getCell(1);//英文菜名
	    			 XSSFCell cell3 = xssfRow.getCell(2);//价格
	    			 String foods_name_cn = cell1.getStringCellValue();
	    			 String foods_name_en = cell2.getStringCellValue();
	    			 double price = cell3.getNumericCellValue();
	    			 String foods_id = UuidUtil.get32UUID();
	    			 row.put("foods_id", foods_id);
	    			 row.put("foods_name_cn", foods_name_cn);
	    			 row.put("foods_name_en", foods_name_en);
	    			 row.put("price", price);
	    			 row.put("store_id", store_id);
	    			 row.put("type_id", type_id);
	    			 rowList.add(row);
	    		 }
	    	 }
	    	 businessService.exportExcel(rowList);
	    	 mv.addObject("msg","success");
	     }catch (Exception e){
	    	 e.printStackTrace();
	    	 logger.debug("导入excel异常：" + e);
	    	 mv.addObject("msg","error");
	     }
	     
		mv.setViewName("save_result1");
		return mv;
	}
}
