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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link CustomerData} with data form {@link B2BCustomerModel}.
 */
public class B2BCustomerApproverPopulator implements Populator<CustomerModel, CustomerData>
{
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Override
	public void populate(final CustomerModel source, final CustomerData target) throws ConversionException
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		if (source instanceof B2BCustomerModel)
		{
			final B2BCustomerModel customer = (B2BCustomerModel) source;
			final List<CustomerData> approvers = new ArrayList<CustomerData>();
			for (final B2BCustomerModel approver : customer.getApprovers())
			{
				approvers.add(convertApprover(approver));
			}
			target.setApprovers(approvers);
		}
	}

	protected CustomerData convertApprover(final B2BCustomerModel source)
	{
		final CustomerData target = new CustomerData();
		target.setName(source.getName());
		target.setEmail(source.getEmail());
		target.setUid(source.getUid());
		populateRoles(source, target);
		return target;
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
