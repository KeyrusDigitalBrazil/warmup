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
package de.hybris.platform.cmsfacades.rendering.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemConverter;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.UniqueIdentifierAttributeToDataContentConverter;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.rendering.populators.CMSComponentModelToDataRenderingPopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSComponentModelToDataRenderingPopulatorTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String CATALOG_VERSION = "version";
	private final String COMPONENT_UID = "some uid";
	private final String COMPONENT_NAME = "some component name";
	private final String COMPONENT_TYPE_CODE = "some component type code";
	private final Date MODIFIED_TIME = new Date(System.currentTimeMillis());

	@Mock
	private AbstractCMSComponentModel component;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private CMSItemConverter converter;

	@Mock
	private UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> uniqueIdentifierAttributeToDataContentConverter;

	@InjectMocks
	private CMSComponentModelToDataRenderingPopulator componentRenderingPopulator;

	private Map<String, Object> otherPropertiesMap;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(component.getUid()).thenReturn(COMPONENT_UID);
		when(component.getName()).thenReturn(COMPONENT_NAME);
		when(component.getItemtype()).thenReturn(COMPONENT_TYPE_CODE);
		when(component.getModifiedtime()).thenReturn(MODIFIED_TIME);
		when(component.getCatalogVersion()).thenReturn(catalogVersion);
		when(uniqueIdentifierAttributeToDataContentConverter.convert(catalogVersion)).thenReturn(CATALOG_VERSION);

		otherPropertiesMap = new HashMap<>();
		otherPropertiesMap.put("KEY", "VALUE");
		when(converter.convert(component)).thenReturn(otherPropertiesMap);
	}

	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void whenPopulatorCalled_ItPopulatesAllTheProperties()
	{
		// GIVEN
		AbstractCMSComponentData componentData = new AbstractCMSComponentData();

		// WHEN
		componentRenderingPopulator.populate(component, componentData);

		// THEN
		assertThat(componentData.getUid(), is(COMPONENT_UID));
		assertThat(componentData.getTypeCode(), is(COMPONENT_TYPE_CODE));
		assertThat(componentData.getName(), is(COMPONENT_NAME));
		assertThat(componentData.getModifiedtime(), is(MODIFIED_TIME));
		assertThat(componentData.getCatalogVersion(), is(CATALOG_VERSION));
		assertThat(componentData.getOtherProperties(), is(otherPropertiesMap));
	}
}
