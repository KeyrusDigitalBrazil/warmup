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
package de.hybris.platform.b2b.punchout.services;



/**
 * Configuration service used to retrieve the punchout specific setting values.
 */
public interface PunchOutConfigurationService
{

	/**
	 * @return the punchout URL to be used by the punchout provider to authenticate and browse the catalog
	 */
	String getPunchOutLoginUrl();


	/**
	 * @return gets the default cost center.
	 */
	String getDefaultCostCenter();

}
