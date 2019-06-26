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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.Dao;
import de.hybris.platform.servicelayer.internal.model.ModelContext;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.servicelayer.model.ModelContextUtils;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * This dao provides convinience methods to look up models without having to implement your own Dao.
 *
 * @deprecated Since 4.4. Perfer use of {@link de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao}
 */
@Deprecated
public class BaseDao implements Dao
{
	private static final Logger LOG = Logger.getLogger(BaseDao.class);
	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;


	/**
	 * Finds all models.
	 *
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public <M extends ItemModel> List<M> findAll(final int count, final int start, final Class<M> modelClass)
	{
		return findAllByAttributes(null, null, count, start, modelClass);
	}

	/**
	 * <p/>
	 * Finds a model matching the given attribute. If the query returns more than one model a
	 * {@link AmbiguousIdentifierException} is thrown. If no model was found <code>null</code> is returned.
	 * <p/>
	 *
	 * @param attributeName
	 *           The name of the attribute.
	 * @param attributeValue
	 *           The value of the attribute
	 * @return A list of models.
	 * @throws AmbiguousIdentifierException
	 *            If more than one model was found.
	 */
	public <M extends ItemModel> M findUniqueByAttribute(final String attributeName, final Object attributeValue,
			final Class<M> modelClass) throws AmbiguousIdentifierException
	{
		return this.findUniqueByAttributes(Collections.singletonMap(attributeName, attributeValue), modelClass);
	}

	/**
	 * <p/>
	 * Finds a model matching the given attributes. If the query returns more than one model a
	 * {@link AmbiguousIdentifierException} is thrown. If no model was found <code>null</code> is returned.
	 * <p/>
	 *
	 * @param attribs
	 *           A map of attribute names and values
	 * @return A list of models.
	 * @throws AmbiguousIdentifierException
	 *            If more than one model was found.
	 */
	public <M extends ItemModel> M findUniqueByAttributes(final Map<String, Object> attribs, final Class<M> modelClass)
			throws AmbiguousIdentifierException
	{
		final List<M> models = findAllByAttributes(attribs, null, -1, 0, modelClass);
		if (models.isEmpty())
		{
			return null;
		}
		if (models.size() > 1)
		{
			throw new AmbiguousIdentifierException("Found more than one model with the given attributes: "
					+ (attribs == null ? "NULL" : attribs.toString()) + "! The first found record is: " + models.get(0));
		}

		return models.get(0);
	}

	/**
	 * <p/>
	 * Finds a model matching the given attribute. If the query returns more than one model the first model is returned.
	 * If no model was found <code>null</code> is returned.
	 * <p/>
	 *
	 * @param attributeName
	 *           The name of the attribute.
	 * @param attributeValue
	 *           The value of the attribute
	 * @return A list of models.
	 */
	public <M extends ItemModel> M findFirstByAttribute(final String attributeName, final Object attributeValue,
			final Class<M> modelClass)
	{
		return this.<M> findFirstByAttributes(Collections.singletonMap(attributeName, attributeValue), null, modelClass);
	}


	/**
	 * <p/>
	 * Finds a model matching the given attribute. If the query returns more than one model the first model is returned.
	 * If no model was found <code>null</code> is returned.
	 * <p/>
	 *
	 * @param attributeName
	 *           The name of the attribute.
	 * @param attributeValue
	 *           The value of the attribute
	 * @param orderBy
	 *           Map providing attribute names that will be included in the <code>order by</code> clause . The boolean
	 *           value determines whether the sort order should be ascending (<code>true</code>) or descending (
	 *           <code>false</code>).
	 * @return A list of models.
	 */
	public <M extends ItemModel> M findFirstByAttribute(final String attributeName, final Object attributeValue,
			final Map<String, Boolean> orderBy, final Class<M> modelClass)
	{
		return this.<M> findFirstByAttributes(Collections.singletonMap(attributeName, attributeValue), orderBy, modelClass);
	}


	public <M extends ItemModel> M findLastInsertedItem(final Class<M> modelClass)
	{
		// sort by last modified date is Ascending
		final Map<String, Boolean> orderBy = Collections.singletonMap(ItemModel.CREATIONTIME, Boolean.FALSE);

		return this.<M> findFirstByAttributes(null, orderBy, modelClass);
	}

