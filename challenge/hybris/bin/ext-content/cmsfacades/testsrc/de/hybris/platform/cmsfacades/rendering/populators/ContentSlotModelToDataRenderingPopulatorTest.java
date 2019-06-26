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
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cmsfacades.cmsitems.attributeconverters.UniqueIdentifierAttributeToDataContentConverter;
import de.hybris.platform.cmsfacades.data.AbstractCMSComponentData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ContentSlotModelToDataRenderingPopulatorTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String SLOT_UID = "some slot uid";
	private final String SLOT_NAME = "some slot name";
	private final String SLOT_POSITION = "some slot position";
	private final String CATALOG_VERSION = "some catalog version";

	@Mock
	private AbstractCMSComponentModel componentModel1;

	@Mock
	private AbstractCMSComponentModel componentModel2;

	@Mock
	private AbstractCMSComponentModel componentModel3;

	@Mock
	private AbstractCMSComponentData convertedComponentData1;

	@Mock
	private AbstractCMSComponentData convertedComponentData2;

	@Mock
	private AbstractCMSComponentData convertedComponentData3;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private ContentSlotModel contentSlotModel;

	@Mock
	private ContentSlotData contentSlotData;

	@Mock
	private RenderingVisibilityService renderingVisibilityService;

	@Mock
	private Converter<AbstractCMSComponentModel, AbstractCMSComponentData> cmsComponentRenderingDataConverter;

	@Mock
	private UniqueIdentifierAttributeToDataContentConverter<CatalogVersionModel> uniqueIdentifierAttributeToDataContentConverter;

	@Spy
	@InjectMocks
	private ContentSlotModelToDataRenderingPopulator contentSlotRenderingPopulator;

	private PageContentSlotData pageContentSlotData;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	@SuppressWarnings("unchecked")
	public void setUp()
	{
		// Slot Data
		pageContentSlotData = new PageContentSlotData();
		when(contentSlotData.getContentSlot()).thenReturn(contentSlotModel);
		when(contentSlotData.getUid()).thenReturn(SLOT_UID);
		when(contentSlotData.getName()).thenReturn(SLOT_NAME);
		when(contentSlotData.getPosition()).thenReturn(SLOT_POSITION);
		when(contentSlotData.isOverrideSlot()).thenReturn(false); // By default is not an override slot.
		when(contentSlotData.isFromMaster()).thenReturn(false); // By default is not coming from master.

		// Catalog Version
		when(contentSlotModel.getCatalogVersion()).thenReturn(catalogVersion);
		when(uniqueIdentifierAttributeToDataContentConverter.convert(catalogVersion)).thenReturn(CATALOG_VERSION);

		// Component Converter
		when(cmsComponentRenderingDataConverter.convert(any())).then(params -> {
			AbstractCMSComponentModel renderingData = params.getArgumentAt(0, AbstractCMSComponentModel.class);

			if (renderingData.equals(componentModel1))
			{
				return convertedComponentData1;
			}
			else if (renderingData.equals(componentModel2))
			{
				return convertedComponentData2;
			}

			return convertedComponentData3;
		});

		// Rendering Utils
		when(renderingVisibilityService.isVisible(any())).thenReturn(true);
	}


	// --------------------------------------------------------------------------
	// Tests
	// --------------------------------------------------------------------------
	@Test
	public void whenPopulatorIsCalled_ThenItPopulatesAllTheRequiredProperties()
	{
		// WHEN
		contentSlotRenderingPopulator.populate(contentSlotData, pageContentSlotData);

		// THEN
		assertThat(pageContentSlotData.getSlotId(), is(SLOT_UID));
		assertThat(pageContentSlotData.getName(), is(SLOT_NAME));
		assertThat(pageContentSlotData.getPosition(), is(SLOT_POSITION));
		assertThat(pageContentSlotData.getCatalogVersion(), is(CATALOG_VERSION));
		assertThat(pageContentSlotData.isSlotShared(), is(false)); // Default should be false.
	}

	@Test
	public void givenSlotHasNoComponents_WhenPopulatorIsCalled_ThenItSetsAnEmptyCollection()
	{
		// GIVEN
		setComponentsInSlot();

		// WHEN
		contentSlotRenderingPopulator.populate(contentSlotData, pageContentSlotData);

		// THEN
		assertThat(pageContentSlotData.getComponents().isEmpty(), is(true));
	}

	@Test
	public void givenSlotHasNonRestrictedComponents_WhenPopulatorIsCalled_ThenItSetsTheConvertedComponents()
	{
		// GIVEN
		setComponentsInSlot(componentModel1, componentModel2, componentModel3);

		// WHEN
		contentSlotRenderingPopulator.populate(contentSlotData, pageContentSlotData);

		// THEN
		assertThat(pageContentSlotData.getComponents(),
				contains(convertedComponentData1, convertedComponentData2, convertedComponentData3));
	}

	@Test
	public void givenSlotHasSomeRestrictedComponents_WhenPopulatorIsCalled_ThenItOnlySetsTheAllowedConvertedComponents()
	{
		// GIVEN
		setComponentsInSlot(componentModel1, componentModel2, componentModel3);
		markComponentAsDisallowed(componentModel3);

		// WHEN
		contentSlotRenderingPopulator.populate(contentSlotData, pageContentSlotData);

		// THEN
		assertThat(pageContentSlotData.getComponents(), contains(convertedComponentData1, convertedComponentData2));
	}

	// --------------------------------------------------------------------------
	// Helper Methods
	// --------------------------------------------------------------------------
	protected void setSlotAsOverride()
	{
		when(contentSlotData.isOverrideSlot()).thenReturn(true);
	}

	protected void setSlotAsFromMaster()
	{
		when(contentSlotData.isFromMaster()).thenReturn(true);
	}

	protected void setComponentsInSlot(AbstractCMSComponentModel... components)
	{
		when(contentSlotModel.getCmsComponents()).thenReturn(Arrays.asList(components));
	}

	protected void markComponentAsDisallowed(AbstractCMSComponentModel componentModel)
	{
		when(renderingVisibilityService.isVisible(componentModel)).thenReturn(false);
	}
}
