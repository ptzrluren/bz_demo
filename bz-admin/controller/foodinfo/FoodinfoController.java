package com.flc.controller.foodinfo;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.entity.system.User;
import com.flc.service.foodinfo.FoodinfoManager;
import com.flc.service.foodinfo.FoodspecManager;
import com.flc.service.foodinfo.FoodtypeManager;
import com.flc.util.AppUtil;
import com.flc.util.Const;
import com.flc.util.Jurisdiction;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 说明：商家商品详情
 * 创建人：FLC
 * 创建时间：2017-08-17
 */
@Controller
@RequestMapping(value="/foodinfo")
public class FoodinfoController extends BaseController {
	
	String menuUrl = "foodinfo/list.do"; //菜单地址(权限用)
	@Resource(name="foodinfoService")
	private FoodinfoManager foodinfoService;
	
	@Autowired
	private FoodtypeManager specClassService;
	@Autowired
	private FoodspecManager specService;
	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	/**
	 * 修改商品状态实现上下架商品
	 * @throws Exception
	 */
	@RequestMapping(value="/updateshelves")
	public String updateShelves() throws Exception{
		PageData pd = null;
		pd = this.getPageData();
		foodinfoService.updateShelves(pd);
		return "redirect:/foodinfo/list.do?STORE_ID="+pd.get("STORE_ID");
	}
	
