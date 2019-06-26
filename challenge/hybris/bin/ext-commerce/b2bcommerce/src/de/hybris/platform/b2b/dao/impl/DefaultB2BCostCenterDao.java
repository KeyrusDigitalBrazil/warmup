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

import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;



/**
 * A data access object around {@link B2BCostCenterModel}
 * 
 * @spring.bean b2bCostCenterDao
 */
public class DefaultB2BCostCenterDao extends DefaultGenericDao<B2BCostCenterModel> implements B2BCostCenterDao
{
	public DefaultB2BCostCenterDao()
	{
		super(B2BCostCenterModel._TYPECODE);
	}

	/**
	 * Finds B2BCostCenter by code, If none is found null is returned.
	 * 
	 */
	public B2BCostCenterModel findByCode(final String code)
	{
		final List<B2BCostCenterModel> models = this.find(Collections.singletonMap(B2BCostCenterModel.CODE, code));
		return (models.iterator().hasNext() ? models.iterator().next() : null);
	}

	/**
	 * Returns list of active {@link B2BCostCenterModel}s associated with any B2BUnit in the set passed and having the
	 * matching currency.
	 * 
	 * @param branch
	 * @param currency
	 * @return List of {@link B2BCostCenterModel}
	 */
	public List<B2BCostCenterModel> findActiveCostCentersByBranchAndCurrency(final Set<B2BUnitModel> branch,
			final CurrencyModel currency)
	{

		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT {c:pk} ");
		queryBuilder.append(" FROM {B2BCostCenter as c}");
		queryBuilder.append(" WHERE {c:active} = 1 ");
		queryBuilder.append(" AND {c:currency} = ?currency ");
		queryBuilder.append(" AND {c:unit} in ( ?branch ) ");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
		query.setCount(-1);
		query.setStart(0);
		query.getQueryParameters().put("currency", currency);
		query.getQueryParameters().put("branch", branch);

		final SearchResult<B2BCostCenterModel> result = getFlexibleSearchService().search(query);
		return result.getResult();

	}
}
