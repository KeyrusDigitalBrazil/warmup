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

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.RelationDescriptorModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;

import java.util.Locale;
import java.util.Optional;

class ClauseBuilderUtil
{
	private static final String MANY = "many";
	static final String MANY_TO_MANY_SOURCE_FIELD = "source";
	static final String MANY_TO_MANY_TARGET_FIELD = "target";

	private ClauseBuilderUtil() {}

	static Optional<AttributeDescriptorModel> getAttributeDescriptorModelFromFilterAndType(final WhereClauseCondition filter,
			final ComposedTypeModel itemType)
	{
		if (filter != null)
		{
			final String attributeName = extractAttributeNameFromFilter(filter);
			return itemType.getDeclaredattributedescriptors().stream()
					.filter(attr -> attr instanceof RelationDescriptorModel)
					.filter(attr -> attributeRelationRoleMatchesAttributeName((RelationDescriptorModel) attr, attributeName))
					.findAny();
		}
		return Optional.empty();
	}

	static String extractAttributeNameFromFilter(final WhereClauseCondition filter)
	{
		// example filter condition "{supercategories} = 8796093055118" -> returns supercategories
		final String condition = filter.getCondition();
		final int openCurlyIndex = condition.indexOf('{');
		final int closeCurlyIndex = condition.indexOf('}');
		if (openCurlyIndex > -1 && closeCurlyIndex > -1)
		{
			return condition.substring(openCurlyIndex + 1, closeCurlyIndex);
		}
		return "";
	}

	static String extractAttributeValueFromFilter(final WhereClauseCondition filter)
	{
		// example filter condition "{supercategories} = 8796093055118" -> returns 8796093055118
		final String filterCondition = filter.getCondition();
		final String operator = getOperator(filterCondition);
		final int operatorIndex = filterCondition.lastIndexOf(operator);
		if (operatorIndex > -1)
		{
			return filterCondition.substring(operatorIndex + operator.length());
		}
		return "";
	}

	static String getRelationAlias(final AttributeDescriptorModel attributeDescriptorModel)
	{
		return getRelationName(attributeDescriptorModel).toLowerCase(Locale.ENGLISH);
	}

	static String getItemAlias(final IntegrationObjectItemModel itemModel)
	{
		return itemModel.getType().getCode().toLowerCase(Locale.ENGLISH);
	}

	static boolean isManyToManyRelation(final AttributeDescriptorModel attributeDescriptorModel)
	{
		if (attributeDescriptorModel instanceof RelationDescriptorModel)
		{
			final RelationDescriptorModel relationDescriptorModel = (RelationDescriptorModel) attributeDescriptorModel;
			return isManySourceRelation(relationDescriptorModel) && isManyTargetRelation(relationDescriptorModel);
		}
		return false;
	}

	static String getRelationName(final AttributeDescriptorModel attributeDescriptorModel)
	{
		final RelationDescriptorModel relationDescriptorModel = (RelationDescriptorModel) attributeDescriptorModel;
		return relationDescriptorModel.getRelationName();
	}

	static boolean isAttributeSource(final RelationDescriptorModel attr, final String attributeName)
	{
		final String sourceTypeRole = attr.getRelationType().getSourceTypeRole();
		return sourceTypeRole != null && sourceTypeRole.equalsIgnoreCase(attributeName);
	}

	private static boolean isAttributeTarget(final RelationDescriptorModel attr, final String attributeName)
	{
		final String targetTypeRole = attr.getRelationType().getTargetTypeRole();
		return targetTypeRole != null && targetTypeRole.equalsIgnoreCase(attributeName);
	}

	private static boolean attributeRelationRoleMatchesAttributeName (final RelationDescriptorModel attr, final String attributeName)
	{
		return isAttributeSource(attr, attributeName) || isAttributeTarget(attr, attributeName);
	}

	private static String getOperator(final String filterCondition)
	{
		return filterCondition.contains(" = ") ? "= " : "IN ";
	}

	private static boolean isManySourceRelation(final RelationDescriptorModel relationDescriptorModel)
	{
		return relationDescriptorModel.getRelationType().getSourceTypeCardinality().getCode().equals(MANY);
	}

	private static boolean isManyTargetRelation(final RelationDescriptorModel relationDescriptorModel)
	{
		return relationDescriptorModel.getRelationType().getTargetTypeCardinality().getCode().equals(MANY);
	}
}
