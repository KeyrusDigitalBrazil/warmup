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
package de.hybris.platform.cmsfacades.navigations.populator.model;

import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Required;


/**
 * This populator will populate the {@link NavigationNodeData}'s base attributes with attributes from
 * {@link CMSNavigationNodeModel}.
 */
public class NavigationNodeModelToDataPopulator implements Populator<CMSNavigationNodeModel, NavigationNodeData>
{
	private LocalizedPopulator localizedPopulator;

	private Converter<CMSNavigationEntryModel, NavigationEntryData> navigationEntryModelToDataConverter;

	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Override
	public void populate(final CMSNavigationNodeModel source, final NavigationNodeData target) throws ConversionException
	{
		getUniqueItemIdentifierService().getItemData(source).ifPresent(itemData -> target.setUuid(itemData.getItemId()));

		target.setItemtype(source.getItemtype());
		target.setUid(source.getUid());
		target.setName(source.getName());
		target.setParentUid(source.getParent() != null ? source.getParent().getUid() : null);
		target.setHasChildren(source.getChildren() != null && !source.getChildren().isEmpty());
		getPosition(source).ifPresent(value -> target.setPosition(value));

		final Map<String, String> titleMap = Optional.ofNullable(target.getTitle()).orElseGet(() -> {
			target.setTitle(new LinkedHashMap<>());
			return target.getTitle();
		});

		getLocalizedPopulator().populate( //
				(locale, value) -> titleMap.put(getLocalizedPopulator().getLanguage(locale), value),
				(locale) -> source.getTitle(locale));

		target.setEntries(source.getEntries() //
				.stream() //
				.map(entryModel -> getNavigationEntryModelToDataConverter().convert(entryModel)) //
				.collect(Collectors.toList()));
	}

	/**
	 * Calculate the position of this node in relation to its siblings
	 *
	 * @param source
	 *           the node we want to calculate its position
	 * @return the position of the node in relation to its siblings
	 */
	protected OptionalInt getPosition(final CMSNavigationNodeModel source)
	{
		final CMSNavigationNodeModel parent = source.getParent();
		if (parent != null)
		{
			final OptionalInt position = IntStream.range(0, parent.getChildren().size())
					.filter(idx -> parent.getChildren().get(idx) == source).findFirst();
			return position;
		}
		return OptionalInt.empty();
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

	protected Converter<CMSNavigationEntryModel, NavigationEntryData> getNavigationEntryModelToDataConverter()
	{
		return navigationEntryModelToDataConverter;
	}

	@Required
	public void setNavigationEntryModelToDataConverter(
			final Converter<CMSNavigationEntryModel, NavigationEntryData> navigationEntryModelToDataConverter)
	{
		this.navigationEntryModelToDataConverter = navigationEntryModelToDataConverter;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return this.uniqueItemIdentifierService;
	}

}
