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
package de.hybris.platform.integrationservices.search;

import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.MANY_TO_MANY_SOURCE_FIELD;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.MANY_TO_MANY_TARGET_FIELD;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.extractAttributeNameFromFilter;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.getAttributeDescriptorModelFromFilterAndType;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.getItemAlias;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.getRelationAlias;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.getRelationName;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.isAttributeSource;
import static de.hybris.platform.integrationservices.search.ClauseBuilderUtil.isManyToManyRelation;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class FromClauseBuilder
{
	private static final String SELECT_FROM_STATEMENT = "SELECT {%s:" + ItemModel.PK + "} FROM {%s%s AS %s%s%s}";
	private static final String JOIN_STATEMENT = "JOIN %s AS %s ON {%s:" + ItemModel.PK + "} = {%s:%s}";

	private WhereClauseConditions filter;
	private IntegrationObjectItemModel itemModel;
	private ItemTypeMatch itemTypeMatch;

	private FromClauseBuilder()
	{
	}

	static FromClauseBuilder builder()
	{
		return new FromClauseBuilder();
	}

	FromClauseBuilder withFilter(final WhereClauseConditions filter)
	{
		this.filter = filter;
		return this;
	}

	FromClauseBuilder withIntegrationObjectItem(final IntegrationObjectItemModel itemModel)
	{
		this.itemModel = itemModel;
		return this;
	}

	FromClauseBuilder withTypeHierarchyRestriction(final ItemTypeMatch itemTypeMatch)
	{
		this.itemTypeMatch = itemTypeMatch;
		return this;
	}

	String build()
	{
		if (itemModel != null)
		{
			final String joins = createJoinStatements();
			return createSelectStatement(joins);
		}
		return "";
	}

	private String createSelectStatement(final String joins)
	{
		final ComposedTypeModel itemType = itemModel.getType();
		final String itemAlias = getItemAlias(itemModel);
		final String queryItemHierarchy = getQueryItemHierarchy();
		final String spaceBeforeJoins = joins.isEmpty() ? "" : " ";
		return String.format(SELECT_FROM_STATEMENT,
				itemAlias,
				itemType.getCode(),
				queryItemHierarchy,
				itemAlias,
				spaceBeforeJoins,
				joins);
	}

	/**
	 * Gets the symbol that indicates how to query the item in the hierarchy.
	 * This method can be used to override the default symbol. When refactoring
	 * this method, take into consideration other extension developers may have
	 * overridden this method.
	 * @return Query item hierarchy symbol
	 */
	protected String getQueryItemHierarchy()
	{
		if (itemTypeMatch == null)
		{
			final ComposedTypeModel itemType = itemModel.getType();
			return itemType instanceof EnumerationMetaTypeModel ? "" : "*";
		}
		return itemTypeMatch.getValue();
	}

	private String createJoinStatements()
	{
		if (filter != null)
		{
			return filter.getConditions().stream()
					.map(this::buildJoinStatement)
					.filter(StringUtils::isNotBlank)
					.reduce("", this::combineStatements);
		}
		return "";
	}

	private String combineStatements(final String s1, final String s2)
	{
		return (s1.isEmpty() ? "" : (s1 + " ")) + s2;
	}

	private String buildJoinStatement(final WhereClauseCondition condition)
	{
		final ComposedTypeModel itemType = itemModel.getType();
		final Optional<AttributeDescriptorModel> attributeDescriptorModelOptional = getAttributeDescriptorModelFromFilterAndType(condition, itemType);
		return attributeDescriptorModelOptional
				.map(attributeDescriptorModel ->
					isManyToManyRelation(attributeDescriptorModel)
						? buildJoinStatementFromAttribute(attributeDescriptorModel, condition)
						: "")
				.orElse("");
	}

	private String buildJoinStatementFromAttribute(final AttributeDescriptorModel attributeDescriptorModel, final WhereClauseCondition condition)
	{
		final String fieldName = getRelationFieldName((RelationDescriptorModel) attributeDescriptorModel, condition);
		final String relationName = getRelationName(attributeDescriptorModel);
		final String relationAlias = getRelationAlias(attributeDescriptorModel);
		return String.format(JOIN_STATEMENT,
				relationName,
				relationAlias,
				getItemAlias(itemModel),
				relationAlias,
				fieldName);
	}

	private String getRelationFieldName(final RelationDescriptorModel attributeDescriptorModel, final WhereClauseCondition condition)
	{
		return isAttributeSource(attributeDescriptorModel, extractAttributeNameFromFilter(condition))
				? MANY_TO_MANY_TARGET_FIELD
				: MANY_TO_MANY_SOURCE_FIELD;
	}
}
