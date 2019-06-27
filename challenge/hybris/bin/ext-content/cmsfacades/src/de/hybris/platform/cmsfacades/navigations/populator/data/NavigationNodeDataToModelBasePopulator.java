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
package de.hybris.platform.cmsfacades.navigations.populator.data;

import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link CMSNavigationNodeModel}'s base attributes with attributes from
 * {@link NavigationNodeData}.
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.cmsfacades.cmsitems.CMSItemFacade} instead.
 */
@Deprecated
public class NavigationNodeDataToModelBasePopulator implements Populator<NavigationNodeData, CMSNavigationNodeModel>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final NavigationNodeData source, final CMSNavigationNodeModel target) throws ConversionException
	{
		target.setName(source.getName());
		Optional.ofNullable(source.getTitle()) //
				.ifPresent(title -> getLocalizedPopulator().populate( //
						(locale, value) -> target.setTitle(value, locale), //
						(locale) -> title.get(getLocalizedPopulator().getLanguage(locale))));
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
