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

import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;


public class EmailAddressModelBuilder
{
	private final EmailAddressModel model;

	private EmailAddressModelBuilder()
	{
		model = new EmailAddressModel();
	}

	private EmailAddressModel getModel()
	{
		return this.model;
	}

	public static EmailAddressModelBuilder aModel()
	{
		return new EmailAddressModelBuilder();
	}

	public EmailAddressModel build()
	{
		return getModel();
	}

	public EmailAddressModelBuilder withDisplayedName(final String name)
	{
		getModel().setDisplayName(name);
		return this;
	}

	public EmailAddressModelBuilder withEmailAddress(final String email)
	{
		getModel().setEmailAddress(email);
		return this;
	}
}
