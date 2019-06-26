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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;


/**
 * Populates {@link CustomerData} from a {@link B2BCustomerModel}
 */
public class B2BUserPopulator implements Populator<B2BCustomerModel, CustomerData>
{
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService;
	private UserService userService;
	private MessageSource messageSource;
	private I18NService i18nService;
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Override
	public void populate(final B2BCustomerModel source, final CustomerData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setUid(source.getUid());
		target.setNormalizedUid(target.getUid().replaceAll("\\W", "_"));
		target.setName(source.getName());
		target.setActive(Boolean.TRUE.equals(source.getActive()));

		populateUnit(source, target);
		populateRoles(source, target);
	}

	protected void populateUnit(final B2BCustomerModel source, final CustomerData target)
	{
		final B2BUnitModel parentUnit = getB2BUnitService().getParent(source);
		if (parentUnit != null)
		{
			final B2BUnitData b2BUnitData = new B2BUnitData();

			b2BUnitData.setUid(parentUnit.getUid());
			b2BUnitData.setName(parentUnit.getLocName());
			b2BUnitData.setActive(Boolean.TRUE.equals(parentUnit.getActive()));

			target.setUnit(b2BUnitData);
		}
	}

	protected void populateRoles(final B2BCustomerModel source, final CustomerData target)
	{
		final List<String> roles = new ArrayList<String>();
		final Set<PrincipalGroupModel> roleModels = new HashSet<PrincipalGroupModel>(source.getGroups());
		CollectionUtils.filter(roleModels, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUnitModel.class)));
		CollectionUtils
				.filter(roleModels, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUserGroupModel.class)));

		for (final PrincipalGroupModel role : roleModels)
		{
			// only display allowed usergroups
			if (getB2BUserGroupsLookUpStrategy().getUserGroups().contains(role.getUid()))
			{
				roles.add(role.getUid());
			}
		}
		target.setRoles(roles);
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2BUnitService()
	{
		return b2BUnitService;
	}

	@Required
	public void setB2BUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2BUnitService)
	{
		this.b2BUnitService = b2BUnitService;
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

	protected MessageSource getMessageSource()
	{
		return messageSource;
	}

	@Required
	public void setMessageSource(final MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}

	protected I18NService getI18nService()
	{
		return i18nService;
	}

	@Required
	public void setI18nService(final I18NService i18nService)
	{
		this.i18nService = i18nService;
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

}
