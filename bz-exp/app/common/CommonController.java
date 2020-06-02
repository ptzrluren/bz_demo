package com.flc.controller.app.common;

import com.alibaba.fastjson.JSONObject;
import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


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
}