	/**保存
	 * @param
	 * @throws Exception
	 */
	@RequestMapping(value="/save")
	public ModelAndView save() throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"新增Foods");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "add")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		pd.put("FOODS_ID", this.get32UUID());	//主键
		foodinfoService.save(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
		this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
		return mv;
	}
	
	/**删除
	 * @param out
	 * @throws Exception
	 */
	@RequestMapping(value="/delete")
	public void delete(PrintWriter out) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"删除Foods");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return;} //校验权限
		PageData pd = new PageData();
		pd = this.getPageData();
		foodinfoService.delete(pd);
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
		logBefore(logger, Jurisdiction.getUsername()+"修改Foodinfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "edit")){return null;} //校验权限
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		foodinfoService.edit(pd);
		mv.addObject("msg","success");
		mv.setViewName("save_result");
//		this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
		return mv;
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@RequestMapping(value="/list")
	public ModelAndView list(Page page) throws Exception{
		logBefore(logger, Jurisdiction.getUsername()+"列表Foodinfo");
		//if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;} //校验权限(无权查看时页面会有提示,如果不注释掉这句代码就无法进入列表页面,所以根据情况是否加入本句代码)
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		String keywords = pd.getString("keywords");				//关键词检索条件
		if(null != keywords && !"".equals(keywords)){
			pd.put("keywords", keywords.trim());
		}
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 商家上下架权限
		}
		
		String store_id = pd.getString("STORE_ID");
		if(!StringUtils.isEmpty(store_id) && !"null".equalsIgnoreCase(store_id)){
			this.getRequest().getSession().setAttribute("STORE_ID", pd.getString("STORE_ID"));
		}
		
		String STORE_ID = (String)this.getRequest().getSession().getAttribute("STORE_ID");
		if(!StringUtils.isEmpty(STORE_ID)) {
			pd.put("STORE_ID", STORE_ID);
		}
		page.setPd(pd);
		List<PageData>	varList = foodinfoService.list(page);	//列出Foodinfo列表
		mv.setViewName("foods/foodinfo/foodinfo_list");
		mv.addObject("varList", varList);
		mv.addObject("pd", pd);
		mv.addObject("STORE_ID",pd.getString("STORE_ID"));
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
		List<PageData> foodList = foodinfoService.findFoodType(pd);
		mv.setViewName("foods/foodinfo/foodinfo_edit");
		mv.addObject("foodList", foodList);
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
		pd = foodinfoService.findById(pd);	//根据ID读取
		List<PageData> foodList = foodinfoService.findFoodType(pd);
		
		//add by liubin 加入商户限制只能看自己的店
		User user = (User)this.getRequest().getSession().getAttribute(Const.SESSION_USER);
		if(user.getROLE_ID().equals(Const.STROE_ADMIN_ROLE_ID)) {
			mv.addObject("SXJ", 0); // 商家上下架权限
		}
		
		mv.setViewName("foods/foodinfo/foodinfo_edit");
		mv.addObject("foodList", foodList);
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
		logBefore(logger, Jurisdiction.getUsername()+"批量删除Foodinfo");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "del")){return null;} //校验权限
		PageData pd = new PageData();		
		Map<String,Object> map = new HashMap<String,Object>();
		pd = this.getPageData();
		List<PageData> pdList = new ArrayList<PageData>();
		String DATA_IDS = pd.getString("DATA_IDS");
		if(null != DATA_IDS && !"".equals(DATA_IDS)){
			String ArrayDATA_IDS[] = DATA_IDS.split(",");
			foodinfoService.deleteAll(ArrayDATA_IDS);
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
		logBefore(logger, Jurisdiction.getUsername()+"导出Foodinfo到excel");
		if(!Jurisdiction.buttonJurisdiction(menuUrl, "cha")){return null;}
		ModelAndView mv = new ModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> dataMap = new HashMap<String,Object>();
		List<String> titles = new ArrayList<String>();
		titles.add("商品编号");	//1
		titles.add("商品中文名称");	//2
		titles.add("商品英文名字");	//3
		titles.add("商品中文图片");	//4
		titles.add("商品英文名字");	//5
		titles.add("中文名排序");	//6
		titles.add("英文名称排序");	//7
		titles.add("价格");	//8
		titles.add("商家");	//9
		titles.add("商品类型");	//10
		titles.add("商品创建时间");	//11
		titles.add("是否上架");	//12
		dataMap.put("titles", titles);
		List<PageData> varOList = foodinfoService.listAll(pd);
		List<PageData> varList = new ArrayList<PageData>();
		for(int i=0;i<varOList.size();i++){
			PageData vpd = new PageData();
			vpd.put("var1", varOList.get(i).getString("FOODS_ID"));	    //1
			vpd.put("var2", varOList.get(i).getString("FOODS_NAME_CN"));	    //2
			vpd.put("var3", varOList.get(i).getString("FOODS_NAME_EN"));	    //3
			vpd.put("var4", varOList.get(i).getString("FOODS_IMAGE_CN"));	    //4
			vpd.put("var5", varOList.get(i).getString("FOODS_IMAGE_EN"));	    //5
			vpd.put("var6", varOList.get(i).getString("FOODS_DESC_CN"));	    //6
			vpd.put("var7", varOList.get(i).getString("FOODS_DESC_EN"));	    //7
			vpd.put("var8", varOList.get(i).getString("PRICE"));	    //8
			vpd.put("var9", varOList.get(i).getString("STORE_ID"));	    //9
			vpd.put("var10", varOList.get(i).getString("TYPE_ID"));	    //10
			vpd.put("var11", varOList.get(i).getString("CREATE_TIME"));	    //11
			vpd.put("var12", varOList.get(i).getString("UP_FRAME"));	    //12
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
	 * 跳转到商品的价格编辑页面
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/toEditPrice")
	public ModelAndView toEditPrice()throws Exception{
		ModelAndView mv = this.getModelAndView();
		PageData pd = new PageData();
		pd = this.getPageData();
		Map<String,Object> map = new HashMap<String,Object>();
		String foods_id = pd.getString("foods_id");
//		List<PageData> rows = new ArrayList<PageData>();
		//商品规格分类
		List<PageData> spec_class_list = specClassService.listAll(pd);
		
		if(null == spec_class_list || spec_class_list.size() == 0){
			mv.setViewName("foods/foodinfo/foods_price");
			mv.addObject("rows", null);
			mv.addObject("head", null);
			mv.addObject("foods_id", pd.getString("foods_id"));
			mv.addObject("msg", "save");
			mv.addObject("pd", pd);
			return mv;
		}
		
		//商品规格列表  按照规格分类的顺序状态list里面 方便操作
		List<List<PageData>> list = new ArrayList<List<PageData>>();
		//计算总行数
		int total = 1;
		for (int i = 0; i < spec_class_list.size(); i++) {
			//查询每个规格分类下面详细的规格
			PageData ppd = new PageData();
			ppd.put("spec_class_id", spec_class_list.get(i).getString("SPEC_CLASS_ID"));
			List<PageData> spec_list = specService.listAll(ppd);
			total = total * spec_list.size();   //总数量
			spec_class_list.get(i).put("spec_list", spec_list);
			list.add(spec_list);  
		}
				//表格的标题
		List<PageData> tb_head = new ArrayList<PageData>();
		for (int i = 0; i < spec_class_list.size(); i++) {
			PageData cell = new PageData();
			cell.put("name", spec_class_list.get(i).getString("SPEC_CLASS_NAME_CN"));
			cell.put("value", spec_class_list.get(i).getString("SPEC_CLASS_ID"));
			tb_head.add(cell);
		}
				
		//空的表格数据
		List<List<PageData>> rows = new ArrayList<List<PageData>>();
		for (int i = 0; i < total; i++) {
			List<PageData> row = new ArrayList<PageData>();
			rows.add(row);
		}
		int index = 0;   //index 从1开始
				
		//将规格数据转换成tb格式
		createTb(rows, total, list, index);
		
		List<PageData> prices = new ArrayList<PageData>();
		
		//根据rows 查询价格
		for (int m = 0; m < rows.size(); m++) {
			List<PageData> row = rows.get(m);
			List<PageData> row_paiux = new ArrayList<PageData>();
			row_paiux.addAll(row);
			for (int i = 0; i < row_paiux.size(); i++) {  
                for(int j=0;j<row_paiux.size()-1;j++){  
                    if((int)row_paiux.get(i).get("value")<(int)row_paiux.get(j).get("value")){  
                        PageData r=row_paiux.get(i);  
                        row_paiux.set(i, row_paiux.get(j));  
                        row_paiux.set(j, r);  
                    }  
                }  
            } 
			
			//拼接ids
			StringBuffer sbf = new StringBuffer();
			for (int i = 0; i < row_paiux.size(); i++) {
				sbf.append(row_paiux.get(i).get("value"));
				if(i != row_paiux.size() - 1){
					sbf.append(",");
				}
			}
			PageData pricepd = new PageData();
			pricepd.put("spec_id_list", sbf.toString());
			pricepd.put("foods_id", foods_id);
			Double price = specClassService.getFoodsSpecPrice(pricepd);
			PageData pc = new PageData();
			pc.put("price", price);
			pc.put("spec_id_list", sbf.toString());
			prices.add(pc);
		}
		
		
		mv.setViewName("foods/foodinfo/foods_price");
		mv.addObject("rows", rows);
		mv.addObject("prices", prices);
		mv.addObject("head", tb_head);
		mv.addObject("foods_id", pd.getString("foods_id"));
		mv.addObject("msg", "save");
		mv.addObject("pd", pd);
		return mv;
	}	
	
	
	
	
	@RequestMapping(value="/saveSpecPrice")
	@ResponseBody
	public Object saveSpecPrice() throws Exception{
		PageData pd = this.getPageData();	
		Map<String,Object> map = new HashMap<String,Object>();
		
		String price_list_str = pd.getString("price_list");
		String foods_id = pd.getString("foods_id");
		
		try {
			List<Map<String, Object>> price_list = (List<Map<String, Object>>)JSON.parse(price_list_str);
			//删除该商品之前所有的规格价格信息
			PageData fpd = new PageData();
			fpd.put("foods_id", foods_id);
			specClassService.delSpecPrice(fpd);
			
			//查询新的价格信息
			specClassService.savePriceList(price_list);
			map.put("status", "10001");//成功
			map.put("message", "保存成功");//成功
		} catch (Exception e) {
			map.put("status", "10000");//失败
			map.put("message", "保存失败");//成功
		}
		
		return AppUtil.returnObject(pd, map);
	}
	
	/**查询foods价格
	 * @param
	 * @throws Exception
	 */
//	@RequestMapping(value="/getFoodsPriceList")
//	@ResponseBody
//	public Object getFoodsPriceList() throws Exception{
//		PageData pd = this.getPageData();	
//		Map<String,Object> map = new HashMap<String,Object>();
////		List<PageData> rows = new ArrayList<PageData>();
//		//商品规格分类
//		List<PageData> spec_class_list = specClassService.listAll(pd);
//		
//		//商品规格列表  按照规格分类的顺序状态list里面 方便操作
//		List<List<PageData>> list = new ArrayList<List<PageData>>();
//		//计算总行数
//		int total = 1;
//		for (int i = 0; i < spec_class_list.size(); i++) {
//			//查询每个规格分类下面详细的规格
//			PageData ppd = new PageData();
//			ppd.put("spec_class_id", spec_class_list.get(i).getString("SPEC_CLASS_ID"));
//			List<PageData> spec_list = specService.listAll(ppd);
//			total = total * spec_list.size();   //总数量
//			spec_class_list.get(i).put("spec_list", spec_list);
//			list.add(spec_list);  
//		}
//		//表格的标题
//		List<PageData> tb_head = new ArrayList<PageData>();
//		for (int i = 0; i < spec_class_list.size(); i++) {
//			PageData cell = new PageData();
//			cell.put("name", spec_class_list.get(i).getString("SPEC_CLASS_NAME_CN"));
//			cell.put("value", spec_class_list.get(i).getString("SPEC_CLASS_ID"));
//			tb_head.add(cell);
//		}
//		
//		//空的表格数据
//		List<List<PageData>> rows = new ArrayList<List<PageData>>();
//		for (int i = 0; i < total; i++) {
//			List<PageData> row = new ArrayList<PageData>();
//			rows.add(row);
//		}
//		int index = 0;   //index 从1开始
//		
//		createTb(rows, total, list, index);
//		map.put("head", tb_head);
//		map.put("rows", rows);
//		return AppUtil.returnObject(pd, map);
//	}
	
	
	private void createTb(List<List<PageData>> rows, int total, List<List<PageData>> spec_detail, int index){
		//每一列数据循环规格
		//total / （自己包括前面所有的spec数据的size的积 ）   的循环次数
		if(index == spec_detail.size() ){
			return;
		}
		
		System.out.println("index---------" + index);
		int mm = 1; 
		
		//本层每个spec循环的次数
		for (int i = 0; i < index + 1; i++) {
			mm = mm * spec_detail.get(i).size();
		}
		
		int count = total/mm;
		
		
		//循环次数
		int quotient = total / (spec_detail.get(index).size() * count);
		
		int loop_index = 0;   //行数
		
		//总体循环次数
		for (int i = 0; i < quotient; i++) {
			//当前列对于的spec_class 中的spec_list
			for (int j = 0; j < spec_detail.get(index).size(); j++) {
				PageData spec = spec_detail.get(index).get(j);
				PageData cell = new PageData();
				cell.put("name", spec.getString("SPEC_NAME_CN"));
				cell.put("value", spec.get("SPEC_ID"));
				//每个循环次数
				for (int k = 0; k < count; k++) {
					System.out.println("loop_index---------" + loop_index);
					rows.get(loop_index).add(cell);
					loop_index ++;
				}
			}
		}
		
		index ++;
		createTb(rows, total,  spec_detail, index);
	}
	
}
