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
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.internal.model.ModelContext;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * <p>
 * Generic DAO to provide generic functionality common to all DAOs.
 * </p>
 * <p/>
 * <p>
 * Just extend from this class providing the generated hybris Item class I and the Model class M.
 * </p>
 * 
 * @deprecated Since 4.4. User {@link DefaultGenericDao}
 */
@Deprecated
public abstract class GenericDao<M extends ItemModel> implements Dao
{
	private final Class<M> modelClass;
	private BaseDao baseDao;
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Instantiates a new generic dao.
	 */
	public GenericDao()
	{
		// get the Class of I
		this.modelClass = (Class<M>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	/**
	 * Finds all models.
	 * 
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public List<M> findAll(final int count, final int start)
	{
		return baseDao.<M> findAllByAttributes(null, null, count, start, modelClass);
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
	public M findUniqueByAttribute(final String attributeName, final Object attributeValue) throws AmbiguousIdentifierException
	{
		return baseDao.<M> findUniqueByAttributes(Collections.singletonMap(attributeName, attributeValue), modelClass);
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
	public M findUniqueByAttributes(final Map<String, Object> attribs) throws AmbiguousIdentifierException
	{
		return baseDao.<M> findUniqueByAttributes(attribs, modelClass);
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
	public M findFirstByAttribute(final String attributeName, final Object attributeValue, final Map<String, Boolean> orderBy)
	{
		return findFirstByAttributes(Collections.singletonMap(attributeName, attributeValue), orderBy);
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
	public M findFirstByAttributes(final Map<String, Object> attribs, final Map<String, Boolean> orderBy)
	{
		final List<M> models = findAllByAttributes(attribs, orderBy, -1, 0);
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
	public List<M> findAllByAttribute(final String attributeName, final Object attributeValue)
	{
		return findAllByAttribute(attributeName, attributeValue, null, -1, 0);
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
	public List<M> findAllByAttribute(final String attributeName, final Object attributeValue, final int count, final int start)
	{
		return findAllByAttribute(attributeName, attributeValue, null, count, start);
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
	public List<M> findAllByAttribute(final String attributeName, final Object attributeValue, final Map<String, Boolean> orderBy,
			final int count, final int start)
	{
		return findAllByAttributes(Collections.singletonMap(attributeName, attributeValue), orderBy, count, start);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 * 
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel} .
	 * @return the list< m>
	 */
	public List<M> findAllByAttributes(final Map<String, Object> attribs)
	{
		return findAllByAttributes(attribs, null, -1, 0);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 * 
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel}.
	 * @param count
	 *           The max. number of models returned.
	 * @param start
	 *           The number of the first record.
	 * @return A list of models.
	 */
	public List<M> findAllByAttributes(final Map<String, Object> attribs, final int count, final int start)
	{
		return findAllByAttributes(attribs, null, count, start);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 * 
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel}.
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
	public List<M> findAllByAttributes(final Map<String, Object> attribs, final Map<String, Boolean> orderByMap, final int count,
			final int start)
	{
		return findAllByAttributes(attribs, orderByMap, count, start, false);
	}

	/**
	 * <p/>
	 * Finds models matching the given attribute map.
	 * <p/>
	 * 
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel}.
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
	 * @return A list of models.
	 */
	public List<M> findAllByAttributes(final Map<String, Object> attribs, final Map<String, Boolean> orderByMap, final int count,
			final int start, final boolean excludeSubtypes)
	{
		return baseDao.<M> findAllByAttributes(attribs, orderByMap, count, start, excludeSubtypes, modelClass);
	}

	/**
	 * <p/>
	 * Counts models matching the given attribute map.
	 * <p/>
	 * 
	 * @param attribs
	 *           Map providing attribute names and values that will be included in the <code>where</code> clause or null.
	 *           The value can be an {@link ItemModel} .
	 * @param excludeSubtypes
	 *           Use if you want to exclude the subtyps so in the case of a Product no Color- or SizeVariants will be
	 *           given back.
	 * @return A list of models.
	 */
	public Integer countAllByAttributes(final Map<String, Object> attribs, final boolean excludeSubtypes)
	{
		return baseDao.countAllByAttributes(attribs, excludeSubtypes, modelClass);
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
	public void save(final M model)
	{
		baseDao.<M> save(model);
	}

	/**
	 * Save all.
	 * 
	 * @param models
	 *           the models
	 */
	public void saveAll(final Collection<M> models)
	{
		baseDao.<M> saveAll(models);
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
	public M copyDirtyAttributes(final M source, final M target)
	{
		return baseDao.<M> copyDirtyAttributes(source, target);
	}

	/**
	 * Copies all attributes that are marked as loaded from <code>model</code> into a new copy of the same
	 * {@link ItemModel} type. Note that both models are detached from the {@link ModelContext} when using this method
	 * you should explicitly call {@link ModelService#attach(Object)}
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
	public M deepClone(final M model, final String... excludeAttributesFromCopy)
	{
		return baseDao.<M> deepClone(model, excludeAttributesFromCopy);
	}

	/**
	 * @param baseDao
	 *           the baseDao to set
	 */
	@Required
	public void setBaseDao(final BaseDao baseDao)
	{
		this.baseDao = baseDao;
	}

	public BaseDao getBaseDao()
	{
		return this.baseDao;
	}

	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}
}
