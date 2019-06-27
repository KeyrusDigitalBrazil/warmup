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
package de.hybris.platform.cmsfacades.cmsitems.populators;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.AttributeDescriptorModelHelperService;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.populator.CMSItemDropdownComponentTypeAttributePopulator;
import de.hybris.platform.cmsfacades.types.populator.I18nComponentTypePopulator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Arrays;
import java.util.Collections;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemDropdownComponentTypeAttributePopulatorTest
{

	@InjectMocks
	private CMSItemDropdownComponentTypeAttributePopulator populator;

	@Mock
	private AttributeDescriptorModel sourceAttributeDescriptor;

	@Mock
	private ComponentTypeAttributeData targetComponentTypeAttributeData;

	@Mock
	private AttributeDescriptorModelHelperService attributeDescriptorModelHelperService;

	@Mock
	private I18nComponentTypePopulator i18nComponentTypePopulator;

	@Mock
	private TypeService typeService;

	@Mock
	private ObjectFactory<ComponentTypeData> componentTypeDataFactory;

	@Mock
	private ComposedTypeModel abstractPageSubTypeComponentModel;

	@Mock
	private ComposedTypeModel subTypeOfCMSItemComponentModel;

	@Mock
	private ComposedTypeModel subTypeLevel2ComponentModel;

	private static final String ID_ATTRIBUTE = "uuid";
	private static final String LABEL_ATTRIBUTE_NAME = "name";
	private static final String LABEL_ATTRIBUTE_UID = "uid";
	private static final String TYPE_CODE = "typeCode";

	private static final String CMS_ITEM_DROPDOWN = "CMSItemDropdown";

	private abstract class SubTypeOfAbstractPageModel extends AbstractPageModel
	{

	}

	private class SubTypeOfCMSItemModel extends CMSItemModel
	{

	}

	@Before
	public void setUp()
	{
		doReturn(new ComponentTypeData()).when(componentTypeDataFactory).getObject();
		doReturn(true).when(abstractPageSubTypeComponentModel).getAbstract();
		doReturn(abstractPageSubTypeComponentModel).when(typeService).getComposedTypeForClass(SubTypeOfAbstractPageModel.class);
		doReturn(Arrays.asList(subTypeLevel2ComponentModel)).when(abstractPageSubTypeComponentModel).getAllSubTypes();
		doReturn("SubTypeOfAbstractPageModel").when(abstractPageSubTypeComponentModel).getCode();

		doReturn(false).when(subTypeOfCMSItemComponentModel).getAbstract();
		doReturn(subTypeOfCMSItemComponentModel).when(typeService).getComposedTypeForClass(SubTypeOfCMSItemModel.class);
		doReturn(Arrays.asList(subTypeLevel2ComponentModel, abstractPageSubTypeComponentModel)).when(subTypeOfCMSItemComponentModel).getAllSubTypes();
		doReturn("SubTypeOfCMSItemModel").when(subTypeOfCMSItemComponentModel).getCode();

		doReturn("SubTypeOfNonAbstractPageModelLevel2").when(subTypeLevel2ComponentModel).getCode();

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final Object[] args = invocationOnMock.getArguments();
				final ComponentTypeData componentTypeData = (ComponentTypeData)args[1];
				componentTypeData.setI18nKey(((ComposedTypeModel)args[0]).getCode() + "Key");

				return null;
			}
		}).when(i18nComponentTypePopulator).populate(Matchers.any(), Matchers.any(ComponentTypeData.class));


		doAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(final InvocationOnMock invocationOnMock) throws Throwable {
				final Object[] args = invocationOnMock.getArguments();
				final ComposedTypeModel componentTypeModel = (ComposedTypeModel) args[1];
				if (componentTypeModel == abstractPageSubTypeComponentModel){
					return true;
				} else if (componentTypeModel == subTypeLevel2ComponentModel){
					return false;
				} else if (componentTypeModel == subTypeOfCMSItemComponentModel){
					return false;
				}
				throw new IllegalArgumentException("unexpected mock argument");
			}
		}).when(typeService).isAssignableFrom(Matchers.any(ComposedTypeModel.class), Matchers.any(ComposedTypeModel.class));

	}

	@Test
	public void testPopulateShouldSetItemDropdownAttributesAlongWithItemSearchParamsForAttributesOfTypeAbstractPageModel()
	{

		// GIVEN
		targetComponentTypeAttributeData = new ComponentTypeAttributeData();
		doReturn(SubTypeOfAbstractPageModel.class).when(attributeDescriptorModelHelperService)
				.getAttributeClass(sourceAttributeDescriptor);
		populator.setSearchParams("pageStatus:active");

		// WHEN
		populator.populate(sourceAttributeDescriptor, targetComponentTypeAttributeData);

		// THEN
		assertThat(targetComponentTypeAttributeData.getCmsStructureType(), is(CMS_ITEM_DROPDOWN));
		assertThat(targetComponentTypeAttributeData.getIdAttribute(), is(ID_ATTRIBUTE));
		assertThat(targetComponentTypeAttributeData.getLabelAttributes(), contains(LABEL_ATTRIBUTE_NAME, LABEL_ATTRIBUTE_UID));

		assertThat(targetComponentTypeAttributeData.getParams(), hasEntry(TYPE_CODE, "SubTypeOfAbstractPage"));
		assertThat(targetComponentTypeAttributeData.getParams(), hasEntry("itemSearchParams", "pageStatus:active"));
		assertThat(targetComponentTypeAttributeData.getSubTypes(), hasEntry("SubTypeOfNonAbstractPageModelLevel2", "SubTypeOfNonAbstractPageModelLevel2Key"));
		assertThat(targetComponentTypeAttributeData.getSubTypes().size(), is(1));
	}

	@Test
	public void testPopulateShouldSetItemDropdownAttributesWithoutItemSearchParamsForAttributesNotOfTypeAbstractPageModel()
	{

		// GIVEN
		targetComponentTypeAttributeData = new ComponentTypeAttributeData();
		doReturn(SubTypeOfCMSItemModel.class).when(attributeDescriptorModelHelperService)
				.getAttributeClass(sourceAttributeDescriptor);

		// WHEN
		populator.populate(sourceAttributeDescriptor, targetComponentTypeAttributeData);

		// THEN
		assertThat(targetComponentTypeAttributeData.getCmsStructureType(), is(CMS_ITEM_DROPDOWN));
		assertThat(targetComponentTypeAttributeData.getIdAttribute(), is(ID_ATTRIBUTE));
		assertThat(targetComponentTypeAttributeData.getLabelAttributes(), contains(LABEL_ATTRIBUTE_NAME, LABEL_ATTRIBUTE_UID));

		assertThat(targetComponentTypeAttributeData.getParams(), hasEntry(TYPE_CODE, "SubTypeOfCMSItem"));
		assertThat(targetComponentTypeAttributeData.getParams(), not(hasEntry("itemSearchParams", "pageStatus:active")));
	}

	@Test
	public void testPopulateShouldIgnoreSubTypesExtendingAbstractPageModel()
	{

		// GIVEN
		targetComponentTypeAttributeData = new ComponentTypeAttributeData();
		doReturn(SubTypeOfCMSItemModel.class).when(attributeDescriptorModelHelperService)
				.getAttributeClass(sourceAttributeDescriptor);

		// WHEN
		populator.populate(sourceAttributeDescriptor, targetComponentTypeAttributeData);

		// THEN
		assertThat(targetComponentTypeAttributeData.getSubTypes(), hasEntry("SubTypeOfNonAbstractPageModelLevel2", "SubTypeOfNonAbstractPageModelLevel2Key"));
		assertThat(targetComponentTypeAttributeData.getSubTypes(), hasEntry("SubTypeOfCMSItemModel", "SubTypeOfCMSItemModelKey"));
		assertThat(targetComponentTypeAttributeData.getSubTypes().size(), is(2));
	}

	@Test
	public void givenNoSupportedSubTypeAndTypeIsAbstract_populate_shouldReturnAnEmptyList()
	{
		// GIVEN
		targetComponentTypeAttributeData = new ComponentTypeAttributeData();
        doReturn(SubTypeOfAbstractPageModel.class).when(attributeDescriptorModelHelperService)
                .getAttributeClass(sourceAttributeDescriptor);
		doReturn(Collections.emptyList()).when(abstractPageSubTypeComponentModel).getAllSubTypes();

		// WHEN
		populator.populate(sourceAttributeDescriptor, targetComponentTypeAttributeData);

		// THEN
		assertThat(targetComponentTypeAttributeData.getSubTypes().size(), is(0));
	}
}
