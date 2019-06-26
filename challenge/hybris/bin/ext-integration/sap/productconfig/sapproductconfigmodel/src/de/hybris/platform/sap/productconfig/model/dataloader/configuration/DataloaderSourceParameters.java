/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.productconfig.model.dataloader.configuration;

/**
 * Parameter Object - just a simple POJO
 */
public class DataloaderSourceParameters
{

	private boolean useLoadBalance;
	private String sysId;
	private String logonGroup;
	private String instanceno;
	private String targetHost;
	private String msgServer;
	private String user;
	private String password;
	private String client;
	/**
	 * Callback destination name, which is the destination called by the ERP system
	 */
	private String clientRfcDestination;
	/**
	 * destination name of the ERP system, effectively replaces the individual connection params
	 */
	private String serverRfcDestination;

	/**
	 * @return the useLoadBalance
	 */
	public boolean isUseLoadBalance()
	{
		return useLoadBalance;
	}

	/**
	 * @param useLoadBalance
	 *           the useLoadBalance to set
	 */
	public void setUseLoadBalance(final boolean useLoadBalance)
	{
		this.useLoadBalance = useLoadBalance;
	}

	/**
	 * @return the sysId
	 */
	public String getSysId()
	{
		return sysId;
	}

	/**
	 * @param sysId
	 *           the sysId to set
	 */
	public void setSysId(final String sysId)
	{
		this.sysId = sysId;
	}

	/**
	 * @return the logonGroup
	 */
	public String getLogonGroup()
	{
		return logonGroup;
	}

	/**
	 * @param logonGroup
	 *           the logonGroup to set
	 */
	public void setLogonGroup(final String logonGroup)
	{
		this.logonGroup = logonGroup;
	}

	/**
	 * @return the instanceno
	 */
	public String getInstanceno()
	{
		return instanceno;
	}

	/**
	 * @param instanceno
	 *           the instanceno to set
	 */
	public void setInstanceno(final String instanceno)
	{
		this.instanceno = instanceno;
	}

	/**
	 * @return the targetHost
	 */
	public String getTargetHost()
	{
		return targetHost;
	}

	/**
	 * @param targetHost
	 *           the targetHost to set
	 */
	public void setTargetHost(final String targetHost)
	{
		this.targetHost = targetHost;
	}

	/**
	 * @return the msgServer
	 */
	public String getMsgServer()
	{
		return msgServer;
	}

	/**
	 * @param msgServer
	 *           the msgServer to set
	 */
	public void setMsgServer(final String msgServer)
	{
		this.msgServer = msgServer;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 * @param user
	 *           the user to set
	 */
	public void setUser(final String user)
	{
		this.user = user;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password
	 *           the password to set
	 */
	public void setPassword(final String password)
	{
		this.password = password;
	}

	/**
	 * @return the client
	 */
	public String getClient()
	{
		return client;
	}

	/**
	 * @param client
	 *           the client to set
	 */
	public void setClient(final String client)
	{
		this.client = client;
	}

	/**
	 * @return the clientRfcDestination
	 */
	public String getClientRfcDestination()
	{
		return clientRfcDestination;
	}

	/**
	 * @param clientRfcDestination
	 *           the clientRfcDestination to set
	 */
	public void setClientRfcDestination(final String clientRfcDestination)
	{
		this.clientRfcDestination = clientRfcDestination;
	}

	/**
	 * @return the serverRfcDestination
	 */
	public String getServerRfcDestination()
	{
		return serverRfcDestination;
	}

	/**
	 * @param serverRfcDestination
	 *           the serverRfcDestination to set
	 */
	public void setServerRfcDestination(final String serverRfcDestination)
	{
		this.serverRfcDestination = serverRfcDestination;
	}

}
