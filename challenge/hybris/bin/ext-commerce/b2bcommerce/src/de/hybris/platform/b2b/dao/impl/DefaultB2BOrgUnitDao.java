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

import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.daos.impl.DefaultOrgUnitDao;


/**
 * Overrides the implementation of commerceservice's {@link DefaultOrgUnitDao<T>#getQuoteForEmployeeQuery()} method by
 * using B2B specific B2B_QUOTE_TO_EMPLOYEE_JOINS clause to build a query string.
 */
public class DefaultB2BOrgUnitDao<T extends OrgUnitModel> extends DefaultOrgUnitDao<T>
{
	protected static final String B2B_QUOTE_TO_EMPLOYEE_JOINS = " JOIN B2BCustomer as cust ON {quote:user} = {cust:pk}"
			+ " JOIN PrincipalGroupRelation as rel1 ON {rel1:source} = {cust:pk}"
			+ " JOIN B2BUnit as b2bunit ON {b2bunit:pk} = {rel1:target}"
			+ " JOIN PrincipalGroupRelation as rel2 ON {rel2:source} = {b2bunit:pk}"
			+ " JOIN OrgUnit as salesunit ON {salesunit:pk} = {rel2:target}"
			+ " JOIN PrincipalGroupRelation as rel3 ON {rel3:target} = {salesunit:pk}"
			+ " JOIN Employee as empl ON {empl:pk} = {rel3:source}";

	/**
	 * Overrides to use B2B hierarchy join clause in the query. Joining the quote table with other tables (e.g.
	 * PrincipipalGroupRelation), so that all quotes associated to the employee's organizational units can be resolved.
	 */
	@Override
	protected String getQuoteForEmployeeQuery()
	{
		return String.format(QUOTE_FOR_EMPLOYEE_QUERY_FORMAT, B2B_QUOTE_TO_EMPLOYEE_JOINS, QUOTE_DISTINCT_CODE_MAX_VERSION_QUERY);
	}

	public DefaultB2BOrgUnitDao(final String typeCode)
	{
		super(typeCode);
	}
}
