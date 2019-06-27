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

import de.hybris.platform.b2b.company.B2BCommerceB2BUserGroupService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


public class B2BCustomerReversePopulator implements Populator<CustomerData, B2BCustomerModel>
{
	private B2BCommerceB2BUserGroupService b2BCommerceB2BUserGroupService;
	private CustomerNameStrategy customerNameStrategy;
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;
	private UserService userService;
	private B2BUnitService<B2BUnitModel, UserModel> b2BUnitService;

	@Override
	public void populate(final CustomerData source, final B2BCustomerModel target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setEmail(source.getEmail());
		target.setName(getCustomerNameStrategy().getName(source.getFirstName(), source.getLastName()));

		populateUid(source, target);
		populateTitle(source, target);
		populateDefaultUnit(source, target);

		getB2BCommerceB2BUserGroupService().updateUserGroups(getB2BUserGroupsLookUpStrategy().getUserGroups(), source.getRoles(),
				target);
	}

	protected void populateTitle(final CustomerData source, final B2BCustomerModel target)
	{
		if (StringUtils.isNotBlank(source.getTitleCode()))
		{
			target.setTitle(getUserService().getTitleForCode(source.getTitleCode()));
		}
		else
		{
			target.setTitle(null);
		}
	}

	protected void populateDefaultUnit(final CustomerData source, final B2BCustomerModel target)
	{
		final B2BUnitModel oldDefaultUnit = getB2BUnitService().getParent(target);
		final B2BUnitModel defaultUnit = getB2BUnitService().getUnitForUid(source.getUnit().getUid());
		target.setDefaultB2BUnit(defaultUnit);

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(target.getGroups());
		if (oldDefaultUnit != null && groups.contains(oldDefaultUnit))
		{
			groups.remove(oldDefaultUnit);
		}
		groups.add(defaultUnit);
		target.setGroups(groups);
	}

	protected void populateUid(final CustomerData source, final B2BCustomerModel target)
	{
		String updateUid = null;
		if (StringUtils.isNotBlank(source.getDisplayUid()))
		{
			updateUid = source.getDisplayUid();
		}
		else if (source.getEmail() != null)
		{
			updateUid = source.getEmail();
		}

		if (updateUid == null)
		{
			return;
		}

		if (StringUtils.isBlank(target.getUid())
				|| !updateUid.equalsIgnoreCase(target.getUid()))
		{
			target.setOriginalUid(updateUid);
			target.setUid(updateUid.toLowerCase());
		}
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected B2BUserGroupsLookUpStrategy getB2BUserGroupsLookUpStrategy()
	{
		return b2BUserGroupsLookUpStrategy;
	}

	@Required
	public void setB2BUserGroupsLookUpStrategy(final B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy)
	{
		this.b2BUserGroupsLookUpStrategy = b2BUserGroupsLookUpStrategy;
	}

	protected CustomerNameStrategy getCustomerNameStrategy()
	{
		return customerNameStrategy;
	}

	@Required
	public void setCustomerNameStrategy(final CustomerNameStrategy customerNameStrategy)
	{
		this.customerNameStrategy = customerNameStrategy;
	}

	protected B2BCommerceB2BUserGroupService getB2BCommerceB2BUserGroupService()
	{
		return b2BCommerceB2BUserGroupService;
	}

	@Required
	public void setB2BCommerceB2BUserGroupService(final B2BCommerceB2BUserGroupService b2bCommerceB2BUserGroupService)
	{
		b2BCommerceB2BUserGroupService = b2bCommerceB2BUserGroupService;
	}

	protected B2BUnitService<B2BUnitModel, UserModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, UserModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
	}

}
