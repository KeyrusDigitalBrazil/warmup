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
package de.hybris.platform.commercefacades.customergroups.converters.populator;

import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Default populator, convert UserGroupModel to UserGroupData
 */
public class UserGroupPopulator implements Populator<UserGroupModel, UserGroupData>
{

	@Override
	public void populate(final UserGroupModel source, final UserGroupData target) throws ConversionException
	{
		target.setName(source.getLocName());
		target.setUid(source.getUid());
	}

}
