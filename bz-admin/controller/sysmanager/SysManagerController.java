package com.flc.controller.sysmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import com.alibaba.druid.support.json.JSONUtils;
import com.flc.controller.base.BaseController;
import com.flc.entity.Page;
import com.flc.service.order.OrderPartManager;
import com.flc.util.AppUtil;
import com.flc.util.ObjectExcelView;
import com.flc.util.PageData;
import com.flc.util.PrintUtil;
import com.flc.util.Jurisdiction;
import com.flc.util.Tools;

/** 
 * 说明：订单详情
 * 创建人：FLC
 * 创建时间：2017-08-22
 */
@Controller
@RequestMapping(value="/sysmanager")
public class SysManagerController extends BaseController {
	
	String menuUrl = "orderpart/list.do"; //菜单地址(权限用)
	@Resource(name="orderpartService")
	private OrderPartManager orderpartService;
	
	@RequestMapping(value = "/save")
	@ResponseBody
	public Object save() throws Exception {
		logBefore(logger, Jurisdiction.getUsername() + "新增Business");
		PageData result = new PageData();
		PageData pd = new PageData();
		pd = this.getPageData();
		
		InputStreamReader stdISR = null;  
        InputStreamReader errISR = null;  
        Process process = null;  
        String command = "/opt/script/baozou_mysql_backup.sh"; 
        try {  
            process = Runtime.getRuntime().exec(command);  
            int exitValue = process.waitFor();  
  
            String line = null;  
  
            stdISR = new InputStreamReader(process.getInputStream());  
            BufferedReader stdBR = new BufferedReader(stdISR);  
            while ((line = stdBR.readLine()) != null) {  
                System.out.println("STD line:" + line);  
            }  
  
            errISR = new InputStreamReader(process.getErrorStream());  
            BufferedReader errBR = new BufferedReader(errISR);  
            while ((line = errBR.readLine()) != null) {  
                System.out.println("ERR line:" + line);  
            }  
            result.put("state", "success");
        } catch (IOException | InterruptedException e) {  
            e.printStackTrace(); 
            result.put("state", "error");
        } finally {  
            try {  
                if (stdISR != null) {  
                    stdISR.close();  
                }  
                if (errISR != null) {  
                    errISR.close();  
                }  
                if (process != null) {  
                    process.destroy();  
                }  
            } catch (IOException e) {  
                System.out.println("正式执行命令：" + command + "有IO异常");  
                result.put("state", "error");
            }  
        }
        String url = "http://47.90.202.220:9092/backsql/baozou.sql.gz";
		return result;
	}
	
	
	public static void main(String[] args) {
        InputStreamReader stdISR = null;  
        InputStreamReader errISR = null;  
        Process process = null;  
        String command = "/opt/script/baozou_mysql_backup.sh"; 
        try {  
            process = Runtime.getRuntime().exec(command);  
            int exitValue = process.waitFor();  
  
            String line = null;  
  
            stdISR = new InputStreamReader(process.getInputStream());  
            BufferedReader stdBR = new BufferedReader(stdISR);  
            while ((line = stdBR.readLine()) != null) {  
                System.out.println("STD line:" + line);  
            }  
  
            errISR = new InputStreamReader(process.getErrorStream());  
            BufferedReader errBR = new BufferedReader(errISR);  
            while ((line = errBR.readLine()) != null) {  
                System.out.println("ERR line:" + line);  
            }  
        } catch (IOException | InterruptedException e) {  
            e.printStackTrace();  
        } finally {  
            try {  
                if (stdISR != null) {  
                    stdISR.close();  
                }  
                if (errISR != null) {  
                    errISR.close();  
                }  
                if (process != null) {  
                    process.destroy();  
                }  
            } catch (IOException e) {  
                System.out.println("正式执行命令：" + command + "有IO异常");  
            }  
        }  
    }  
}
