/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.pagescontentslots.service.impl;

import de.hybris.platform.cms2.model.relations.CMSRelationModel;
import de.hybris.platform.cmsfacades.pagescontentslots.service.PageContentSlotConverterRegistry;
import de.hybris.platform.cmsfacades.pagescontentslots.service.PageContentSlotConverterType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Default implementation of the <code>PageContentSlotConverterRegistry</code>. This implementation uses
 * autowire-by-type to inject all beans implementing {@link PageContentSlotConverterType}.
 */
public class DefaultPageContentSlotConverterRegistry implements PageContentSlotConverterRegistry, InitializingBean
{
	@Autowired
	private Set<PageContentSlotConverterType> allPageContentSlotConverterTypes;
	private final Map<Class<? extends CMSRelationModel>, PageContentSlotConverterType> convertersByType = new HashMap<>();

	@Override
	public Optional<PageContentSlotConverterType> getPageContentSlotConverterType(
			final Class<? extends CMSRelationModel> classType)
	{
		return Optional.ofNullable(convertersByType.get(classType));
	}

	@Override
	public Collection<PageContentSlotConverterType> getPageContentSlotConverterTypes()
	{
		return convertersByType.values();
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		getAllPageContentSlotConverterTypes().stream().forEach(converterType -> putOrUpdatePageContentSlot(converterType));
	}

	protected void putOrUpdatePageContentSlot(final PageContentSlotConverterType converterType)
	{
		convertersByType.put(converterType.getClassType(), converterType);
	}

	protected Set<PageContentSlotConverterType> getAllPageContentSlotConverterTypes()
	{
		return allPageContentSlotConverterTypes;
	}

	public void setAllPageContentSlotConverterTypes(final Set<PageContentSlotConverterType> allPageContentSlots)
	{
		this.allPageContentSlotConverterTypes = allPageContentSlots;
	}

}
