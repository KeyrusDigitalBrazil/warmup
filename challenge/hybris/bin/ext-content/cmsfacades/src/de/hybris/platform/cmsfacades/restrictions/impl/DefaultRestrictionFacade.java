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
package de.hybris.platform.cmsfacades.restrictions.impl;

import de.hybris.platform.cms2.model.RestrictionTypeModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminRestrictionService;
import de.hybris.platform.cmsfacades.data.RestrictionTypeData;
import de.hybris.platform.cmsfacades.restrictions.RestrictionFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the <code>RestrictionFacade</code>.
 */
public class DefaultRestrictionFacade implements RestrictionFacade
{
	private CMSAdminRestrictionService cmsAdminRestrictionService;
	private Converter<RestrictionTypeModel, RestrictionTypeData> restrictionTypeModelConverter;

	@Override
	public List<RestrictionTypeData> findAllRestrictionTypes()
	{
		return getCmsAdminRestrictionService().getAllRestrictionTypes().stream() //
				.map(restrictionType -> getRestrictionTypeModelConverter().convert(restrictionType)) //
				.collect(Collectors.toList());
	}

	protected CMSAdminRestrictionService getCmsAdminRestrictionService()
	{
		return cmsAdminRestrictionService;
	}

	@Required
	public void setCmsAdminRestrictionService(final CMSAdminRestrictionService cmsAdminRestrictionService)
	{
		this.cmsAdminRestrictionService = cmsAdminRestrictionService;
	}

	protected Converter<RestrictionTypeModel, RestrictionTypeData> getRestrictionTypeModelConverter()
	{
		return restrictionTypeModelConverter;
	}

	@Required
	public void setRestrictionTypeModelConverter(
			final Converter<RestrictionTypeModel, RestrictionTypeData> restrictionTypeModelConverter)
	{
		this.restrictionTypeModelConverter = restrictionTypeModelConverter;
	}

}
