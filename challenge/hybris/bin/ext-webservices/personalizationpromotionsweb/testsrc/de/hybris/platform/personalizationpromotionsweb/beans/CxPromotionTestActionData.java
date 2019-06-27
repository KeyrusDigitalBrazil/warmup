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
package de.hybris.platform.personalizationpromotionsweb.beans;

import de.hybris.platform.personalizationfacades.data.ActionData;


public class CxPromotionTestActionData extends ActionData
{
	private String testField;

	public CxPromotionTestActionData()
	{

	}

	public String getTestField()
	{
		return testField;
	}

	public void setTestField(final String testField)
	{
		this.testField = testField;
	}



}
