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
package de.hybris.platform.integrationservices.service.impl;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.service.AttributeDescriptorNotFoundException;
import de.hybris.platform.integrationservices.service.IntegrationObjectService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * The default implementation of the IntegrationObjectService
 */
public class DefaultIntegrationObjectService implements IntegrationObjectService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIntegrationObjectService.class);
	private static final String INTEGRATION_OBJECT_CODE = "integrationObjectCode";
	private static final String INTEGRATION_OBJECT_ITEM_CODE = "integrationObjectItemCode";

	private static final String ITEM_NOT_FOUND_ERROR =
			"The Integration Object Definition of '%s' was not found";
	private static final String MORE_THAN_ONE_ITEM_FOUND_ERROR =
			"The Integration Object and the ItemModel class provided have more than one match, "
					+ "please adjust the Integration Object definition of '%s'";


	private FlexibleSearchService flexibleSearchService;

	@Override
	public IntegrationObjectModel findIntegrationObject(final String integrationObjectCode)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(integrationObjectCode),
				"Integration object code provided cannot be empty or null.");

		final IntegrationObjectModel example = new IntegrationObjectModel();
		example.setCode(integrationObjectCode);

		return getFlexibleSearchService().getModelByExample(example);
	}

	@Override
	public Set<IntegrationObjectItemModel> findAllIntegrationObjectItems(final String integrationObjectCode)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(integrationObjectCode),
				"Integration object code provided cannot be empty or null.");

		LOGGER.debug("Finding all integration object definitions");
		final SearchResult<IntegrationObjectItemModel> query = getFlexibleSearchService().search(
				"SELECT DISTINCT {ioi." + IntegrationObjectItemModel.PK + "} FROM {"
						+ IntegrationObjectItemModel._TYPECODE + " as ioi JOIN "
						+ IntegrationObjectModel._TYPECODE + " as io ON "
						+ "{ioi:" + IntegrationObjectItemModel.INTEGRATIONOBJECT + "} = "
						+ "{io:" + IntegrationObjectModel.PK + "}} "
						+ "WHERE {io.code}=?integrationObjectCode",
				Collections.singletonMap(INTEGRATION_OBJECT_CODE, integrationObjectCode));

		final Set<IntegrationObjectItemModel> results = new HashSet<>(query.getResult());
		LOGGER.debug("Found {} integration object definitions", results.size());
		return results;
	}

	@Override
	public Set<IntegrationObjectItemModel> findAllDependencyTypes(final String integrationObjectItemCode, final String integrationObjectCode)
	{
		final Set<IntegrationObjectItemModel> dependencyTypes = new HashSet<>();
		LOGGER.debug("Finding all dependency types for integration object '{}'", integrationObjectItemCode);
		if (isValidIntegrationObjectItemCode(integrationObjectItemCode))
		{
			final Optional<IntegrationObjectItemModel> integrationObjectOptional = findIntegrationObjectItem(integrationObjectItemCode, integrationObjectCode);
			integrationObjectOptional.ifPresent(integrationObjectItemModel -> {
				dependencyTypes.add(integrationObjectItemModel);
				accumulateDependencyTypesFromAttributeDefinitions(integrationObjectCode, integrationObjectItemCode,
						integrationObjectItemModel.getAttributes(), dependencyTypes);
			});
		}
		LOGGER.debug("Found {} dependency types for integration object '{}'", dependencyTypes.size(), integrationObjectItemCode);
		return dependencyTypes;
	}

	@Override
	public Set<IntegrationObjectModel> findAllIntegrationObjects(final String itemTypeCode)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(itemTypeCode),
				"Type code provided cannot be empty or null.");

		LOGGER.debug("Finding all associated integration object definition for {}", itemTypeCode);

		final String sql = "SELECT DISTINCT {io." + IntegrationObjectModel.PK + "}"
				+ " FROM {" + IntegrationObjectModel._TYPECODE + " as io"
				+ " JOIN  " + IntegrationObjectItemModel._TYPECODE + " as ioi"
				+ "	ON {io:" + IntegrationObjectModel.PK + "} = {ioi:" + IntegrationObjectItemModel.INTEGRATIONOBJECT + "} JOIN "
				+ ComposedTypeModel._TYPECODE + " as ct "
				+ "	ON {ioi:" + IntegrationObjectItemModel.TYPE + "} = {ct:" + ComposedTypeModel.PK + "}} "
				+ "WHERE {ct.code}=?itemTypeCode";

		final Map<String, String> paramsMap = new HashMap<>();
		paramsMap.put("itemTypeCode", itemTypeCode);

		final SearchResult<IntegrationObjectModel> query = getFlexibleSearchService().search(sql, paramsMap);

		return new HashSet<>(query.getResult());
	}

	@Override
	public AttributeDescriptorModel findAttributeDescriptor(final String integrationObjectCode,
			final String integrationObjectItemCode, final String integrationObjectItemAttributeName)
	{
		final Optional<IntegrationObjectItemModel> integrationObjectItem =
				findIntegrationObjectItem(integrationObjectItemCode, integrationObjectCode);

		if (integrationObjectItem.isPresent())
		{
			return integrationObjectItem.get().getAttributes().stream()
										.filter(byName(integrationObjectItemAttributeName))
										.map(toAttributeDescriptor())
										.findFirst()
										.orElseThrow(() -> new AttributeDescriptorNotFoundException(integrationObjectCode, integrationObjectItemCode, integrationObjectItemAttributeName));
		}
		throw new AttributeDescriptorNotFoundException(integrationObjectCode, integrationObjectItemCode, integrationObjectItemAttributeName);
	}

	@Override
	public String findItemAttributeName(final String integrationObjectCode, final String integrationObjectItemCode, final String integrationObjectItemAttributeName)
	{
		final Optional<IntegrationObjectItemModel> integrationObjectItem = findIntegrationObjectItem(integrationObjectItemCode, integrationObjectCode);
		if (integrationObjectItem.isPresent())
		{
			return integrationObjectItem.get()
					.getAttributes()
					.stream()
					.filter(byName(integrationObjectItemAttributeName))
					.map(toAttributeDescriptor())
					.findFirst()
					.orElseThrow(() -> new AttributeDescriptorNotFoundException(integrationObjectCode, integrationObjectItemCode, integrationObjectItemAttributeName))
					.getQualifier();
		}
		throw new AttributeDescriptorNotFoundException(integrationObjectCode, integrationObjectItemCode, integrationObjectItemAttributeName);
	}

	@Override
	public String findItemTypeCode(final String integrationObjectCode, final String integrationObjectItemCode)
	{
		Preconditions.checkArgument(StringUtils.isNotBlank(integrationObjectCode),
				"integrationObjectCode cannot be empty or blank");
		Preconditions.checkArgument(StringUtils.isNotBlank(integrationObjectItemCode),
				"integrationObjectItemCode cannot be empty or blank");
		LOGGER.debug("Finding type code for integrationObject {} and integrationObjectItem {}", integrationObjectCode,
				integrationObjectItemCode);

		final Map<String, String> paramMap = new HashMap<>();
		paramMap.put(INTEGRATION_OBJECT_CODE, integrationObjectCode);
		paramMap.put(INTEGRATION_OBJECT_ITEM_CODE, integrationObjectItemCode);

		final SearchResult<ComposedTypeModel> query = getFlexibleSearchService().search(
				"SELECT {ioi." + IntegrationObjectItemModel.TYPE + "} " +
						"FROM {" + IntegrationObjectItemModel._TYPECODE + " as ioi " +
						"JOIN " + IntegrationObjectModel._TYPECODE + " as io " +
						"ON {ioi:" + IntegrationObjectItemModel.INTEGRATIONOBJECT + "} = " +
						"{io:" + IntegrationObjectModel.PK + "}} "
						+ "WHERE {io.code}=?" + INTEGRATION_OBJECT_CODE +
						" AND {ioi.code}=?" + INTEGRATION_OBJECT_ITEM_CODE,
				paramMap);

		final List<ComposedTypeModel> queryResult = query.getResult();
		return queryResult.isEmpty() ? "" : queryResult.get(0).getCode();
	}

	@Override
	public IntegrationObjectItemModel findIntegrationObjectItemByTypeCode(final String integrationObjectCode, final String typeCode)
	{
		final String queryString =
				"SELECT DISTINCT {inteObjItem." + IntegrationObjectItemModel.PK + "}"
						+ " FROM {" + IntegrationObjectItemModel._TYPECODE + " as inteObjItem"
						+ " JOIN " + IntegrationObjectModel._TYPECODE + " as inteObj"
						+ " ON {inteObjItem:" + IntegrationObjectItemModel.INTEGRATIONOBJECT + "} = {inteObj:" + IntegrationObjectModel.PK + "}"
						+ " JOIN " + ComposedTypeModel._TYPECODE + " as ct"
						+ " ON {inteObjItem:" + IntegrationObjectItemModel.TYPE + "} = {ct:" + ComposedTypeModel.PK + "}}"
						+ " WHERE {inteObj." + IntegrationObjectModel.CODE + "}=?integrationObjectCode AND {ct." + ComposedTypeModel.CODE + "}=?typeCode";

		final Map<String, Object> params = Maps.newHashMap();
		params.put(INTEGRATION_OBJECT_CODE, integrationObjectCode);
		params.put("typeCode", typeCode);

		final SearchResult<IntegrationObjectItemModel> search = getFlexibleSearchService().search(queryString, params);
		final List<IntegrationObjectItemModel> list = search.getResult();
		if (list.isEmpty())
		{
			throw new ModelNotFoundException(
					String.format(ITEM_NOT_FOUND_ERROR, integrationObjectCode));
		}
		else if (list.size() > 1)
		{
			throw new AmbiguousIdentifierException(
					String.format(MORE_THAN_ONE_ITEM_FOUND_ERROR, integrationObjectCode));
		}

		return list.get(0);
	}

	protected Function<IntegrationObjectItemAttributeModel, AttributeDescriptorModel> toAttributeDescriptor()
	{
		return IntegrationObjectItemAttributeModel::getAttributeDescriptor;
	}

	protected Predicate<IntegrationObjectItemAttributeModel> byName(final String attributeName)
	{
		return attr -> attr.getAttributeName().equals(attributeName);
	}

	protected Optional<IntegrationObjectItemModel> findIntegrationObjectItem(final String integrationObjectItemCode, final String integrationObjectCode)
	{
		LOGGER.debug("Finding integration object definition for {}", integrationObjectItemCode);
		List<IntegrationObjectItemModel> results = Collections.emptyList();

		if (isValidIntegrationObjectItemCode(integrationObjectItemCode))
		{
			try
			{
				final Map<String, String> paramsMap = new HashMap<>();
				paramsMap.put(INTEGRATION_OBJECT_ITEM_CODE, integrationObjectItemCode);
				paramsMap.put(INTEGRATION_OBJECT_CODE, integrationObjectCode);

				final SearchResult<IntegrationObjectItemModel> query = getFlexibleSearchService().search(
						"SELECT {ioi." + IntegrationObjectItemModel.PK + "} " +
								"FROM {" + IntegrationObjectItemModel._TYPECODE + " as ioi " +
								"JOIN " + IntegrationObjectModel._TYPECODE + " as io " +
								"ON {ioi:" + IntegrationObjectItemModel.INTEGRATIONOBJECT + "} = " +
								"{io:" + IntegrationObjectModel.PK + "}} "
								+ "WHERE {ioi.code}=?" + INTEGRATION_OBJECT_ITEM_CODE
								+ " AND {io.code}=?" + INTEGRATION_OBJECT_CODE,
						paramsMap);

				results = query.getResult();
			}
			catch (final RuntimeException e)
			{
				LOGGER.error("Failed finding integration definition for '{}'", integrationObjectItemCode, e);
			}
		}
		LOGGER.debug("Found {} result(s) for integration object definition '{}'", results.size(), integrationObjectItemCode);
		return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
	}

	protected void accumulateDependencyTypesFromAttributeDefinitions(
			final String integrationObjectCode,
			final String integrationObjectItemCode,
			final Set<IntegrationObjectItemAttributeModel> attributeDefinitionModels,
			final Set<IntegrationObjectItemModel> dependencyTypes)
	{
		if (attributeDefinitionModels != null)
		{
			attributeDefinitionModels.forEach(attributeDefinitionModel -> {
				final AttributeDescriptorModel attributeDescriptor = attributeDefinitionModel.getAttributeDescriptor();

				if (attributeDefinitionModel.getReturnIntegrationObjectItem() != null)
				{
					if (StringUtils.isBlank(attributeDefinitionModel.getReturnIntegrationObjectItem().getCode()) && isValidAttributeDescriptor(attributeDescriptor))
					{
						LOGGER.debug("Attribute descriptor is valid, accumulating dependency types for attribute");
						accumulateAllDependencyTypes(integrationObjectCode, attributeDescriptor.getAttributeType().getCode(), dependencyTypes);
					}
					else if (isValidReturnIntegrationObjectItem(attributeDefinitionModel.getReturnIntegrationObjectItem().getCode(), integrationObjectItemCode))
					{
						LOGGER.debug("Return integration object is valid, accumulating dependency types for attribute");
						accumulateAllDependencyTypes(integrationObjectCode, attributeDefinitionModel.getReturnIntegrationObjectItem().getCode(), dependencyTypes);
					}
				}
			});
		}
	}

	protected void accumulateAllDependencyTypes(final String integrationObjectCode, final String integrationObjectItemCode, final Set<IntegrationObjectItemModel> dependencyTypes)
	{
		final Optional<IntegrationObjectItemModel> integrationObjectOptional = findIntegrationObjectItem(integrationObjectItemCode, integrationObjectCode);
		integrationObjectOptional.ifPresent(integrationObjectItemModel -> {
			if (!dependencyTypes.contains(integrationObjectItemModel))
			{
				dependencyTypes.add(integrationObjectItemModel);
				accumulateDependencyTypesFromAttributeDefinitions(integrationObjectCode, integrationObjectItemCode, integrationObjectItemModel.getAttributes(), dependencyTypes);
			}
		});
	}

	protected boolean isValidAttributeDescriptor(final AttributeDescriptorModel attributeDescriptor)
	{
		return attributeDescriptor != null
				&& attributeDescriptor.getAttributeType() instanceof ComposedTypeModel;
	}

	protected boolean isValidReturnIntegrationObjectItem(final String returnIntegrationObjectItemCode, final String integrationObjectItemCode)
	{
		return returnIntegrationObjectItemCode != null
				&& !returnIntegrationObjectItemCode.equalsIgnoreCase(integrationObjectItemCode);
	}

	protected boolean isValidIntegrationObjectItemCode(final String integrationObjectItemCode)
	{
		return StringUtils.isNotBlank(integrationObjectItemCode);
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
