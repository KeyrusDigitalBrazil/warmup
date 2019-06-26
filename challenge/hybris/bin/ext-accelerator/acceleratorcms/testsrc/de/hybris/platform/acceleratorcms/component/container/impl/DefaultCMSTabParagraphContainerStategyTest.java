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
package de.hybris.platform.acceleratorcms.component.container.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorcms.model.components.CMSTabParagraphComponentModel;
import de.hybris.platform.acceleratorcms.model.components.CMSTabParagraphContainerModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for DefaultCMSTabParagraphContainerStategy
 */
@UnitTest
public class DefaultCMSTabParagraphContainerStategyTest
{
	private static final String CMS_TAB_PARAGRAPH_COMPONENT = "CMSTabParagraphComponent";
	private static final String OTHER_COMPONENT_CODE = "others";
	private static final String COMPONENT_UID = "default";

	@Mock
	private TypeService typeService;

	private DefaultCMSTabParagraphContainerStategy cmsTabParagraphContainerStategy;

	private CMSTabParagraphContainerModel container;

	private CMSTabParagraphComponentModel cmsTabParagraphComponent;

	private ComposedTypeModel composedType;

	@Before
	public void prepare()
	{
		MockitoAnnotations.initMocks(this);
		cmsTabParagraphContainerStategy = new DefaultCMSTabParagraphContainerStategy();
		cmsTabParagraphContainerStategy.setTypeService(typeService);
		cmsTabParagraphContainerStategy.setShowContainerForTypes(Arrays.asList(CMS_TAB_PARAGRAPH_COMPONENT));

		container = new CMSTabParagraphContainerModel();
		cmsTabParagraphComponent = new CMSTabParagraphComponentModel();
		container.setSimpleCMSComponents(Arrays.asList(cmsTabParagraphComponent));

		composedType = new ComposedTypeModel();
		composedType.setCode(CMS_TAB_PARAGRAPH_COMPONENT);
	}

	@Test
	public void testNeedShowContainer()
	{
		Mockito.doReturn(composedType).when(typeService).getComposedTypeForClass(Mockito.any());
		final List<AbstractCMSComponentModel> components = (List) container.getSimpleCMSComponents();
		assertTrue(cmsTabParagraphContainerStategy.needShowContainer(components));
	}

	@Test
	public void testGetDisplayComponentsForContainer()
	{
		container.setUid(COMPONENT_UID);
		Mockito.doReturn(composedType).when(typeService).getComposedTypeForClass(Mockito.any());
		assertEquals(cmsTabParagraphContainerStategy.getDisplayComponentsForContainer(container), Arrays.asList(container));

		// should return the simple components of this container
		composedType.setCode(OTHER_COMPONENT_CODE);
		assertEquals(cmsTabParagraphContainerStategy.getDisplayComponentsForContainer(container),
				Arrays.asList(cmsTabParagraphComponent));
	}
}
