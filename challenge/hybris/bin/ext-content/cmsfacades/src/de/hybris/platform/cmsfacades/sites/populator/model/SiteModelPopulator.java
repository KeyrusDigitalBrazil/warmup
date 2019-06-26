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
package de.hybris.platform.cmsfacades.sites.populator.model;

import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmsfacades.resolvers.sites.SiteThumbnailResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@Link SiteData} DTO from a {@Link CMSSiteModel}
 */
public class SiteModelPopulator implements Populator<CMSSiteModel, SiteData>
{

	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final CMSSiteModel source, final SiteData target) throws ConversionException
	{
		target.setUid(source.getUid());
		target.setPreviewUrl(source.getPreviewURL());
		target.setContentCatalogs(getSortedContentCatalogsID(source.getContentCatalogs()));

		getLocalizedPopulator().populate(getSiteDataNameSetter(target), getSiteModelNameGetter(source));
	}

	protected Function<Locale, String> getSiteModelNameGetter(final CMSSiteModel source)
	{
		return (locale) -> source.getName(locale);
	}

	protected BiConsumer<Locale, String> getSiteDataNameSetter(final SiteData target)
	{
		return (locale, value) -> {
			if (Objects.isNull(target.getName()))
			{
				target.setName(new LinkedHashMap<>());
			}
			target.getName().put(localizedPopulator.getLanguage(locale), value);
		};
	}

	/**
	 * This method returns a list of the IDs of the provided content catalogs. Please note that the order
	 * is maintained.
	 *
	 * @param contentCatalogs The content catalogs for which to retrieve their IDs.
	 * @return The list of IDs of the provided content catalogs.
	 */
	protected List<String> getSortedContentCatalogsID(List<ContentCatalogModel> contentCatalogs)
	{
		return contentCatalogs.stream()
				.map(ContentCatalogModel::getId)
				.collect(Collectors.toList());
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
