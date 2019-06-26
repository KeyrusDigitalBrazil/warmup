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
package de.hybris.platform.b2bcommercefacades.util;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.servicelayer.model.AbstractItemModel;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;


public final class B2BCommercefacadesTestUtils
{
	private B2BCommercefacadesTestUtils()
	{
		// private constructor to avoid initialization
	}

	public static B2BCostCenterData createB2BCostCenterData(final String originalCode, final String code, final String name,
			final String isoCode, final B2BUnitData unit)
	{
		final B2BCostCenterData b2BCostCenterData = new B2BCostCenterData();
		b2BCostCenterData.setOriginalCode(originalCode);
		b2BCostCenterData.setCode(code);
		b2BCostCenterData.setName(name);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode(isoCode);
		b2BCostCenterData.setCurrency(currencyData);
		b2BCostCenterData.setUnit(unit);

		return b2BCostCenterData;
	}

	public static List<B2BUserGroupData> getSelectedUserGroups(final List<B2BUserGroupData> userGroups)
	{
		final List<B2BUserGroupData> results = new ArrayList<>();
		for (final B2BUserGroupData userGroup : userGroups)
		{
			if (userGroup.isSelected())
			{
				results.add(userGroup);
			}
		}
		return results;
	}

	public static List<CustomerData> getSelectedUsers(final List<CustomerData> users)
	{
		final List<CustomerData> results = new ArrayList<>();
		for (final CustomerData user : users)
		{
			if (user.isSelected())
			{
				results.add(user);
			}
		}
		return results;
	}

	public static boolean isUserIncluded(final Collection<CustomerData> users, final String userUid)
	{
		return CollectionUtils.find(users, new BeanPropertyValueEqualsPredicate(B2BCustomerModel.UID, userUid)) != null;
	}

	public static boolean isCustomerIncluded(final Collection<? extends PrincipalData> customers, final String customerUid)
	{
		return CollectionUtils.find(customers, new BeanPropertyValueEqualsPredicate(B2BCustomerModel.UID, customerUid)) != null;
	}

	public static boolean isUserGroupIncluded(final Collection<B2BUserGroupData> users, final String userGroupUid)
	{
		return CollectionUtils.find(users, new BeanPropertyValueEqualsPredicate(B2BUserGroupModel.UID, userGroupUid)) != null;
	}

	public static ItemModelContextImpl getContext(final AbstractItemModel model)
	{
		return (ItemModelContextImpl) ModelContextUtils.getItemModelContext(model);
	}
}
