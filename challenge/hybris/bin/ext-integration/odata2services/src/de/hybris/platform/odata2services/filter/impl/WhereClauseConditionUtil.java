/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.filter.impl;

import de.hybris.platform.integrationservices.search.WhereClauseCondition;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;

import java.util.Collections;

/**
 * This class is intended to be used internally and shouldn't be exposed as public
 */
class WhereClauseConditionUtil
{
	private static final WhereClauseCondition NO_RESULT_CONDITION = new WhereClauseCondition("NO_RESULT");
	static final WhereClauseConditions EMPTY_CONDITIONS = new WhereClauseConditions(Collections.emptyList());
	static final WhereClauseConditions NO_RESULT_CONDITIONS = NO_RESULT_CONDITION.toWhereClauseConditions();

	private WhereClauseConditionUtil()
	{
		// Prevent from instantiation
	}

	static boolean containsNoResultCondition(final WhereClauseConditions conditions)
	{
		return conditions.getConditions().stream().anyMatch(NO_RESULT_CONDITION::equals);
	}
}
