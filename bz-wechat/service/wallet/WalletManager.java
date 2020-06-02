package com.flc.service.wallet;

import com.flc.util.PageData;

public interface WalletManager {

	
	public PageData getWalletPage(PageData pd) throws Exception;
	
	public PageData exchange(PageData pd) throws Exception;
	
}
