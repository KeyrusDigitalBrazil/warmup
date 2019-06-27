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
package de.hybris.platform.cmsfacades.common.populator.impl;

import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.ComposedTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to populate the basic attributes from a <code>ComposedTypeModel</code> to a <code>ComposedTypeData</code>.
 *
 * <p>
 * Will populate:</br>
 * <ul>
 * <li><code>code</code></li>
 * <li><code>name</code></li>
 * <li><code>description</code></li>
 * </ul>
 * </p>
 */
public class ComposedTypeModelPopulator implements Populator<ComposedTypeModel, ComposedTypeData>
{
	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final ComposedTypeModel source, final ComposedTypeData target) throws ConversionException
	{
		target.setCode(source.getCode());

		final Map<String, String> nameMap = Optional.ofNullable(target.getName()).orElseGet(() -> getNewNameMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value),
				(locale) -> source.getName(locale));

		final Map<String, String> descriptionMap = Optional.ofNullable(target.getDescription())
				.orElseGet(() -> getNewDescriptionMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> descriptionMap.put(getLocalizedPopulator().getLanguage(locale), value),
				(locale) -> source.getDescription(locale));
	}

	protected Map<String, String> getNewNameMap(final ComposedTypeData target)
	{
		target.setName(new LinkedHashMap<>());
		return target.getName();
	}

	protected Map<String, String> getNewDescriptionMap(final ComposedTypeData target)
	{
		target.setDescription(new LinkedHashMap<>());
		return target.getDescription();
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
