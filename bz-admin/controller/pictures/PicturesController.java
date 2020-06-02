package com.flc.controller.pictures;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.enums.KEYEnum;
import com.flc.util.DateUtil;
import com.flc.util.Jurisdiction;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

/** 
 * 类名称：图片管理
 * 创建人：FH Q313596790
 * 创建时间：2015-03-21
 */
@Controller
@RequestMapping(value="/pictures")
public class PicturesController extends BaseController {
	
//	@Value("${upload.max}")
	private String imageMax = "2147483648";//上传图片最大体积
	@Value("${upload.imagePath}")
	private String imagePath;//上传图片保存路径
	@Value("${upload.requestPath}")
	private String requestPath;//上传图片的外部访问路径
	@Value("${upload.width}")
	private String imageWidth;//上传图片的最大宽度
	/**
	 * 保存文件名前缀
	 */
	private final String PREFIXION = "BK";
	private final String ANYFILE = KEYEnum.ANYFILE_PATH.getCode();//非图片上传保存路径
	private final String MOVIE = KEYEnum.MOVIE_PATH.getCode();//视频文件
	
	@RequestMapping(value="/uploadimage")
	@ResponseBody
	public Object upload(@RequestParam("file") CommonsMultipartFile file,  
	        HttpServletRequest request){
		
		 try {
			 
			// 定义解析器去解析request  
			CommonsMultipartResolver mutilpartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());  
			//request如果是Multipart类型、  
			if (mutilpartResolver.isMultipart(request)) {  
			    //强转成 MultipartHttpServletRequest  
			    MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
			    //获取文件名  
			    Iterator<String> it = multiRequest.getFileNames();  
			    while (it.hasNext()) {  
			        //获取MultipartFile类型文件  
			        MultipartFile fileDetail = multiRequest.getFile(it.next());
			        if(fileDetail.getSize()>getMax()){
			        	return getResultMap(ExEnum.EX_UPLOAD_IMAGE_MAX.getCode(), ExEnum.EX_SUCCESS.getDes(), null);
			        }
			        else if (fileDetail != null) { 
			        	//判断文件类型
			        	String fileType = fileDetail.getName();
			        	//文件流转图片
						InputStream is = fileDetail.getInputStream();
			            BufferedImage im=ImageIO.read(is);
						/* 原始图像的宽度和高度 */  
				        int width = im.getWidth();  
				        int height = im.getHeight();
				        
				        /**处理图片压缩倍率*/
				        float resizeTimesWidth = getWidth(request.getParameter("width"))>width?1:((float)getWidth(request.getParameter("width"))/(float)width);  /*压缩倍率,如果上传的图片宽度较设定的值要大，则要按比例压缩*/
				        float resizeTimesheight = getWidth(request.getParameter("height"))>height?1:((float)getWidth(request.getParameter("height"))/(float)height);  /*压缩倍率,如果上传的图片高度较设定的值要大，则要按比例压缩*/
				        
				        //判断到底使用哪一个压缩倍率，逻辑是如果上传图片的宽与设置的宽比例结果要大于等于上传图片的高与设置的高比例结果，那么压缩倍率则使用宽度的压缩比，否则使用高度的压缩比
				        float resizeTimes = resizeTimesWidth<=resizeTimesheight?resizeTimesWidth:resizeTimesheight;
				        
				        /* 调整后的图片的宽度和高度 */  
				        int toWidth = (int) (width * resizeTimes);  
				        int toHeight = (int) (height * resizeTimes);
				        /*//前台传入的图片大小格式
				        String _req_width = request.getParameter("width");
				        String _req_height = request.getParameter("height");
				        if(null!=_req_height&&!"".equals(_req_height)&&null!=_req_width&&!"".equals(_req_width)){
				        	try {
								toWidth = Integer.valueOf(_req_width);
								toHeight = Integer.valueOf(_req_height);
							} catch (Exception e) {
								//如果前台传过来的图片大小规格出了问题，则仍然用原来的规格
								toWidth = (int) (width * resizeTimes); 
								toHeight = (int) (height * resizeTimes);
							}
				        }*/
				        /* 新生成结果图片 */
				        BufferedImage result = new BufferedImage(toWidth, toHeight,BufferedImage.TYPE_INT_RGB);
				        result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight,Image.SCALE_SMOOTH), 0, 0, null);

						Calendar cal = new GregorianCalendar();
						cal.setTime(new Date());
						String year = String.valueOf(cal.get(cal.YEAR));
						String month = String.valueOf(cal.get(cal.MONTH) + 1);
						String day = String.valueOf(cal.get(cal.DATE));
						String uploadPath = "/" + year + "/" + month + "/"+day+"/";
						String path = getImagePath() + "/" + year + "/" + month + "/"+day+"/"; // 文件类型
						String FileType = "jpg";
						// 文件名称
						String fileName = PREFIXION+String.valueOf(new Date().getTime()) + "." + FileType;

						File targetFile = new File(path, fileName);
						if (!targetFile.exists()) {
							targetFile.mkdirs();
						}

						//保存图片
						ImageIO.write(result, "jpg", targetFile);

						// 隐藏域path
						String resultPath = getRequestPath() + uploadPath + fileName;
						// 显示图片图片路径
						String showPath = getRequestPath() + uploadPath + fileName;
						JSONObject resJson = new JSONObject();
						resJson.put("status", true);
						resJson.put("path", resultPath);
						resJson.put("showImg", showPath);
						logger.info("上传文件信息>>>："+fileName+"，此文件被存储在["+path+"]目录下。上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
						return getResultMap(ExEnum.EX_SUCCESS.getCode(), ExEnum.EX_SUCCESS.getDes(), resJson);
			        }else{
			        	logger.info("上传文件失败！上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
			        	return getResultMap(ExEnum.EX_SYSTEM_FILE.getCode(), ExEnum.EX_SYSTEM_FILE.getDes(), null);
			        }
			    }
			}
		} catch (Exception e) {
			logger.info("上传文件失败！上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
			e.printStackTrace();
		}  
		
		return getResultMap(ExEnum.EX_SYSTEM_ERROR.getCode(), ExEnum.EX_SYSTEM_ERROR.getDes(), null);
	}
	
