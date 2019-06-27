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
package de.hybris.platform.cmsfacades.types.service.impl;

import static java.util.stream.Collectors.toList;

import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilter;
import de.hybris.platform.cmsfacades.types.service.StructureTypeModeAttributeFilterProvider;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link StructureTypeModeAttributeFilterProvider}
 */
public class DefaultStructureTypeModeAttributeFilterProvider implements StructureTypeModeAttributeFilterProvider
{

	private List<StructureTypeModeAttributeFilter> cmsStructureTypeModes;
	
	@Override
	public List<StructureTypeModeAttributeFilter> getStructureTypeModeAttributeFilters(final String typeCode, final StructureTypeMode mode)
	{
		return getCmsStructureTypeModes().stream()
				.filter(modesData -> modesData.getConstrainedBy().test(typeCode, mode))
				.collect(toList());
	}

	@Override
	public void addStructureTypeModeAttributeFilter(final StructureTypeModeAttributeFilter mode)
	{
		// not thread safe
		getCmsStructureTypeModes().add(mode);
	}


	protected List<StructureTypeModeAttributeFilter> getCmsStructureTypeModes()
	{
		return cmsStructureTypeModes;
	}

	@Required
	public void setCmsStructureTypeModes(final List<StructureTypeModeAttributeFilter> cmsStructureTypeModes)
	{
		this.cmsStructureTypeModes = cmsStructureTypeModes;
	}
}
