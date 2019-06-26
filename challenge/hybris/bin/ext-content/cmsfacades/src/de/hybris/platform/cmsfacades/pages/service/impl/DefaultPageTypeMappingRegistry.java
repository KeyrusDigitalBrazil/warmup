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

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMapping;
import de.hybris.platform.cmsfacades.pages.service.PageTypeMappingRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Default implementation of the <code>PageTypeMappingRegistry</code>. This implementation uses autowire-by-type to
 * inject all beans implementing {@link PageTypeMapping}.
 * 
 * @deprecated since 6.6
 */
@Deprecated
@HybrisDeprecation(sinceVersion = "6.6")
public class DefaultPageTypeMappingRegistry implements PageTypeMappingRegistry, InitializingBean
{
	@Autowired
	private Set<PageTypeMapping> allPageTypeMappings;

	private final Map<String, PageTypeMapping> pageTypeMappings = new HashMap<>();

	@Override
	public Optional<PageTypeMapping> getPageTypeMapping(final String typecode)
	{
		return Optional.ofNullable(getPageTypeMappings().get(typecode));
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		getAllPageTypeMappings().forEach(mapping -> getPageTypeMappings().put(mapping.getTypecode(), mapping));
	}

	protected Set<PageTypeMapping> getAllPageTypeMappings()
	{
		return allPageTypeMappings;
	}

	public void setAllPageTypeMappings(final Set<PageTypeMapping> allPageTypeMappings)
	{
		this.allPageTypeMappings = allPageTypeMappings;
	}

	protected Map<String, PageTypeMapping> getPageTypeMappings()
	{
		return pageTypeMappings;
	}

}
