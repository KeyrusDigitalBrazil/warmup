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
package de.hybris.platform.importcockpit.components.contentbrowser.mapping.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import de.hybris.platform.cockpit.session.BrowserSectionModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.importcockpit.model.mappingview.MappingModel;
import de.hybris.platform.importcockpit.session.mapping.impl.MappingBrowserModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class TargetSectionComponentTest
{
	public static final String CATALOG_VERSION_QUALIFIER = "catalogVersion";
	private TargetSectionComponent targetSectionComponent;

	@Mock
	private BrowserSectionModel sectionModel;

	@Mock
	private MappingBrowserModel browserModel;

	@Mock
	private MappingModel mapping;

	@Mock
	private ComposedTypeModel catalogAwareTypeModel;

	@Mock
	private AttributeDescriptorModel catalogVersionAttribute;

	@Before
	public void initialize()
	{
		initMocks(this);
		targetSectionComponent = new TargetSectionComponent(sectionModel);
		when(sectionModel.getSectionBrowserModel()).thenReturn(browserModel);
		when(browserModel.getMapping()).thenReturn(mapping);
		when(mapping.getBaseTypeModel()).thenReturn(catalogAwareTypeModel);
		when(catalogAwareTypeModel.getCatalogVersionAttribute()).thenReturn(catalogVersionAttribute);
		when(catalogVersionAttribute.getQualifier()).thenReturn(CATALOG_VERSION_QUALIFIER);
		Mockito.doReturn(Boolean.TRUE).when(mapping).isCatalogVersionOption();
	}

	@Test
	public void isAllowedCatalogVersionAttribute()
	{
		assertFalse(targetSectionComponent.isAllowedCatalogVersionAttribute("randomQualifier"));
		assertTrue(targetSectionComponent.isAllowedCatalogVersionAttribute(CATALOG_VERSION_QUALIFIER));
	}

}
