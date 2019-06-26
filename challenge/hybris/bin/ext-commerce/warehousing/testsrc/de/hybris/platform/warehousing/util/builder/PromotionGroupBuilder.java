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

import de.hybris.platform.promotions.model.PromotionGroupModel;


public class PromotionGroupBuilder
{
	private final PromotionGroupModel model;

	private PromotionGroupBuilder()
	{
		model = new PromotionGroupModel();
	}

	public static PromotionGroupBuilder aModel()
	{
		return new PromotionGroupBuilder();
	}

	private PromotionGroupModel getModel()
	{
		return this.model;
	}

	public PromotionGroupModel build()
	{
		return getModel();
	}

	public PromotionGroupBuilder withIdentifier(final String identifier)
	{
		getModel().setIdentifier(identifier);
		return this;
	}
}
