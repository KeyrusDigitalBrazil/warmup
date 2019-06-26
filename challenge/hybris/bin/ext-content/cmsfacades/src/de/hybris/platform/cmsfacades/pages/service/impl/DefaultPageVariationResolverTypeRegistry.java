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
package de.hybris.platform.cmsfacades.pages.service.impl;

import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Default implementation of the <code>PageVariationResolverTypeRegistry</code>. This implementation uses
 * autowire-by-type to inject all beans implementing {@link PageVariationResolverType}.
 */
public class DefaultPageVariationResolverTypeRegistry implements PageVariationResolverTypeRegistry, InitializingBean
{
	@Autowired
	private Set<PageVariationResolverType> allPageVariationResolverTypes;

	private final Map<String, PageVariationResolverType> resolversByType = new HashMap<>();

	@Override
	public Optional<PageVariationResolverType> getPageVariationResolverType(final String typecode)
	{
		return Optional.ofNullable(Optional.ofNullable(getResolversByType().get(typecode))
				.orElseGet(() -> getResolversByType().get(AbstractPageModel._TYPECODE)));
	}

	@Override
	public Collection<PageVariationResolverType> getPageVariationResolverTypes()
	{
		return resolversByType.values();
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		getAllPageVariationResolverTypes().stream()
				.forEach(resolverType -> resolversByType.put(resolverType.getTypecode(), resolverType));
	}

	protected Set<PageVariationResolverType> getAllPageVariationResolverTypes()
	{
		return allPageVariationResolverTypes;
	}

	public void setAllPageVariationResolverTypes(final Set<PageVariationResolverType> allPageVariationResolverTypes)
	{
		this.allPageVariationResolverTypes = allPageVariationResolverTypes;
	}

	protected Map<String, PageVariationResolverType> getResolversByType()
	{
		return resolversByType;
	}

}
