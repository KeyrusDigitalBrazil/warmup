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

package de.hybris.platform.integrationservices.model;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * Provides calculation of the dynamic {@code partOf} attribute on the {@link IntegrationObjectItemAttributeModel}
 */
public class UniqueAttributesAttributeHandler extends AbstractDynamicAttributeHandler<Collection<IntegrationObjectItemAttributeModel>, IntegrationObjectItemModel>
{
	/**
	 * Reads value of the {@code partOf} attribute
	 *
	 * @param model a model object to read the value from.
	 * @return a collection of unique attributes for the given {@link IntegrationObjectItemModel}.
	 */
	@Override
	@NotNull
	public Collection<IntegrationObjectItemAttributeModel> get(final IntegrationObjectItemModel model)
	{
		return internalGet(model, Sets.newHashSet());
	}

	private Collection<IntegrationObjectItemAttributeModel> internalGet(final IntegrationObjectItemModel model, final Set<String> cache)
	{
		assert model != null : "Platform does not pass null models into the dynamic attribute handlers";

		cache.add(model.getCode());

		final Collection<IntegrationObjectItemAttributeModel> keyAttributes = extractSimpleKeyAttributesFrom(model);
		keyAttributes.addAll(extractKeyAttributesFromItemReferences(model, cache));
		return keyAttributes;
	}

	protected Set<IntegrationObjectItemAttributeModel> extractSimpleKeyAttributesFrom(final IntegrationObjectItemModel model)
	{
		return model.getAttributes().stream()
				.filter(this::isUniqueSimpleAttribute)
				.collect(Collectors.toSet());
	}

	protected Collection<IntegrationObjectItemAttributeModel> extractKeyAttributesFromItemReferences(final IntegrationObjectItemModel item, final Set<String> cache)
	{
		return item.getAttributes().stream()
				.filter(this::isUniqueReferenceAttribute)
				.filter(attributeModel -> doesNotCreateCyclicDependency(attributeModel, cache))
				.map(IntegrationObjectItemAttributeModel::getReturnIntegrationObjectItem)
				.map(model -> internalGet(model, Sets.newHashSet(cache)))
				.flatMap(Collection::stream)
				.collect(Collectors.toSet());
	}

	protected boolean isUniqueSimpleAttribute(final IntegrationObjectItemAttributeModel attr)
	{
		return attr.getReturnIntegrationObjectItem() == null && ModelUtils.isUnique(attr);
	}

	protected boolean isUniqueReferenceAttribute(final IntegrationObjectItemAttributeModel attr)
	{
		return attr.getReturnIntegrationObjectItem() != null && ModelUtils.isUnique(attr);
	}

	protected boolean doesNotCreateCyclicDependency(final IntegrationObjectItemAttributeModel attr,
			final Set<String> cache)
	{
		Preconditions.checkState(!cache.contains(attr.getReturnIntegrationObjectItem().getCode()),
				"Metadata error: key attribute '%s' in item type '%s' forms a cyclic return type dependency",
				attr.getAttributeName(), attr.getIntegrationObjectItem().getCode());

		return true;
	}
}
