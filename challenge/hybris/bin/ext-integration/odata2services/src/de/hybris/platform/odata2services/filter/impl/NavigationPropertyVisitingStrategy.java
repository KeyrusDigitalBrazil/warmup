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

import static de.hybris.platform.odata2services.filter.impl.WhereClauseConditionUtil.NO_RESULT_CONDITIONS;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.search.WhereClauseCondition;
import de.hybris.platform.integrationservices.search.WhereClauseConditions;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;
import de.hybris.platform.odata2services.odata.persistence.lookup.ItemLookupResult;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy creates a {@link WhereClauseCondition} from a navigation property's sub-property that is not an integrationKey.
 * For example, if filtering by catalogVersion/version eq 'Staged', this strategy looks up all CatalogVersions with
 * version = 'Staged' and creates a where clause condition containing the PKs of all the matching CatalogVersions.
 */
public class NavigationPropertyVisitingStrategy extends AbstractNavigationPropertyVisitingStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(NavigationPropertyVisitingStrategy.class);

	@Override
	public boolean isApplicable(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		return leftResult instanceof EdmEntitySet && expression.getLeftOperand() instanceof MemberExpression &&
			!getLeftOperandPropertyName(expression).contains("integrationKey");
	}

	@Override
	protected WhereClauseConditions createWhereClauseConditionForEqual(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		try
		{
			final String property = getLeftOperandPropertyName(expression);
			final ItemLookupRequest itemLookupRequest = getItemLookupRequestFactory().create(getContext(), (EdmEntitySet) leftResult, new ImmutablePair<>(property, (String) rightResult));
			final ItemLookupResult<ItemModel> itemModels = getItemLookupStrategy().lookupItems(itemLookupRequest);
			if (itemModels != null && itemModels.getTotalCount() > 0)
			{
				final String navPropertyName = getLeftOperandNavPropertyName(expression);
				final String pks = itemModels.getEntries().stream().map(m -> m.getPk().toString()).reduce("", (a, b) -> a + b + ",");
				return new WhereClauseCondition(String.format("{%s} IN (%s)", navPropertyName, pks.substring(0, pks.length() - 1))).toWhereClauseConditions();
			}
		}
		catch (final EdmException e)
		{
			LOG.error("An exception occurred while visiting the navigation property's sub-property", e);
			throw new InternalProcessingException(e);
		}
		return NO_RESULT_CONDITIONS;
	}
}