	/**
	 * 文件上传，不压缩
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/uploadfile")
	@ResponseBody
	public Object uploadfile(@RequestParam("file") CommonsMultipartFile file,
	        HttpServletRequest request){
		
		 try {
			 
			// 定义解析器去解析request  
			CommonsMultipartResolver mutilpartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());  
			//request如果是Multipart类型、  
			if (mutilpartResolver.isMultipart(request)) {  
			    //强转成 MultipartHttpServletRequest  
			    MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
			    //获取文件名  
			    Iterator<String> it = multiRequest.getFileNames();  
			    while (it.hasNext()) {  
			        //获取MultipartFile类型文件  
			        MultipartFile fileDetail = multiRequest.getFile(it.next());
			        if (fileDetail != null) { 
			        	//判断文件类型
			        	String fileType = fileDetail.getOriginalFilename();
			        	String[] fileNameArr = fileType.split("\\.");
			        	if(fileNameArr.length<2){
			        		return getResultMap(ExEnum.EX_UPLOAD_FILE_NAME_IS_NOT_CRITERION.getCode(), ExEnum.EX_UPLOAD_FILE_NAME_IS_NOT_CRITERION.getDes(), null);
			        	}
			        	fileType = fileNameArr[fileNameArr.length-1];//获取上传文件的后缀
			        	//文件流
						InputStream is = fileDetail.getInputStream();

						Calendar cal = new GregorianCalendar();
						cal.setTime(new Date());
						String year = String.valueOf(cal.get(cal.YEAR));
						String month = String.valueOf(cal.get(cal.MONTH) + 1);
						String day = String.valueOf(cal.get(cal.DATE));
						
						String uploadPath = "/" + year + "/" + month + "/"+day+"/";
						if(
								fileType.indexOf("png")<0||
								fileType.indexOf("gif")<0||
								fileType.indexOf("jpg")<0||
								fileType.indexOf("bmp")<0
								){//如果上传文件为非图片文件则另存到anyfile目录
							uploadPath = ANYFILE;
						}
						
						String path = getImagePath() +uploadPath; // 文件路径
						// 文件名称
						String fileName = String.valueOf(new Date().getTime()) + "." + fileType;
						String req_type_name = request.getParameter("types");
						if(null!=req_type_name&&!"".equals(req_type_name)){//判断是否上传types属性，如果存在，则文件命名方式为传过来的名字
							uploadPath = ANYFILE;
							fileName = req_type_name+"."+fileType;
						}

						File targetFile = new File(path);
						if (!targetFile.exists()) {
							targetFile.mkdirs();
						}

						//保存文件
						saveToFile(path+fileName,is);
						// 数据库存储路径
						String resultPath = getRequestPath() + uploadPath + fileName;
						// 外网访问路径
						String showPath = getRequestPath() + uploadPath + fileName;
						JSONObject resJson = new JSONObject();
						resJson.put("status", true);
						resJson.put("path", resultPath);
						resJson.put("showImg", showPath);
						logger.info("上传文件信息>>>："+fileName+"，此文件被存储在["+path+"]目录下。上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
						return getResultMap(ExEnum.EX_SUCCESS.getCode(), ExEnum.EX_SUCCESS.getDes(), resJson);
			        }else{
			        	logger.info("上传文件失败。上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
			        	return getResultMap(ExEnum.EX_SYSTEM_FILE.getCode(), ExEnum.EX_SYSTEM_FILE.getDes(), null);
			        }
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("上传文件失败。上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
		}  
		
		return getResultMap(ExEnum.EX_SYSTEM_ERROR.getCode(), ExEnum.EX_SYSTEM_ERROR.getDes(), null);
	}
	
	/**
	 * 视频文件上传，不压缩
	 * @param file
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/uploadmovie")
	@ResponseBody
	public Object uploadmovie(@RequestParam("file") CommonsMultipartFile file,
	        HttpServletRequest request){
		
		 try {
			 
			// 定义解析器去解析request  
			CommonsMultipartResolver mutilpartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());  
			//request如果是Multipart类型、  
			if (mutilpartResolver.isMultipart(request)) {  
			    //强转成 MultipartHttpServletRequest  
			    MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;  
			    //获取文件名  
			    Iterator<String> it = multiRequest.getFileNames();  
			    while (it.hasNext()) {  
			        //获取MultipartFile类型文件  
			        MultipartFile fileDetail = multiRequest.getFile(it.next());
			        if (fileDetail != null) { 
			        	//判断文件类型
			        	String fileType = fileDetail.getOriginalFilename();
			        	String[] fileNameArr = fileType.split("\\.");
			        	if(fileNameArr.length<2){
			        		return getResultMap(ExEnum.EX_UPLOAD_FILE_NAME_IS_NOT_CRITERION.getCode(), ExEnum.EX_UPLOAD_FILE_NAME_IS_NOT_CRITERION.getDes(), null);
			        	}
			        	fileType = fileNameArr[fileNameArr.length-1];//获取上传文件的后缀
			        	//文件流
						InputStream is = fileDetail.getInputStream();

						Calendar cal = new GregorianCalendar();
						cal.setTime(new Date());
						String year = String.valueOf(cal.get(cal.YEAR));
						String month = String.valueOf(cal.get(cal.MONTH) + 1);
						String day = String.valueOf(cal.get(cal.DATE));
//						String uploadPath = "/" + year + "/" + month + "/"+day+"/";
						String path = getImagePath() + MOVIE; // 文件类型
						// 文件名称
						String fileName = String.valueOf(new Date().getTime()) + "." + fileType;
						File targetFile = new File(path);
						if (!targetFile.exists()) {
							targetFile.mkdirs();
						}

						//保存文件流到文件
						saveToFile(path+fileName,is);
						// 数据库存储路径
						String resultPath = getRequestPath() + MOVIE + fileName;
						// 外网访问路径
						String showPath = getRequestPath() + MOVIE + fileName;
						JSONObject resJson = new JSONObject();
						resJson.put("status", true);
						resJson.put("path", resultPath);
						resJson.put("showImg", showPath);
						logger.info("上传文件信息>>>："+fileName+"，此文件被存储在["+path+"]目录下。上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
						return getResultMap(ExEnum.EX_SUCCESS.getCode(), ExEnum.EX_SUCCESS.getDes(), resJson);
			        }else{
			        	logger.info("上传文件失败!上传上期:"+DateUtil.getAfterDayDate("0")+",上传者："+Jurisdiction.getUsername()+",上传者IP:"+getIpAddr(request));
			        	return getResultMap(ExEnum.EX_SYSTEM_FILE.getCode(), ExEnum.EX_SYSTEM_FILE.getDes(), null);
			        }
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
		return getResultMap(ExEnum.EX_SYSTEM_ERROR.getCode(), ExEnum.EX_SYSTEM_ERROR.getDes(), null);
	}
	
	/**
	 * 保存文件流到文件
	 * @param fileName
	 * @param in
	 * @throws IOException
	 */
	private void saveToFile(String fileName, InputStream in) throws IOException {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		bis = new BufferedInputStream(in);
		fos = new FileOutputStream(fileName);
		
		 //保存文件
		while ((size = bis.read(buf)) != -1)
			fos.write(buf, 0, size);
		fos.close();
		bis.close();
	}

