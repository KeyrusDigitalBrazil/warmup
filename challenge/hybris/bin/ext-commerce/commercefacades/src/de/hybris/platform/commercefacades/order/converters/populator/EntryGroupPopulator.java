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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.stream.Collectors;


/**
 * Creates a deep copy of {@link EntryGroup}, intended to be used with root entryGroups as
 * it copies its children recursively as well.
 */
public class EntryGroupPopulator implements Populator<EntryGroup, EntryGroupData>
{

	private Converter<EntryGroup, EntryGroupData> entryGroupConverter;

	@Override
	public void populate(final EntryGroup entryGroup, final EntryGroupData entryGroupData) throws ConversionException
	{
		entryGroupData.setGroupNumber(entryGroup.getGroupNumber());
		entryGroupData.setLabel(entryGroup.getLabel());
		entryGroupData.setExternalReferenceId(entryGroup.getExternalReferenceId());
		entryGroupData.setGroupType(entryGroup.getGroupType());
		entryGroupData.setPriority(entryGroup.getPriority());
		entryGroupData.setErroneous(entryGroup.getErroneous());

		if (CollectionUtils.isNotEmpty(entryGroup.getChildren()))
		{
			entryGroupData.setChildren(entryGroup.getChildren().stream()
					.map(childGroup -> {
						EntryGroupData childData = new EntryGroupData();
						childData.setParent(entryGroupData);
						getEntryGroupConverter().convert(childGroup, childData);
						return childData;
					}).collect(Collectors.toList()));
		}
		else
		{
			entryGroupData.setChildren(Collections.emptyList());
		}
	}

	protected Converter<EntryGroup, EntryGroupData> getEntryGroupConverter()
	{
		return entryGroupConverter;
	}

	@Required
	public void setEntryGroupConverter(Converter<EntryGroup, EntryGroupData> entryGroupConverter)
	{
		this.entryGroupConverter = entryGroupConverter;
	}
}
