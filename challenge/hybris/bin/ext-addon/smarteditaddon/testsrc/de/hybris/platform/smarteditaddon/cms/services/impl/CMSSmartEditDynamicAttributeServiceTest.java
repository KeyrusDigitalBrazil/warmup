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
package de.hybris.platform.smarteditaddon.cms.services.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.registry.CMSComponentContainerRegistry;
import de.hybris.platform.cms2.strategies.CMSComponentContainerStrategy;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.jsp.PageContext;

import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSSmartEditDynamicAttributeServiceTest
{
	private static final String CLASS_ATTRIBUTE = "class";
	private static final String COMPONENT_TYPE_ATTRIBUTE = "data-smartedit-component-type";
	private static final String COMPONENT_ID_ATTRIBUTE = "data-smartedit-component-id";
	private static final String SMART_EDIT_COMPONENT_CLASS = "smartEditComponent";
	private static final String CONTENT_SLOT_TYPE = "ContentSlot";
	private static final String CONTENT_SLOT_UID = "TestSlot";
	private static final String COMPONENT_TYPE = "Component";
	private static final String COMPONENT_UID = "TestComponent";
	private static final String UUID = "uuid";

	@Mock
	private AbstractCMSComponentModel component;

	@Mock
	private CMSComponentContainerStrategy cmsComponentContainerStrategy;

	@Mock
	private AbstractCMSComponentContainerModel containerComponent;

	@Mock
	private SessionService sessionService;

	@Mock
	private CMSComponentContainerRegistry cmsComponentContainerRegistry;

	@Mock
	private ContentSlotModel contentSlot;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@InjectMocks
	@Spy
	private CMSSmartEditDynamicAttributeService cmsSmartEditDynamicAttributeService;

	private ItemData itemData;

	@Before
	public void setUp()
	{
		when(contentSlot.getUid()).thenReturn(CONTENT_SLOT_UID);
		when(contentSlot.getItemtype()).thenReturn(CONTENT_SLOT_TYPE);
		when(contentSlot.getCatalogVersion()).thenReturn(catalogVersion);

		when(component.getUid()).thenReturn(COMPONENT_UID);
		when(component.getItemtype()).thenReturn(COMPONENT_TYPE);
		when(component.getCatalogVersion()).thenReturn(catalogVersion);

		itemData = new ItemData();
		itemData.setItemId(UUID);
		when(uniqueItemIdentifierService.getItemData(contentSlot)).thenReturn(Optional.of(itemData));
		when(uniqueItemIdentifierService.getItemData(catalogVersion)).thenReturn(Optional.of(itemData));

		when(uniqueItemIdentifierService.getItemData(component)).thenReturn(Optional.of(itemData));
		when(uniqueItemIdentifierService.getItemData(component)).thenReturn(Optional.of(itemData));

		when(cmsComponentContainerRegistry.getStrategy(containerComponent)).thenReturn(cmsComponentContainerStrategy);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTheComponentBeWrappedThrowsExceptionIfComponentNull()
	{
		cmsSmartEditDynamicAttributeService.shouldTheComponentBeWrapped(null, contentSlot);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldTheComponentBeWrappedThrowsExceptionIfSlotNull()
	{
		cmsSmartEditDynamicAttributeService.shouldTheComponentBeWrapped(component, null);
	}

	@Test
	public void shouldTheComponentBeWrappedReturnsTrueIfDirectChild()
	{
		when(contentSlot.getCmsComponents()).thenReturn(asList(component));
		assertThat(cmsSmartEditDynamicAttributeService.shouldTheComponentBeWrapped(component, contentSlot), is(true));
	}

	@Test
	public void shouldTheComponentBeWrappedReturnsTrueIfComponentInContainer()
	{
		when(contentSlot.getCmsComponents()).thenReturn(asList(containerComponent));
		when(cmsComponentContainerStrategy.getDisplayComponentsForContainer(containerComponent)).thenReturn(asList(component));
		assertThat(cmsSmartEditDynamicAttributeService.shouldTheComponentBeWrapped(component, contentSlot), is(true));
	}

	@Test
	public void shouldTheComponentBeWrappedReturnsFalseIfComponentIsNeitherDirectChildNorThroughContainer()
	{
		when(contentSlot.getCmsComponents()).thenReturn(asList(component, containerComponent));
		when(cmsComponentContainerStrategy.getDisplayComponentsForContainer(containerComponent)).thenReturn(asList(component));
		assertThat(
				cmsSmartEditDynamicAttributeService.shouldTheComponentBeWrapped(mock(AbstractCMSComponentModel.class), contentSlot),
				is(false));
	}

	@Test
	public void whenComponentShouldBeWrappedAndInPreviewThenDynamicAttributesWillBeAdded()
	{
		when(sessionService.getAttribute(anyString())).thenReturn("previewId");
		doReturn(true).when(cmsSmartEditDynamicAttributeService).shouldTheComponentBeWrapped(component, contentSlot);

		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicComponentAttributes(component,
				contentSlot);

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertEquals("component id attribute does not match expected value", COMPONENT_UID,
				dynamicAttributes.get(COMPONENT_ID_ATTRIBUTE));
		assertEquals("component type attribute does not match expected value", COMPONENT_TYPE,
				dynamicAttributes.get(COMPONENT_TYPE_ATTRIBUTE));
		assertEquals("class does not match the expected value", SMART_EDIT_COMPONENT_CLASS, dynamicAttributes.get(CLASS_ATTRIBUTE));

		verify(sessionService, times(1)).getAttribute("cmsTicketId");
	}

	@Test
	public void whenComponentShouldNotBeWrappedDynamicAttributesWillNotBeAddedToComponent()
	{
		when(sessionService.getAttribute(anyString())).thenReturn("previewId");
		doReturn(false).when(cmsSmartEditDynamicAttributeService).shouldTheComponentBeWrapped(component, contentSlot);

		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicComponentAttributes(component,
				contentSlot);

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertTrue("Dynamic attribute map should be empty but is: " + dynamicAttributes.toString(),
				MapUtils.isEmpty(dynamicAttributes));

		verify(sessionService, times(1)).getAttribute("cmsTicketId");
	}

	@Test
	public void whenNotInPreviewWrappedDynamicAttributesWillNotBeAddedToComponent()
	{
		when(sessionService.getAttribute(anyString())).thenReturn(null);
		doReturn(true).when(cmsSmartEditDynamicAttributeService).shouldTheComponentBeWrapped(component, contentSlot);

		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicComponentAttributes(component,
				contentSlot);

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertTrue("Dynamic attribute map should be empty but is: " + dynamicAttributes.toString(),
				MapUtils.isEmpty(dynamicAttributes));

		verify(sessionService, times(1)).getAttribute("cmsTicketId");
	}

	@Test
	public void whenComponentIsNullDynamicAttributesWillNotBeAddedToComponent()
	{
		when(sessionService.getAttribute(anyString())).thenReturn("previewId");
		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicComponentAttributes(null,
				contentSlot);

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertTrue("Dynamic attribute map should be empty but is: " + dynamicAttributes.toString(),
				MapUtils.isEmpty(dynamicAttributes));

	}

	@Test
	public void whenSlotIsNullDynamicAttributesWillNotBeAddedToComponent()
	{
		when(sessionService.getAttribute(anyString())).thenReturn("previewId");
		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicComponentAttributes(component,
				null);

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertTrue("Dynamic attribute map should be empty but is: " + dynamicAttributes.toString(),
				MapUtils.isEmpty(dynamicAttributes));

	}

	@Test
	public void whenInPreviewDynamicAttributesWillBeAddedOnSlot()
	{
		when(sessionService.getAttribute(anyString())).thenReturn("previewId");
		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService
				.getDynamicContentSlotAttributes(contentSlot, mock(PageContext.class), new HashMap<>());

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertEquals("component id attribute does not match expected value", CONTENT_SLOT_UID,
				dynamicAttributes.get(COMPONENT_ID_ATTRIBUTE));
		assertEquals("component type attribute does not match expected value", CONTENT_SLOT_TYPE,
				dynamicAttributes.get(COMPONENT_TYPE_ATTRIBUTE));
		assertEquals("class does not match the expected value", SMART_EDIT_COMPONENT_CLASS, dynamicAttributes.get(CLASS_ATTRIBUTE));

		verify(sessionService, times(1)).getAttribute("cmsTicketId");
	}

	@Test
	public void whenNotInPreviewDynamicAttributesWillNotBeAddedOnSlot()
	{
		when(sessionService.getAttribute(anyString())).thenReturn(null);
		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicContentSlotAttributes(null,
				mock(PageContext.class), new HashMap<>());

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertTrue("Dynamic attribute map should be empty but is: " + dynamicAttributes.toString(),
				MapUtils.isEmpty(dynamicAttributes));

		verify(sessionService, times(1)).getAttribute("cmsTicketId");
	}

	@Test
	public void dynamicAttributesWillNotBeAddedOnNullSlot()
	{
		when(sessionService.getAttribute(anyString())).thenReturn("previewId");
		final Map<String, String> dynamicAttributes = cmsSmartEditDynamicAttributeService.getDynamicContentSlotAttributes(null,
				mock(PageContext.class), new HashMap<>());

		assertNotNull("Dynamic attribute map should not be null", dynamicAttributes);
		assertTrue("Dynamic attribute map should be empty but is: " + dynamicAttributes.toString(),
				MapUtils.isEmpty(dynamicAttributes));

		verify(sessionService, times(1)).getAttribute("cmsTicketId");
	}
}
