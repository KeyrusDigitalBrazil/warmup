/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.util.builder;

import de.hybris.platform.warehousing.model.RestockConfigModel;


public class RestockConfigModelBuilder
{
	private final RestockConfigModel model;

	private RestockConfigModelBuilder()
	{
		model = new RestockConfigModel();
	}

	private RestockConfigModel getModel()
	{
		return this.model;
	}

	public static RestockConfigModelBuilder aModel()
	{
		return new RestockConfigModelBuilder();
	}

	public RestockConfigModelBuilder withReturnedBin(final String returnedBin)
	{
		getModel().setReturnedBinCode(returnedBin);
		return this;
	}

	public RestockConfigModelBuilder withIsUpdateStockAfterReturn(final Boolean isUpdateStockAfterReturn)
	{
		getModel().setIsUpdateStockAfterReturn(isUpdateStockAfterReturn);
		return this;
	}

	public RestockConfigModelBuilder withDelayDaysBeforeRestock(final int delayDaysBeforeRestock)
	{
		getModel().setDelayDaysBeforeRestock(0);
		return this;
	}

	public RestockConfigModel build()
	{
		return getModel();
	}
}
