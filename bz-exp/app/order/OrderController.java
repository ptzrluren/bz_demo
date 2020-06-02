package com.flc.controller.app.order;

import com.flc.controller.base.BaseController;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.order.OrderManager;
import com.flc.util.PageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value="/app/order")
public class OrderController extends BaseController {
	
	
	@Autowired
	private OrderManager orderService;
	
	/**
	 * 首页，查询有未接订单的邮编列表
	 * @return
	 */
	@RequestMapping("getOrderCodeList")
	@ResponseBody
	public Object getOrderCodeList(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.getOrderCodeList(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 首页根据邮编进入未接的订单列表
	 * @param order
	 * @return
	 */
	@RequestMapping("getOrderListByZipCode")
	@ResponseBody
	public Object getOrderListByZipCode(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.getOrderListByZipCode(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 
		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 快递员接单
	 * @param order
	 * @return
	 */
	@RequestMapping("expOrders")
	@ResponseBody
	public Object expOrders(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.expOrders(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 查询快递员的订单列表-邮编列表
	 * @param order
	 * @return
	 */
	@RequestMapping("getExpOrderCodeList")
	@ResponseBody
	public Object getExpOrderCodeList(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.getExpOrderCodeList(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 上传经纬度
	 * @param order
	 * @return
	 */
	@RequestMapping("uploadLocation")
	@ResponseBody
	public Object uploadLocation(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.uploadLocation(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	@RequestMapping("getExpOrderListByZipCode")
	@ResponseBody
	public Object getExpOrderListByZipCode(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.getExpOrderListByZipCode(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 配送员取件
	 * @param order
	 * @return
	 */
	@RequestMapping("pickupOrderFoods")
	@ResponseBody
	public Object pickupOrderFoods(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.pickupOrderFoods(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 在路上接口，将该快递员所有订单状态改成在路上
	 * @return
	 */
	@RequestMapping("onTheWay")
	@ResponseBody
	public Object onTheWay(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.onTheWay(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	/**
	 * 订单列表-地图接单
	 * @return
	 */
	@RequestMapping("getOrderList4Map")
	@ResponseBody
	public Object getOrderList4Map(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.getOrderList4Map(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 确认送达
	 * @return
	 */
	@RequestMapping("sendTo")
	@ResponseBody
	public Object sendTo(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.sendTo(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	/**
	 * 快递员接单
	 * @return
	 */
	@RequestMapping("takeOrders")
	@ResponseBody
	public Object takeOrders(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.takeOrders(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	/**
	 * 查询订单详情
	 * @return
	 */
	@RequestMapping("getOrderDetail")
	@ResponseBody
	public Object getOrderDetail(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.getOrderDetail(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
	
	/**
	 * 付款类型为现金支付的订单确认付款
	 * @return
	 */
	@RequestMapping("payOrder")
	@ResponseBody
	public Object payOrder(){
		PageData pd = null;
		PageData result = new PageData();
		try {
			pd = this.getPageData();
			result = orderService.payOrder(pd);
		} catch (NjmsException e){
			e.printStackTrace();
			return getResultMap(e.getEx(), new PageData());
		} catch (Exception e){
			e.printStackTrace();
			return getResultMap(ExEnum.EX_SYSTEM_ERROR, new PageData());
		} 

		return getResultMap(ExEnum.EX_SUCCESS, result);
	}
	
}
