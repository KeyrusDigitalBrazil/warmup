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
package de.hybris.platform.b2bacceleratorfacades.order.populators;

import de.hybris.platform.b2b.enums.B2BPermissionTypeEnum;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPermissionTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.type.TypeService;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link B2BPermissionTypeEnum} to {@link B2BPermissionTypeData}.
 *
 * @deprecated Since 6.0. Use
 *             {@link de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators.B2BPermissionTypePopulator}
 *             instead.
 */
@Deprecated
public class B2BPermissionTypePopulator implements Populator<B2BPermissionTypeEnum, B2BPermissionTypeData>
{
	private TypeService typeService;

	@Override
	public void populate(final B2BPermissionTypeEnum source, final B2BPermissionTypeData target)
	{
		target.setCode(source.getCode());
		target.setName(getTypeService().getEnumerationValue(source).getName());
	}

	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}
}
