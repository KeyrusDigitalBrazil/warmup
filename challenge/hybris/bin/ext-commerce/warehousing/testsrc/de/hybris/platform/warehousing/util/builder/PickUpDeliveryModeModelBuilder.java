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

import de.hybris.platform.commerceservices.model.PickUpDeliveryModeModel;

import java.util.Locale;


public class PickUpDeliveryModeModelBuilder
{
	private final PickUpDeliveryModeModel model;

	private PickUpDeliveryModeModelBuilder()
	{
		model = new PickUpDeliveryModeModel();
	}

	private PickUpDeliveryModeModel getModel()
	{
		return this.model;
	}

	public static PickUpDeliveryModeModelBuilder aModel()
	{
		return new PickUpDeliveryModeModelBuilder();
	}

	public PickUpDeliveryModeModel build()
	{
		return getModel();
	}

	public PickUpDeliveryModeModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public PickUpDeliveryModeModelBuilder withActive(final Boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public PickUpDeliveryModeModelBuilder withName(final String name, final Locale locale)
	{
		getModel().setName(name, locale);
		return this;
	}

}