	/**
	 * <p/>
	 * Finds a model matching the given attributes. If the query returns more than one model the first model is returned.
	 * If no model was found <code>null</code> is returned.
	 * <p/>
	 *
	 * @param attribs
	 *           A map of attribute names and values
	 * @param orderBy
	 *           Map providing attribute names that will be included in the <code>order by</code> clause. The boolean
	 *           value determines whether the sort order should be ascending (<code>true</code>) or descending (
	 *           <code>false</code>).
	 * @return A list of models.
	 */
	public <M extends ItemModel> M findFirstByAttributes(final Map<String, Object> attribs, final Map<String, Boolean> orderBy,
			final Class<M> modelClass)
	{
		final List<M> models = findAllByAttributes(attribs, orderBy, -1, 0, modelClass);
		if (models.isEmpty())
		{
			return null;
		}
		return models.get(0);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute.
	 * <p/>
	 *
	 * @param attributeName
	 *           The name of the attribute.
	 * @param attributeValue
	 *           The value of the attribute
	 * @return A list of models.
	 */
	public <M extends ItemModel> List<M> findAllByAttribute(final String attributeName, final Object attributeValue,
			final Class<M> modelClass)
	{
		return this.<M> findAllByAttribute(attributeName, attributeValue, null, -1, 0, modelClass);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute.
	 * <p/>
	 *
	 * @param attributeName
	 *           The name of the attribute.
	 * @param attributeValue
	 *           The value of the attribute
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public <M extends ItemModel> List<M> findAllByAttribute(final String attributeName, final Object attributeValue,
			final int count, final int start, final Class<M> modelClass)
	{
		return this.<M> findAllByAttribute(attributeName, attributeValue, null, count, start, modelClass);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute.
	 * <p/>
	 *
	 * @param attributeName
	 *           The name of the attribute.
	 * @param attributeValue
	 *           The value of the attribute
	 * @param orderBy
	 *           Map providing attribute names that will be included in the <code>order by</code> clause . The boolean
	 *           value determines whether the sort order should be ascending (<code>true</code>) or descending (
	 *           <code>false</code>).
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public <M extends ItemModel> List<M> findAllByAttribute(final String attributeName, final Object attributeValue,
			final Map<String, Boolean> orderBy, final int count, final int start, final Class<M> modelClass)
	{
		return this.<M> findAllByAttributes(Collections.singletonMap(attributeName, attributeValue), orderBy, count, start,
				modelClass);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 *
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel}.
	 * @return the list< m>
	 */
	public <M extends ItemModel> List<M> findAllByAttributes(final Map<String, Object> attribs, final Class<M> modelClass)
	{
		return this.<M> findAllByAttributes(attribs, null, -1, 0, modelClass);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 *
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel} .
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public <M extends ItemModel> List<M> findAllByAttributes(final Map<String, Object> attribs, final int count, final int start,
			final Class<M> modelClass)
	{
		return this.<M> findAllByAttributes(attribs, null, count, start, modelClass);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 *
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel} .
	 * @param orderByMap
	 *           Map providing attribute names that will be included in the <code>order by</code> clause. The boolean
	 *           value determines whether the sort order should be ascending (<code>true</code>) or descending (
	 *           <code>false</code>).
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public <M extends ItemModel> List<M> findAllByAttributes(final Map<String, Object> attribs,
			final Map<String, Boolean> orderByMap, final int count, final int start, final Class<M> modelClass)
	{
		return this.<M> findAllByAttributes(attribs, orderByMap, count, start, false, modelClass);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 *
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel} .
	 * @param orderByMap
	 *           Map providing attribute names that will be included in the <code>order by</code> clause. The boolean
	 *           value determines whether the sort order should be ascending (<code>true</code>) or descending (
	 *           <code>false</code>).
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @param excludeSubtypes
	 *           Use if you want to exclude the subtyps so in the case of a Product no Color- or SizeVariants will be
	 *           given back.
	 * @param modelClass
	 *           Service Layer model class
	 * @return A list of models.
	 */


	public <M extends ItemModel> List<M> findAllByAttributes(final Map<String, Object> attribs,
			final Map<String, Boolean> orderByMap, final int count, final int start, final boolean excludeSubtypes,
			final Class<M> modelClass)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT {pk} FROM {").append(modelService.getModelType(modelClass));

		if (excludeSubtypes)
		{
			stringBuilder.append("!}");
		}
		else
		{
			stringBuilder.append("}");
		}

		if (attribs != null && CollectionUtils.isNotEmpty(attribs.entrySet()))
		{
			stringBuilder.append(" WHERE");
			for (final String attr : attribs.keySet())
			{
				stringBuilder.append(" {").append(attr).append("} = ?").append(attr).append(" AND");
			}
			stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length());
		}
		if (orderByMap != null && CollectionUtils.isNotEmpty(orderByMap.entrySet()))
		{
			stringBuilder.append(" ORDER BY ");
			for (final Entry<String, Boolean> orderBy : orderByMap.entrySet())
			{
				stringBuilder.append("{").append(orderBy.getKey()).append("} ")
						.append(Boolean.TRUE.equals(orderBy.getValue()) ? "ASC" : "DESC").append(", ");
			}
			stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
		}

		if (LOG.isTraceEnabled())
		{
			LOG.trace(stringBuilder.toString() + " attributes: " + attribs);
		}

		final FlexibleSearchQuery query = new FlexibleSearchQuery(stringBuilder.toString());
		query.setCount(count);
		query.setStart(start);
		if (attribs != null)
		{
			query.getQueryParameters().putAll(attribs);
		}
		final SearchResult<M> result = flexibleSearchService.search(query);
		return result.getResult();
	}


	/**
	 * <p/>
	 * Counts models matching the given attribute map.
	 * <p/>
	 *
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel}.
	 * @param excludeSubtypes
	 *           Use if you want to exclude the subtyps so in the case of a Product no Color- or SizeVariants will be
	 *           given back.
	 * @return A list of models.
	 */
	public <M extends ItemModel> Integer countAllByAttributes(final Map<String, Object> attribs, final boolean excludeSubtypes,
			final Class<M> modelClass)
	{
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("SELECT count({pk}) FROM {").append(this.getModelService().getModelType(modelClass));

		if (excludeSubtypes)
		{
			stringBuilder.append("!}");
		}
		else
		{
			stringBuilder.append("}");
		}

		if (attribs != null && CollectionUtils.isNotEmpty(attribs.entrySet()))
		{
			stringBuilder.append(" WHERE");
			for (final String attr : attribs.keySet())
			{
				stringBuilder.append(" {").append(attr).append("} = ?").append(attr).append(" AND");
			}
			stringBuilder.delete(stringBuilder.length() - 4, stringBuilder.length());
		}

		final FlexibleSearchQuery query = new FlexibleSearchQuery(stringBuilder.toString());
		query.setResultClassList(Collections.singletonList(Integer.class));

		if (attribs != null)
		{
			query.getQueryParameters().putAll(attribs);
		}

		final SearchResult<Integer> result = flexibleSearchService.search(query);
		return result.getResult().iterator().next();
	}

	/**
	 * Persists the model.
	 *
	 * @param model
	 *           the model to save
	 * @throws de.hybris.platform.servicelayer.exceptions.ModelSavingException
	 *            if the save operation failed
	 * @see de.hybris.platform.servicelayer.model.ModelService#save(java.lang.Object)
	 */
	public <M extends ItemModel> void save(final M model)
	{
		modelService.save(model);
	}

	/**
	 * Save all.
	 *
	 * @param models
	 *           the models
	 */
	public <M extends ItemModel> void saveAll(final Collection<M> models)
	{
		modelService.saveAll(models);
	}


	/**
	 * Gets the search service.
	 *
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * Sets the search service.
	 *
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * Gets the model service.
	 *
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * Sets the model service.
	 *
	 * @param modelService
	 *           the modelService to set
	 */
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * Copies dirty attributes from <code>source</code> model to the <code>target</code>.
	 *
	 * @param source
	 *           A model to read dirty attributes from.
	 * @param target
	 *           A model to updated with dirty values of the source.
	 * @return An updated <code>target</code> model.
	 */
	public <M extends ItemModel> M copyDirtyAttributes(final M source, final M target)
	{
		final ItemModelContextImpl context = (ItemModelContextImpl) ModelContextUtils.getItemModelContext(source);
		final Set<String> dirtyAttributeQualifiers = context.getValueHistory().getDirtyAttributes();
		for (final String attributeQualifier : dirtyAttributeQualifiers)
		{
			try
			{
				final Object value = modelService.getAttributeValue(source, attributeQualifier);
				if (value != null)
				{
					modelService.setAttributeValue(target, attributeQualifier, value);
				}
			}
			catch (final IllegalArgumentException iae)
			{
				LOG.debug("Failed to copy dirty attribute: " + attributeQualifier, iae);
			}
		}
		return target;
	}

	/**
	 * Copies all attributes that are marked as loaded from <code>model</code> into a new copy of the same
	 * {@link ItemModel} type. Note that both models are detached from the {@link ModelContext} when using this method
	 * you should explicitly call {@link ModelService#attach(Object)}
	 * <p/>
	 * <p/>
	 *
	 * <pre>
	 * The following attributes will not be copied.
	 * Item.CREATION_TIME
	 * Item.MODIFIED_TIME
	 * Item.PK
	 * Item.OWNER
	 * assignedCockpitItemTemplates
	 * allDocuments
	 * synchronizationSources
	 * synchronizedCopies
	 * savedValues
	 * valueHistory
	 *
	 * </pre>
	 *
	 * @param model
	 *           A model to copy
	 * @param excludeAttributesFromCopy
	 *           the exclude attributes from copy
	 * @return A copy of a model.
	 */
	public <M extends ItemModel> M deepClone(final M model, final String... excludeAttributesFromCopy)
	{
		final M copy = modelService.<M> create(model.getClass());
		modelService.detach(model);
		modelService.detach(copy);

		final Set<String> attributesToExclude = new HashSet(Arrays.asList(excludeAttributesFromCopy));
		// attributes to always exclude from a copy
		attributesToExclude.add(ItemModel.CREATIONTIME);
		attributesToExclude.add(ItemModel.MODIFIEDTIME);
		attributesToExclude.add(ItemModel.PK);
		attributesToExclude.add(ItemModel.OWNER);
		attributesToExclude.add("assignedCockpitItemTemplates");
		attributesToExclude.add("allDocuments");
		attributesToExclude.add("synchronizationSources");
		attributesToExclude.add("synchronizedCopies");
		attributesToExclude.add("savedValues");
		attributesToExclude.add("valueHistory");

		final Set<String> attributesOfModel = getAllAttributes(model);
		attributesOfModel.removeAll(attributesToExclude);

		for (final String attributeQualifier : attributesOfModel)
		{
			try
			{
				final Object value = modelService.getAttributeValue(model, attributeQualifier);
				if (value != null)
				{
					modelService.setAttributeValue(copy, attributeQualifier, value);
				}
			}
			catch (final IllegalArgumentException iae)
			{
				LOG.debug("Failed to clone attribute: " + attributeQualifier, iae);
			}
		}
		return copy;
	}

	/**
	 * Get all the attributes an ItemModel instance including all the attributes of its super classes.
	 *
	 * @param model
	 *           An {@link ItemModel} instance.
	 * @return A unmodifiable set of all modifiable attributes of an {@link ItemModel} and its super classes
	 */
	protected Set<String> getAllAttributes(final ItemModel model)
	{
		final Set<String> attributes = new HashSet<String>();
		final List<Field> fields = new ArrayList(Arrays.asList(model.getClass().getDeclaredFields()));
		// get fields of all the super classes of the ItemModel instance
		final List<Class<?>> superClasses = ClassUtils.getAllSuperclasses(model.getClass());
		for (final Class superclass : superClasses)
		{
			fields.addAll(Arrays.asList(superclass.getDeclaredFields()));
		}
		for (final Field field : fields)
		{
			// only get fields that starts with underscore, designated attributes of a model in hybris service layer.
			if (field.getName().startsWith("_"))
			{
				attributes.add(field.getName().substring(1));
			}
		}
		return attributes;
	}

	public <M> M findByBeanProperty(final Collection<M> collection, final String propertyName, final String propertyValue)
	{
		return (M) CollectionUtils.find(collection, new BeanPropertyValueEqualsPredicate(propertyName, propertyValue, true));
	}
}
