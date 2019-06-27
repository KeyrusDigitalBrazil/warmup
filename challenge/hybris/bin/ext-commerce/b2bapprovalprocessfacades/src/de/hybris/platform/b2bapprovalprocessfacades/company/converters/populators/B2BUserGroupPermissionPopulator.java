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
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


public class B2BUserGroupPermissionPopulator implements Populator<B2BUserGroupModel, B2BUserGroupData>
{
	private Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter;

	@Override
	public void populate(final B2BUserGroupModel source, final B2BUserGroupData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		final List<B2BPermissionModel> permissions = source.getPermissions();
		if (CollectionUtils.isNotEmpty(permissions))
		{
			target.setPermissions(Converters.convertAll(permissions, getB2BPermissionConverter()));
		}
	}

	protected Converter<B2BPermissionModel, B2BPermissionData> getB2BPermissionConverter()
	{
		return b2BPermissionConverter;
	}

	@Required
	public void setB2BPermissionConverter(final Converter<B2BPermissionModel, B2BPermissionData> b2BPermissionConverter)
	{
		this.b2BPermissionConverter = b2BPermissionConverter;
	}

}
