package com.flc.controller.app.common;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.flc.config.Constant;
import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.uploadimg.UploadImgManager;
import com.flc.util.PageData;


/**
 * 文件上传基础类
 * 
 * @author RUING
 *
 */
@Controller
@RequestMapping("/app/common")
public class CommonController extends  BaseController {

	@Value("${upload.imagePath}")
	private String imagePath;

	
	@Value("${upload.requestPath}")
	private String requestPath;
	
	@Autowired
	private UploadImgManager uploadImgService;
	

	private static Logger log = Logger.getLogger(CommonController.class);

//	@SuppressWarnings("static-access")
	@RequestMapping("/upload")
	@ResponseBody
	public Object upload(
			@RequestParam(value = "file", required = false) MultipartFile file,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		// System.out.println(imagePath);

		
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		String year = String.valueOf(cal.get(cal.YEAR));
		String month = String.valueOf(cal.get(cal.MONTH) + 1);
		String uploadPath = "/" + year + "/" + month + "/";
		// 存放的相对路径
		// String path =
		// request.getSession().getServletContext().getRealPath(uploadPath);
		String path = imagePath + "/" + year + "/" + month + "/";
		String fileName = file.getOriginalFilename();
		// 文件类型
		String FileType = fileName.substring(fileName.lastIndexOf('.') + 1,
				fileName.length());
		// 文件名称
		String timestr = String.valueOf(new Date().getTime());
		fileName = timestr + "." + FileType;
		String xiaotu = timestr + "." + FileType;

		File targetFile = new File(path, fileName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}

		boolean success = true;
		// 保存
		try {
			file.transferTo(targetFile);
			long size = file.getSize();
		} catch (Exception e) {
			log.error("文件上传异常", e);
			e.printStackTrace();
		}

//		String resultPath = requestPath + uploadPath + fileName;
//		// 显示图片图片路劲
//		String showPath = requestPath + uploadPath + fileName;
		JSONObject result = new JSONObject();
		result.put("status", success);
		result.put("prefixPath", requestPath);
		result.put("showImgPath", uploadPath + xiaotu);
		result.put("fullPath", requestPath + uploadPath + xiaotu);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", ExEnum.EX_SUCCESS.getCode());
		map.put("message", ExEnum.EX_SUCCESS.getDes_cn());
		map.put("data", result);

		return map;
	}
	
	/**
	 * 文件上传通用工具类
	 * @param file					上传文件
	 * @param table_name			文件主表名称
	 * @param foreign_key_id		文件主表主键
	 * @param field_order			文件主表字段标记
	 * @param sort					文件排序
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/uploadNew")
	@ResponseBody
	public Object uploadNew(
			@RequestParam(value = "file", required = false) MultipartFile file, 
			@RequestParam(value = "table_name", required = false) String table_name,
			@RequestParam(value = "foreign_key_id", required = false) String foreign_key_id, 
			@RequestParam(value = "field_order", required = false) String field_order, 
			@RequestParam(value = "sort", required = false) String sort, 
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if(file == null || StringUtils.isBlank(table_name) || StringUtils.isBlank(foreign_key_id)) {
			return getResultMap(ExEnum.EX_PARAM_NULL, new PageData());
		}
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		String year = String.valueOf(cal.get(cal.YEAR));
		String month = String.valueOf(cal.get(cal.MONTH) + 1);
		String uploadPath = "/" + year + "/" + month + "/";
		// 存放的相对路径
		String path = imagePath + "/" + year + "/" + month + "/";
		String fileName = file.getOriginalFilename();
		// 文件类型
		String FileType = fileName.substring(fileName.lastIndexOf('.') + 1,
				fileName.length());
		// 文件名称
		String timestr = String.valueOf(new Date().getTime());
		fileName = timestr + "." + FileType;
		File targetFile = new File(path, fileName);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		// 保存文件
		try {
			file.transferTo(targetFile);
			file.getSize();
		} catch (Exception e) {
			log.error("文件上传异常", e);
			e.printStackTrace();
		}
		
		//文件路径等信息入库
		PageData pd = new PageData();
		pd.put("path", uploadPath + fileName);
		pd.put("table_name", table_name);
		pd.put("foreign_key_id", foreign_key_id);
		pd.put("field_order", field_order);
		pd.put("sort", sort);
		try {
			int i = uploadImgService.save(pd);
			if(i != 1) {
				return getResultMap(ExEnum.EX_IP_ADDRESS_LOCALHOST, new PageData());
			}
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		}
		
		//返回json数据
		JSONObject json = new JSONObject();
		json.put("file_name", fileName);
		json.put("path", requestPath+pd.get("path"));
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", ExEnum.EX_SUCCESS.getCode());
		map.put("msg_cn", ExEnum.EX_SUCCESS.getDes_cn());
		map.put("msg_en", ExEnum.EX_SUCCESS.getDes_en());
		map.put("data", json);
		return map;
	}
	
}