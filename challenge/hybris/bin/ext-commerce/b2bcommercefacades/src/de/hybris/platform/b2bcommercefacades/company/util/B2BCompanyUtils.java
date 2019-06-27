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
package de.hybris.platform.b2bcommercefacades.company.util;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;


/**
 * Utility class for b2b commerce facades.
 */
public final class B2BCompanyUtils
{

	public static final String NORMALIZED_CHAR = "_";

	private B2BCompanyUtils()
	{
		// private constructor to avoid instantiation
	}

	/**
	 * Creates a {@link B2BSelectionData} instance.
	 *
	 * @param code
	 * @param selected
	 * @param active
	 * @return The new {@link B2BSelectionData}.
	 */
	public static B2BSelectionData createB2BSelectionData(final String code, final boolean selected, final boolean active)
	{
		final B2BSelectionData b2BSelectionData = new B2BSelectionData();
		b2BSelectionData.setId(code);
		// replace any "non-word" char in the code by the NORMALIZED_CHAR
		b2BSelectionData.setNormalizedCode(code == null ? null : code.replaceAll("\\W", NORMALIZED_CHAR));
		b2BSelectionData.setSelected(selected);
		b2BSelectionData.setActive(active);
		return b2BSelectionData;
	}

	/**
	 * @deprecated "Deprecated since 6.3". Use {@link CommerceUtils#convertPageData(SearchPageData, Converter) instead}.
	 *             Converts a {@link SearchPageData} of type {@literal <}S{@literal >} into one of type {@literal <}T
	 *             {@literal >} using the converter provided.
	 *
	 * @param source
	 * @param converter
	 * @param <S>
	 *           The source type.
	 * @param <T>
	 *           The target type.
	 * @return The new {@link SearchPageData}.
	 */
	@Deprecated
	public static <S, T> SearchPageData<T> convertPageData(final SearchPageData<S> source, final Converter<S, T> converter)
	{
		return CommerceUtils.convertPageData(source, converter);
	}

	/**
	 * Populates a {@link B2BSelectionData} with the roles linked to a given customer.
	 *
	 * @param customerModel
	 * @param b2BSelectionData
	 * @return The {@link B2BSelectionData}.
	 */
	public static B2BSelectionData populateRolesForCustomer(final B2BCustomerModel customerModel,
			final B2BSelectionData b2BSelectionData)
	{
		final List<String> roles = new ArrayList<String>();
		final Set<PrincipalGroupModel> roleModels = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		CollectionUtils.filter(roleModels, PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUnitModel.class)));
		CollectionUtils.filter(roleModels,
				PredicateUtils.notPredicate(PredicateUtils.instanceofPredicate(B2BUserGroupModel.class)));

		final B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy = (B2BUserGroupsLookUpStrategy) Registry
				.getApplicationContext().getBean("b2bUserGroupsLookUpStrategy");

		for (final PrincipalGroupModel role : roleModels)
		{
			// only display allowed usergroups
			if (b2BUserGroupsLookUpStrategy.getUserGroups().contains(role.getUid()))
			{
				roles.add(role.getUid());
			}
		}
		b2BSelectionData.setRoles(roles);

		return b2BSelectionData;
	}
}
