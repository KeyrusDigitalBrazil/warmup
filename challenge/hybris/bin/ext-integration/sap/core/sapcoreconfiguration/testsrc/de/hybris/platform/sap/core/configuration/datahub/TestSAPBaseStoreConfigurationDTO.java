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
package de.hybris.platform.sap.core.configuration.datahub;

import de.hybris.platform.core.PK;


/**
 * Test DTO bean.
 */
@SuppressWarnings("javadoc")
public class TestSAPBaseStoreConfigurationDTO
{

	private PK pk;
	private String core_name;
	private PK SAPRFCDestinationPK;

	public void setPk(final PK pk)
	{
		this.pk = pk;
	}

	public PK getPk()
	{
		return pk;
	}

	public void setCore_name(final String core_name) // NOPMD
	{
		this.core_name = core_name;
	}

	public String getCore_name() // NOPMD
	{
		return core_name;
	}

	public PK getSAPRFCDestinationPK()
	{
		return SAPRFCDestinationPK;
	}

	public void setSAPRFCDestinationPK(final PK sAPRFCDestinationPK)
	{
		SAPRFCDestinationPK = sAPRFCDestinationPK;
	}

}