	private long getMax() {
		return Long.parseLong(this.imageMax);
	}

	private String getImagePath() {
		return this.imagePath;
	}

	private String getRequestPath() {
		return this.requestPath;
	}

	private int getWidth(String width) {
		if(null!=width){
			try {
				return Integer.parseInt(width);
			} catch (Exception e) {
				logger.info("上传图片时带的图片最大宽度转换为数字失败,参数width:"+width);
				e.printStackTrace();
			}
		}
		return Integer.parseInt(this.imageWidth);
	}
	
	/**
	 * 获取IP地址
	 * @param request
	 * @return
	 */
	public  String getIpAddr(HttpServletRequest request)  {
	    String ip  =  request.getHeader( " x-forwarded-for " );
	     if (ip  ==   null   ||  ip.length()  ==   0   ||   " unknown " .equalsIgnoreCase(ip))  {
	        ip  =  request.getHeader( " Proxy-Client-IP " );
	    } 
	     if (ip  ==   null   ||  ip.length()  ==   0   ||   " unknown " .equalsIgnoreCase(ip))  {
	        ip  =  request.getHeader( " WL-Proxy-Client-IP " );
	    } 
	     if (ip  ==   null   ||  ip.length()  ==   0   ||   " unknown " .equalsIgnoreCase(ip))  {
	        ip  =  request.getRemoteAddr();
	    } 
	     return  ip;
	}
}

