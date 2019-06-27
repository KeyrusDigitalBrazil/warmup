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
package de.hybris.platform.cmsoccaddon.jaxb.adapters;

import de.hybris.platform.cmsoccaddon.data.ComponentWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData;

import javax.xml.bind.annotation.adapters.XmlAdapter;


/**
 * This adapter is used to convert {@link de.hybris.platform.cmsoccaddon.data.ComponentWsDTO} into
 * {@link de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData}
 */
public class ComponentWsDTOAdapter extends XmlAdapter<ComponentAdaptedData, ComponentWsDTO>
{
	@Override
	public ComponentAdaptedData marshal(final ComponentWsDTO componentDTO)
	{
		if (componentDTO == null)
		{
			return null;
		}

		return ComponentAdapterUtil.convert(componentDTO);
	}

	@Override
	public ComponentWsDTO unmarshal(final ComponentAdaptedData v) throws Exception
	{
		throw new UnsupportedOperationException();
	}
}
