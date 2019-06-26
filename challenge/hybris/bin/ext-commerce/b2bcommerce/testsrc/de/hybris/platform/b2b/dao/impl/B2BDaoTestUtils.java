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
package de.hybris.platform.b2b.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import de.hybris.platform.b2b.enums.B2BPeriodRange;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.util.B2BDateUtils;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;


public class B2BDaoTestUtils
{
	private ModelService modelService;
	private CommonI18NService commonI18NService;
	private B2BDateUtils b2bDateUtils;

	public void assertResultsSize(final int expectedSize, final SearchPageData<?> pageData)
	{
		assertNotNull("search page data is null.", pageData);
		assertNotNull("search results are null.", pageData.getResults());
		assertEquals("unexpected number of search results. Expecting:" + expectedSize + ", Get:" + pageData.getResults().size(),
				expectedSize, pageData.getResults().size());
	}

	public B2BUnitModel createUnit(final String name, final String uid)
	{
		final B2BUnitModel unit = modelService.create(B2BUnitModel.class);
		unit.setUid(uid);
		unit.setLocName(name, Locale.ENGLISH);
		unit.setName(name);
		modelService.save(unit);
		return unit;
	}

	public B2BBudgetModel createBudget(final String code, final String name, final B2BUnitModel unit, final BigDecimal budgetValue,
			final B2BPeriodRange range)
	{
		final B2BBudgetModel budgetModel = modelService.create(B2BBudgetModel.class);
		budgetModel.setCode(code);
		budgetModel.setName(name);
		budgetModel.setUnit(unit);
		budgetModel.setBudget(budgetValue);
		budgetModel.setCurrency(commonI18NService.getCurrency("USD"));
		budgetModel.setDateRange(b2bDateUtils.createDateRange(range));
		modelService.save(budgetModel);
		return budgetModel;
	}

	public B2BCostCenterModel createCostCenter(final boolean active, final String code, final String name, final B2BUnitModel unit)
	{
		final B2BCostCenterModel costCenterModel = modelService.create(B2BCostCenterModel.class);
		costCenterModel.setActive(active);
		costCenterModel.setCode(code);
		costCenterModel.setCurrency(commonI18NService.getCurrency("USD"));
		costCenterModel.setName(name);
		costCenterModel.setUnit(unit);
		modelService.save(costCenterModel);
		return costCenterModel;
	}

	public B2BUserGroupModel createUserGroup(final String name, final B2BUnitModel unit)
	{
		final B2BUserGroupModel groupModel = modelService.create(B2BUserGroupModel.class);
		groupModel.setUid(name + System.currentTimeMillis());
		groupModel.setName(name);
		groupModel.setUnit(unit);

		modelService.save(groupModel);
		return groupModel;
	}

	public B2BCustomerModel createCustomer(final String email, final String name, final B2BUnitModel unit,
			final UserGroupModel group)
	{
		final B2BCustomerModel customerModel = modelService.create(B2BCustomerModel.class);
		customerModel.setUid(name + System.currentTimeMillis());
		customerModel.setName(name);
		customerModel.setEmail(email);

		final Set<PrincipalGroupModel> groups = new HashSet<PrincipalGroupModel>(customerModel.getGroups());
		groups.add(unit);
		groups.add(group);
		customerModel.setGroups(groups);

		modelService.save(customerModel);
		return customerModel;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected B2BDateUtils getB2bDateUtils()
	{
		return b2bDateUtils;
	}

	@Required
	public void setB2bDateUtils(final B2BDateUtils b2bDateUtils)
	{
		this.b2bDateUtils = b2bDateUtils;
	}
}
