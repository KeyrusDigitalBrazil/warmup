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

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator for {@link B2BUserGroupModel}.
 */
public class B2BUserGroupPopulator implements Populator<B2BUserGroupModel, B2BUserGroupData>
{
	private Converter<CustomerModel, CustomerData> b2BCustomerConverter;

	@Override
	public void populate(final B2BUserGroupModel source, final B2BUserGroupData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setUid(source.getUid());
		target.setName(source.getName());

		populateUnit(source, target);
		populateMembers(source, target);
	}

	protected void populateMembers(final B2BUserGroupModel source, final B2BUserGroupData target)
	{
		final Set<PrincipalModel> members = source.getMembers();
		if (CollectionUtils.isNotEmpty(members))
		{
			target.setMembers(Converters.convertAll(
					CollectionUtils.select(members, PredicateUtils.instanceofPredicate(CustomerModel.class)),
					getB2BCustomerConverter()));
		}
	}

	protected void populateUnit(final B2BUserGroupModel source, final B2BUserGroupData target)
	{
		final B2BUnitModel unit = source.getUnit();
		if (unit != null)
		{
			final B2BUnitData b2BUnitData = new B2BUnitData();
			b2BUnitData.setUid(unit.getUid());
			b2BUnitData.setName(unit.getLocName());
			b2BUnitData.setActive(Boolean.TRUE.equals(unit.getActive()));
			target.setUnit(b2BUnitData);
		}
	}

	protected Converter<CustomerModel, CustomerData> getB2BCustomerConverter()
	{
		return b2BCustomerConverter;
	}

	@Required
	public void setB2BCustomerConverter(final Converter<CustomerModel, CustomerData> b2BCustomerConverter)
	{
		this.b2BCustomerConverter = b2BCustomerConverter;
	}
}
