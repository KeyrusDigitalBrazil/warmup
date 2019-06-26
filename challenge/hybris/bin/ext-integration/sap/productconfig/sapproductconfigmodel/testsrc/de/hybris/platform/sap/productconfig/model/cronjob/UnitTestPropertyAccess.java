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
package de.hybris.platform.sap.productconfig.model.cronjob;

@SuppressWarnings("javadoc")
public class UnitTestPropertyAccess implements PropertyAccessFacade
{

	private boolean startDeltaloadAfterInitial = true;


	public void setStartDeltaloadAfterInitial(final boolean startDeltaloadAfterInitial)
	{
		this.startDeltaloadAfterInitial = startDeltaloadAfterInitial;
	}


	@Override
	public boolean getStartDeltaloadAfterInitial()
	{
		return startDeltaloadAfterInitial;
	}

}
