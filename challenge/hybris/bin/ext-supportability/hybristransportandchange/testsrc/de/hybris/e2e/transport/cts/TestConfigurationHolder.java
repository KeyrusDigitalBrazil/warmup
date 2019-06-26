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
package de.hybris.e2e.transport.cts;

public class TestConfigurationHolder implements ConfigurationHolder
{

	/**
	 * Gets application type
	 * 
	 * @return application type
	 */
	@Override
	public String getApplicationType()
	{
		return "HYBRIS";
	}

	/**
	 * Gets SID
	 * 
	 * @return SID
	 */
	@Override
	public String getSid()
	{
		return "TEST";
	}

	/**
	 * Gets user
	 * 
	 * @return user
	 */
	@Override
	public String getUser()
	{
		return "TestUser";
	}

	/**
	 * Gets password
	 * 
	 * @return password
	 */
	@Override
	public String getPassword()
	{
		return "TestPwd";
	}

	/**
	 * Gets WS' url
	 * 
	 * @return url
	 */
	@Override
	public String getUrl()
	{
		return "sap.fake_url.exportwebservice?wsdl";
	}

	/**
	 * Gets transport package size
	 * 
	 * @return transport package size
	 */
	@Override
	public int getPackageSize()
	{
		return 2;
	}

	/**
	 * Gets WS' name
	 * 
	 * @return name
	 */
	@Override
	public String getWsName()
	{
		return "wWsFakeName";
	}

	/**
	 * Gets WS' bidning's name
	 * 
	 * @return name
	 */
	@Override
	public String getWsBindingName()
	{
		return "fakeBindingName";
	}

}
