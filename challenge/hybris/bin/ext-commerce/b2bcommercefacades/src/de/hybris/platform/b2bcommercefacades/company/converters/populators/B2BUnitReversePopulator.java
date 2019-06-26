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
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.beans.factory.annotation.Required;


/**
 * Reverse populator, to populate a {@link B2BUnitModel} from a {@link B2BUnitData}.
 */
public class B2BUnitReversePopulator implements Populator<B2BUnitData, B2BUnitModel>
{
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Override
	public void populate(final B2BUnitData source, final B2BUnitModel target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setName(source.getName());
		target.setLocName(source.getName());
		target.setUid(source.getUid());
		target.setActive(Boolean.TRUE);

		if (source.getUnit() != null)
		{
			final B2BUnitModel parentUnit = this.getB2bUnitService().getUnitForUid(source.getUnit().getUid());
			getB2bCommerceUnitService().setParentUnit(target, parentUnit);
		}
	}

	protected B2BUnitService<B2BUnitModel, UserModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	protected B2BCommerceUnitService getB2bCommerceUnitService()
	{
		return b2bCommerceUnitService;
	}

	@Required
	public void setB2bCommerceUnitService(final B2BCommerceUnitService b2bUnitService)
	{
		this.b2bCommerceUnitService = b2bUnitService;
	}

}
