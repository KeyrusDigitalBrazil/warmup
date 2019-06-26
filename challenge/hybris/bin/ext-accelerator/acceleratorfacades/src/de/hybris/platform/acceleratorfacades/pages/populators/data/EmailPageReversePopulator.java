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
package de.hybris.platform.acceleratorfacades.pages.populators.data;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.EmailPageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converts an {@link EmailPageData} of the type code EmailPage to {@link EmailPageModel}
 */
public class EmailPageReversePopulator implements Populator<EmailPageData, EmailPageModel>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final EmailPageData source, final EmailPageModel target) throws ConversionException
	{
		Optional.ofNullable(source.getFromEmail()) //
		.ifPresent(fromEmail -> getLocalizedPopulator().populate( //
				(locale, value) -> target.setFromEmail(value, locale), //
				(locale) -> fromEmail.get(getLocalizedPopulator().getLanguage(locale))));

		Optional.ofNullable(source.getFromName()) //
		.ifPresent(fromName -> getLocalizedPopulator().populate( //
				(locale, value) -> target.setFromName(value, locale), //
				(locale) -> fromName.get(getLocalizedPopulator().getLanguage(locale))));
	}

	protected LocalizedPopulator getLocalizedPopulator()
	{
		return localizedPopulator;
	}

	@Required
	public void setLocalizedPopulator(final LocalizedPopulator localizedPopulator)
	{
		this.localizedPopulator = localizedPopulator;
	}
}
