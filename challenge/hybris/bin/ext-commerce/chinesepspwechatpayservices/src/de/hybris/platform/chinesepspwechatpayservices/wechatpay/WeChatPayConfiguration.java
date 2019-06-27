/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.chinesepspwechatpayservices.wechatpay;

/**
 * Wechat Pay related configurations
 */
public class WeChatPayConfiguration
{
	private String appId;
	private String appSecret;
	private String mechId;
	private String oauthURL;
	private String accessTokenURL;
	private String unifiedOrderURL;
	private String orderQueryURL;
	private String mechKey;
	private String testMode;
	private Integer timeout;

	/**
	 * @return the appId
	 */
	public String getAppId()
	{
		return appId;
	}

	/**
	 * @param appId
	 *           the appId to set
	 */
	public void setAppId(final String appId)
	{
		this.appId = appId;
	}

	/**
	 * @return the appSecret
	 */
	public String getAppSecret()
	{
		return appSecret;
	}

	/**
	 * @param appSecret
	 *           the appSecret to set
	 */
	public void setAppSecret(final String appSecret)
	{
		this.appSecret = appSecret;
	}

	/**
	 * @return the mechId
	 */
	public String getMechId()
	{
		return mechId;
	}

	/**
	 * @param mechId
	 *           the mechId to set
	 */
	public void setMechId(final String mechId)
	{
		this.mechId = mechId;
	}


	/**
	 * @return the oauthURL
	 */
	public String getOauthURL()
	{
		return oauthURL;
	}

	/**
	 * @param oauthURL
	 *           the oauthURL to set
	 */
	public void setOauthURL(final String oauthURL)
	{
		this.oauthURL = oauthURL;
	}

	/**
	 * @return the accessTokenURL
	 */
	public String getAccessTokenURL()
	{
		return accessTokenURL;
	}

	/**
	 * @param accessTokenURL
	 *           the accessTokenURL to set
	 */
	public void setAccessTokenURL(final String accessTokenURL)
	{
		this.accessTokenURL = accessTokenURL;
	}

	/**
	 * @return the unifiedOrderURL
	 */
	public String getUnifiedOrderURL()
	{
		return unifiedOrderURL;
	}

	/**
	 * @param unifiedOrderURL
	 *           the unifiedOrderURL to set
	 */
	public void setUnifiedOrderURL(final String unifiedOrderURL)
	{
		this.unifiedOrderURL = unifiedOrderURL;
	}

	/**
	 * @return the orderQueryURL
	 */
	public String getOrderQueryURL()
	{
		return orderQueryURL;
	}

	/**
	 * @param orderQueryURL
	 *           the orderQueryURL to set
	 */
	public void setOrderQueryURL(final String orderQueryURL)
	{
		this.orderQueryURL = orderQueryURL;
	}

	/**
	 * @return the mechKey
	 */
	public String getMechKey()
	{
		return mechKey;
	}

	/**
	 * @param mechKey
	 *           the mechKey to set
	 */
	public void setMechKey(final String mechKey)
	{
		this.mechKey = mechKey;
	}

	/**
	 * @return the testMode
	 */
	public String getTestMode()
	{
		return testMode;
	}

	/**
	 * @param testMode
	 *           the testMode to set
	 */
	public void setTestMode(final String testMode)
	{
		this.testMode = testMode;
	}

	/**
	 * @return the timeout
	 */
	public Integer getTimeout()
	{
		return timeout;
	}

	/**
	 * @param timeout
	 *           the timeout to set
	 */
	public void setTimeout(final Integer timeout)
	{
		this.timeout = timeout;
	}


}
