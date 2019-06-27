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

import de.hybris.platform.ordersplitting.model.VendorModel;


public class VendorModelBuilder
{
	private final VendorModel model;

	private VendorModelBuilder()
	{
		model = new VendorModel();
	}

	private VendorModel getModel()
	{
		return this.model;
	}

	public static VendorModelBuilder aModel()
	{
		return new VendorModelBuilder();
	}

	public VendorModel build()
	{
		return getModel();
	}

	public VendorModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

}
