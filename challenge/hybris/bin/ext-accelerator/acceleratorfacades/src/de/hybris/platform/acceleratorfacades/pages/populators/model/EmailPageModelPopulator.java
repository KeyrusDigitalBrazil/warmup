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
package de.hybris.platform.acceleratorfacades.pages.populators.model;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.EmailPageData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Converts an {@link EmailPageModel} page to a {@link EmailPageData} dto
 */
public class EmailPageModelPopulator implements Populator<EmailPageModel, EmailPageData>
{

	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final EmailPageModel source, final EmailPageData target) throws ConversionException
	{
		final Map<String, String> fromEmailMap = Optional.ofNullable(target.getFromEmail())
				.orElseGet(() -> getNewFromEmailMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> fromEmailMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getFromEmail(locale));

		final Map<String, String> fromNameMap = Optional.ofNullable(target.getFromName())
				.orElseGet(() -> getNewFromNameMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> fromNameMap.put(getLocalizedPopulator().getLanguage(locale), value), //
				(locale) -> source.getFromName(locale));
	}

	protected Map<String, String> getNewFromEmailMap(final EmailPageData target)
	{
		target.setFromEmail(new LinkedHashMap<>());
		return target.getFromEmail();
	}

	protected Map<String, String> getNewFromNameMap(final EmailPageData target)
	{
		target.setFromName(new LinkedHashMap<>());
		return target.getFromName();
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
