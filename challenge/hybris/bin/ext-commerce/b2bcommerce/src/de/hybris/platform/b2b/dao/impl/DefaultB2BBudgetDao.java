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

import de.hybris.platform.b2b.dao.B2BBudgetDao;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

import java.util.Collections;
import java.util.List;


/**
 * A data access object around {@link B2BBudgetModel}
 *
 * @spring.bean b2bBudgetDao
 */
public class DefaultB2BBudgetDao extends DefaultGenericDao<B2BBudgetModel> implements B2BBudgetDao
{

	public DefaultB2BBudgetDao()
	{
		super(B2BBudgetModel._TYPECODE);
	}

	@Override
	public B2BBudgetModel findBudgetByCode(final String code)
	{
		final List<B2BBudgetModel> budgets = this.find(Collections.singletonMap(B2BBudgetModel.CODE, code));
		return (budgets.iterator().hasNext() ? budgets.iterator().next() : null);
	}
}
