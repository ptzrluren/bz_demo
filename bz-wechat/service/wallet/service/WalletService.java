package com.flc.service.wallet.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.flc.dao.DaoSupport;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.userwalletlog.impl.UserWalletLogService;
import com.flc.service.wallet.WalletManager;
import com.flc.util.Const;
import com.flc.util.PageData;

@Service("walletService")
public class WalletService implements WalletManager {
	
	@Resource(name =  "daoSupport")
	private DaoSupport dao;
	@Resource(name="userwalletlogService")
	private UserWalletLogService userWalletLogService;

	/**
	 * 钱包
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData getWalletPage(PageData pd) throws Exception {

		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		String token = pd.getString("token");
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE); 
		}
		
		//钱包内容:饭点,累计金额,兑换比例
		PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		//如果没有钱包,创建空的钱包
		if(null == wallet){
			dao.save("WalletMapper.saveWallet", pd);
			wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		}
		String rewardpoint = (String)dao.findForObject("WalletMapper.getWalletParam", pd);
		if(!StringUtils.isEmpty(rewardpoint)){
			wallet.put("ratio", Integer.parseInt(rewardpoint));//兑换比例
		}
		
		//可兑换的饭点
		Double money = (Double)wallet.get("money");
		if(null == money){
			money = new Double(0);
		}
		int point = Integer.parseInt(rewardpoint);
		//可兑换的饭点
		int point_add = (int)(money / point);
		wallet.put("point_add", point_add);
		PageData info =  (PageData)dao.findForObject("WalletMapper.getFandianInfo", pd);
		wallet.put("info_cn", info.getString("value"));
		wallet.put("info_en", info.getString("value_en"));
		wallet.put("min_exchange", Const.MIN_EXCHANGE);//最低兑换饭点
		return wallet;
	}

	
	/**
	 * 兑换累计金额
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@Override
	public PageData exchange(PageData pd) throws Exception {

		if(pd.get("token") == null || StringUtils.isBlank(pd.getString("token"))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		if((pd.get("phone") == null && pd.get("user_id") == null) 
				|| (StringUtils.isBlank(pd.getString("phone")) && (StringUtils.isBlank(pd.getString("user_id"))))) {
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE);
		}
		String token = pd.getString("token");
		PageData user = (PageData) this.dao.findForObject("UserMapper.findOne", pd);

		//登录失效
		if (user == null) {
			throw new NjmsException(ExEnum.EX_NO_USER);
		} else if(!token.equals(user.getString("token"))){
			throw new NjmsException(ExEnum.EX_SOURCE_NOT_ONE); 
		}
		
		//一次只能兑换20个饭点 ,如果兑换的饭点不足20个  不给兑换
		//如果超过20个,则兑换20个
		PageData wallet = (PageData)dao.findForObject("WalletMapper.getWallet", pd);
		String rewardpoint = (String)dao.findForObject("WalletMapper.getWalletParam", pd);
		//可兑换的饭点
		Double money = (Double)wallet.get("money");
		if(null == money){
			money = new Double(0);
		}
		int point = Integer.parseInt(rewardpoint);
		//可兑换的饭点
		int point_add = (int)(money / point);
		
		if(point_add < Const.MIN_EXCHANGE){
			throw new NjmsException(ExEnum.EX_POINT_ERROR);
		}
		
		PageData exchangePoint = new PageData();
		exchangePoint.put("user_id", user.getString("user_id"));
		exchangePoint.put("money", Const.MIN_EXCHANGE * point);
		userWalletLogService.save(exchangePoint, ExEnum.WL_TYPE9);

		//增加兑换记录
		pd.put("money", Const.MIN_EXCHANGE * point);  //饭点一次兑换20个,小号的金额为20*兑换比例
		pd.put("point", Const.MIN_EXCHANGE);
		dao.save("WalletMapper.addExchangeLog", pd);
		
		return new PageData();
	}

}
