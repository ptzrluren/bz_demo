package com.flc.service.userwalletlog.impl;

import com.flc.dao.DaoSupport;
import com.flc.entity.Page;
import com.flc.enums.ExEnum;
import com.flc.exception.NjmsException;
import com.flc.service.userwalletlog.UserWalletLogManager;
import com.flc.util.Const;
import com.flc.util.PageData;
import com.flc.util.UuidUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/** 
 * 说明： 饭点日志
 * 创建人：FLC
 * 创建时间：2018-11-28
 * @version
 */
@Service("userwalletlogService")
public class UserWalletLogService implements UserWalletLogManager{

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**新增
	 * @param pd
	 * @throws Exception
	 */
	public void save(PageData pd, ExEnum walletEnum)throws Exception{
		PageData userWalletLogPd = new PageData();
		BigDecimal point = new BigDecimal(0);
		PageData exchangePoint = new PageData();
		if(walletEnum.getCode().equals(ExEnum.WL_TYPE3.getCode())) {
			//注册赠送饭点
			if(pd.get("invite") != null && StringUtils.isNotBlank(pd.getString("invite"))) {
				PageData invitePd = new PageData();
				invitePd.put("phone", pd.getString("invite"));
				PageData inviteUser = (PageData) this.dao.findForObject("UserMapper.findOne", invitePd);
				if(inviteUser == null) {
					//邀请人不存在
					throw new NjmsException(ExEnum.EX_NO_INVITE_USER_ERROR);
				}
				userWalletLogPd.put("POINT", Const.REG_GIVE_POINT);
			}
		}
		if(walletEnum.getCode().equals(ExEnum.WL_TYPE4.getCode())) {
			//邀请赠送饭点
			if(pd.get("invite") != null && StringUtils.isNotBlank(pd.getString("invite"))) {
				PageData invitePd = new PageData();
				invitePd.put("phone", pd.getString("invite"));
				PageData inviteUser = (PageData) this.dao.findForObject("UserMapper.findOne", invitePd);
				if(inviteUser == null) {
					//邀请人不存在
					throw new NjmsException(ExEnum.EX_NO_INVITE_USER_ERROR);
				}
				point = new BigDecimal(Const.INVITE_GIVE_POINT);
				userWalletLogPd.put("POINT", point);
				pd.put("user_id", inviteUser.get("user_id"));
			}
		}
		if(walletEnum.getCode().equals(ExEnum.WL_TYPE5.getCode())) {
			//消费饭点，记录为负数
			point = new BigDecimal(Double.parseDouble(String.valueOf(pd.get("POINT")))*-1).setScale(2,BigDecimal.ROUND_HALF_UP);
			userWalletLogPd.put("POINT", point);
		}
		if(walletEnum.getCode().equals(ExEnum.WL_TYPE1.getCode())) {
			//充值饭点
			point = new BigDecimal(String.valueOf(Const.RECHARGE_LIST.get(Integer.parseInt(pd.getString("index"))).get("RECHARGE_AMOUNT")));
			userWalletLogPd.put("POINT", point);
			userWalletLogPd.put("CHARGEID", pd.getString("charge_id"));
//			exchangePoint.put("money", point);	//充值不累计消费金额，仅消费累计
		}
		if(walletEnum.getCode().equals(ExEnum.WL_TYPE9.getCode())) {
			//消费金额兑换饭点，金额为负数
			point = new BigDecimal(Const.MIN_EXCHANGE);
			userWalletLogPd.put("POINT", Const.MIN_EXCHANGE);
			exchangePoint.put("money", new BigDecimal(Double.parseDouble(String.valueOf(pd.get("money")))*-1).setScale(2,BigDecimal.ROUND_HALF_UP));
		}
		userWalletLogPd.put("WL_ID", UuidUtil.get32UUID());
		userWalletLogPd.put("USER_ID", pd.getString("user_id"));
		userWalletLogPd.put("TYPE", walletEnum.getCode());
		userWalletLogPd.put("REMARK", walletEnum.getDes_cn());
		userWalletLogPd.put("REMARK_EN", walletEnum.getDes_en());
		dao.save("UserWalletLogMapper.save", userWalletLogPd);
		if(walletEnum.getCode().equals(ExEnum.WL_TYPE1.getCode())) {
			//充值饭点
			point = point.add(new BigDecimal(String.valueOf(Const.RECHARGE_LIST.get(Integer.parseInt(pd.getString("index"))).get("GIFT_AMOUNT"))));
			userWalletLogPd = new PageData();
			userWalletLogPd.put("POINT", new BigDecimal(String.valueOf(Const.RECHARGE_LIST.get(Integer.parseInt(pd.getString("index"))).get("GIFT_AMOUNT"))));
			userWalletLogPd.put("WL_ID", UuidUtil.get32UUID());
			userWalletLogPd.put("TYPE", ExEnum.WL_TYPE2.getCode());
			userWalletLogPd.put("REMARK", ExEnum.WL_TYPE2.getDes_cn());
			userWalletLogPd.put("REMARK_EN", ExEnum.WL_TYPE2.getDes_en());
			userWalletLogPd.put("USER_ID", pd.getString("user_id"));
			dao.save("UserWalletLogMapper.save", userWalletLogPd);
		}
		//改用户钱包金额
		//+point
		exchangePoint.put("user_id", pd.getString("user_id"));
		exchangePoint.put("point_add", point);
		dao.update("WalletMapper.exchangePointNew", exchangePoint);
	}
	
	/**删除
	 * @param pd
	 * @throws Exception
	 */
	public void delete(PageData pd)throws Exception{
		dao.delete("UserWalletLogMapper.delete", pd);
	}
	
	/**修改
	 * @param pd
	 * @throws Exception
	 */
	public void edit(PageData pd)throws Exception{
		dao.update("UserWalletLogMapper.edit", pd);
	}
	
	/**列表
	 * @param page
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public PageData list(PageData pd)throws Exception{
		PageData result = new PageData();
		String page = pd.getString("page");
		String page_size = pd.getString("page_size");
		int _page = 0;
		if(null != page && !"".equals(page)){
			_page  = Integer.parseInt(page);
		}
		
		int _page_size = 9;
		if(null != page_size && !"".equals(page_size)){
			_page_size  = Integer.parseInt(page_size);
		}
		
		Page pg = new Page();
		pg.setPd(pd);
		pg.setCurrentPage(_page);
		pg.setShowCount(_page_size);
		
		List<PageData> list = (List<PageData>)dao.findForList("UserWalletLogMapper.datalistPage", pg);
		result.put("rows", list);
		result.put("total", pg.getTotalResult());
		result.put("total_page", pg.getTotalPage());
		return result;
	}
	
	/**列表(全部)
	 * @param pd
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> listAll(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("UserWalletLogMapper.listAll", pd);
	}
	
	/**通过id获取数据
	 * @param pd
	 * @throws Exception
	 */
	public PageData findById(PageData pd)throws Exception{
		return (PageData)dao.findForObject("UserWalletLogMapper.findById", pd);
	}
	
	/**批量删除
	 * @param ArrayDATA_IDS
	 * @throws Exception
	 */
	public void deleteAll(String[] ArrayDATA_IDS)throws Exception{
		dao.delete("UserWalletLogMapper.deleteAll", ArrayDATA_IDS);
	}
	
}

