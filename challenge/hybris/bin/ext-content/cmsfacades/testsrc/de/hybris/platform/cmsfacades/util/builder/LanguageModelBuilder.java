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
package de.hybris.platform.cmsfacades.util.builder;

import de.hybris.platform.core.model.c2l.LanguageModel;



public class LanguageModelBuilder
{
	private final LanguageModel model;

	private LanguageModelBuilder()
	{
		model = new LanguageModel();
	}

	private LanguageModelBuilder(final LanguageModel model)
	{
		this.model = model;
	}

	protected LanguageModel getModel()
	{
		return this.model;
	}

	public static LanguageModelBuilder aModel()
	{
		return new LanguageModelBuilder();
	}

	public static LanguageModelBuilder fromModel(final LanguageModel model)
	{
		return new LanguageModelBuilder(model);
	}

	public LanguageModel build()
	{
		return this.getModel();
	}

	public LanguageModelBuilder withActive(final Boolean active)
	{
		getModel().setActive(active);
		return this;
	}

	public LanguageModelBuilder withIsocode(final String isocode)
	{
		getModel().setIsocode(isocode);
		return this;
	}
}
