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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.acceleratorservices.email.dao.EmailAddressDao;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.warehousing.util.builder.EmailAddressModelBuilder;
import org.springframework.beans.factory.annotation.Required;


public class EmailAddresses extends AbstractItems<EmailAddressModel>
{
	public static final String DISPLAYED_NAME = "polo";
	public static final String EMAIL_ADDRESS = "polo@polo.com";

	private EmailAddressDao emailAddressDao;

	public EmailAddressModel polo()
	{
		return getOrCreateEmail(EMAIL_ADDRESS, DISPLAYED_NAME);
	}

	protected EmailAddressModel getOrCreateEmail(final String name, final String email)
	{
		return getOrSaveAndReturn(() -> getEmailAddressDao().findEmailAddressByEmailAndDisplayName(email, name),
				() -> EmailAddressModelBuilder.aModel().withDisplayedName(DISPLAYED_NAME).withEmailAddress(EMAIL_ADDRESS)
						.build());
	}

	public EmailAddressDao getEmailAddressDao()
	{
		return emailAddressDao;
	}

	@Required
	public void setEmailAddressDao(final EmailAddressDao emailAddressDao)
	{
		this.emailAddressDao = emailAddressDao;
	}


}
