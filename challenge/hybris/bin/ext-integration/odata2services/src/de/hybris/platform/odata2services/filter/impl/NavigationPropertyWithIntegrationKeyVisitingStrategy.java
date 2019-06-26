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
import de.hybris.platform.odata2services.odata.integrationkey.IntegrationKeyToODataEntryGenerator;
import de.hybris.platform.odata2services.odata.persistence.InternalProcessingException;
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequest;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This strategy creates a {@link WhereClauseCondition} from a navigation property's integrationKey.
 * For example, if filtering by catalogVersion/integrationKey eq 'Staged|Default', this strategy looks up the CatalogVersion with
 * version = 'Staged' and catalog.id = 'Default', then creates a where clause condition containing the CatalogVersion's PK.
 */
public class NavigationPropertyWithIntegrationKeyVisitingStrategy extends AbstractNavigationPropertyVisitingStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(NavigationPropertyWithIntegrationKeyVisitingStrategy.class);
	
	private IntegrationKeyToODataEntryGenerator integrationKeyConverter;

	@Override
	public boolean isApplicable(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		return leftResult instanceof EdmEntitySet && expression.getLeftOperand() instanceof MemberExpression &&
				getLeftOperandPropertyName(expression).contains("integrationKey");
	}

	@Override
	public WhereClauseConditions createWhereClauseConditionForEqual(final BinaryExpression expression, final BinaryOperator operator, final Object leftResult, final Object rightResult)
	{
		try
		{
			final EdmEntitySet entitySet = (EdmEntitySet) leftResult;
			final String integrationKey = (String) rightResult;
			final ODataEntry entry = getIntegrationKeyConverter().generate(entitySet, integrationKey);
			final ItemLookupRequest itemLookupRequest = getItemLookupRequestFactory().create(getContext(), entitySet, entry, integrationKey);
			final ItemModel itemModel = getItemLookupStrategy().lookup(itemLookupRequest);
			if (itemModel != null)
			{
				final String navPropertyName = getLeftOperandNavPropertyName(expression);
				return new WhereClauseCondition(String.format("{%s} = %s", navPropertyName, itemModel.getPk())).toWhereClauseConditions();
			}
		}
		catch (final EdmException e)
		{
			LOG.error("An exception occurred while visiting the navigation property's integration key", e);
			throw new InternalProcessingException(e);
		}
		return NO_RESULT_CONDITIONS;
	}

	protected IntegrationKeyToODataEntryGenerator getIntegrationKeyConverter()
	{
		return integrationKeyConverter;
	}

	public void setIntegrationKeyConverter(final IntegrationKeyToODataEntryGenerator integrationKeyConverter)
	{
		this.integrationKeyConverter = integrationKeyConverter;
	}
}
