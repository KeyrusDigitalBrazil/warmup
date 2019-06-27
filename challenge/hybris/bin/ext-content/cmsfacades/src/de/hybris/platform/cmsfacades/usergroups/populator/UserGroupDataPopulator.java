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
package de.hybris.platform.cmsfacades.usergroups.populator;

import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.data.UserGroupData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a {@link UserGroupData} instance from the {@link UserGroupModel} source data model.
 */
public class UserGroupDataPopulator implements Populator<UserGroupModel, UserGroupData>
{

	private LocalizedPopulator localizedPopulator;

	@Override
	public void populate(final UserGroupModel source, final UserGroupData target) throws ConversionException
	{
		target.setUid(source.getUid());

		final Map<String, String> nameMap = Optional.ofNullable(target.getName()).orElseGet(() -> getNewNameMap(target));
		getLocalizedPopulator().populate( //
				(locale, value) -> nameMap.put(getLocalizedPopulator().getLanguage(locale), value),
				(locale) -> source.getLocName(locale));
	}

	protected Map<String, String> getNewNameMap(final UserGroupData target)
	{
		target.setName(new LinkedHashMap<>());
		return target.getName();
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
