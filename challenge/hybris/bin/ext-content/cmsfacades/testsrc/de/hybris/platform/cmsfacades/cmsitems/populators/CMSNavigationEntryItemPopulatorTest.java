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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.ComponentTypeData;
import de.hybris.platform.cmsfacades.types.populator.I18nComponentTypePopulator;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.ObjectFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSNavigationEntryItemPopulatorTest
{
    private static final String TYPE_CODE = "typeCode";
    private static final String TYPE_CODES = "typeCodes";

    private static final String SORT_KEY = "sort";
    private static final String SORT_VALUE = "itemtype:ASC,name:ASC";

    @InjectMocks
    private CMSNavigationEntryItemPopulator populator;

    @Mock
    private ComponentTypeAttributeData targetComponentTypeAttributeData;

    @Mock
    private I18nComponentTypePopulator i18nComponentTypePopulator;

    @Mock
    private TypeService typeService;

    @Mock
    private ObjectFactory<ComponentTypeData> componentTypeDataFactory;

    @Mock
    private ComposedTypeModel subTypeOfCMSItemComponentModel;

    @Mock
    private ComposedTypeModel subTypeLevel2ComponentModel;

    private class SubTypeOfCMSItemModel extends CMSItemModel {}

    @Test
    public void test_GetComponentParams_WithoutSubTypes_ShouldSetTypeCode_SubTypeOfCMSItem()
    {
        // WHEN
        targetComponentTypeAttributeData = new ComponentTypeAttributeData();
        final Map<String, String> paramsMap = populator.getComponentParams(SubTypeOfCMSItemModel.class, targetComponentTypeAttributeData);

        // THEN
        assertThat(paramsMap, hasEntry(TYPE_CODE, "SubTypeOfCMSItem"));
        assertThat(paramsMap, hasEntry(SORT_KEY, SORT_VALUE));
        assertThat(paramsMap.size(), is(2));
    }

    @Test
    public void test_GetComponentParams_WithCMSLinkComponentSubType_ShouldSetTypeCode_CMSLinkComponent()
    {
        // GIVEN
        populator.setTypeCodes(Arrays.asList(CMSLinkComponentModel._TYPECODE));

        // WHEN
        targetComponentTypeAttributeData = new ComponentTypeAttributeData();
        final Map<String, String> paramsMap = populator.getComponentParams(SubTypeOfCMSItemModel.class, targetComponentTypeAttributeData);

        // THEN
        assertThat(paramsMap, hasEntry(TYPE_CODES, CMSLinkComponentModel._TYPECODE));
        assertThat(paramsMap, hasEntry(SORT_KEY, SORT_VALUE));
        assertThat(paramsMap.size(), is(2));
    }

    @Test
    public void test_GetComponentParams_WithTwoSubTypes_ShouldSetTypeCode_WithSameTwoSubTypes()
    {
        // GIVEN
        populator.setTypeCodes(Arrays.asList(CMSLinkComponentModel._TYPECODE, SubTypeOfCMSItemModel._TYPECODE));

        // WHEN
        targetComponentTypeAttributeData = new ComponentTypeAttributeData();
        final Map<String, String> paramsMap = populator.getComponentParams(SubTypeOfCMSItemModel.class, targetComponentTypeAttributeData);

        // THEN
        final List<String> expectedTypeCodes = Arrays.asList(CMSLinkComponentModel._TYPECODE, SubTypeOfCMSItemModel._TYPECODE);
        assertThat(paramsMap, hasEntry(TYPE_CODES, expectedTypeCodes.stream().collect(Collectors.joining(","))));
        assertThat(paramsMap, hasEntry(SORT_KEY, SORT_VALUE));
        assertThat(paramsMap.size(), is(2));
    }

    @Test
    public void test_GetComponentSubTypes_OnItemThatDoesNotMatchPopulatorTypeCodes_ShouldReturnEmptyComponentSubTypes()
    {
        // GIVEN
        populator.setTypeCodes(Arrays.asList(CMSLinkComponentModel._TYPECODE));

        doReturn(subTypeOfCMSItemComponentModel).when(typeService).getComposedTypeForClass(SubTypeOfCMSItemModel.class);
        doReturn(Arrays.asList(subTypeLevel2ComponentModel)).when(subTypeOfCMSItemComponentModel).getAllSubTypes();

        doReturn(new ComponentTypeData()).when(componentTypeDataFactory).getObject();
        doReturn(true).when(subTypeOfCMSItemComponentModel).getAbstract();

        doReturn("SubTypeOfComponentModelLevel2").when(subTypeLevel2ComponentModel).getCode();

        mapI18nKeyPopulator();

        // WHEN
        final Map<String, String> subTypes = populator.getComponentSubTypes(SubTypeOfCMSItemModel.class);

        // THEN
        assertThat(subTypes.size(), is(0));
    }

    @Test
    public void test_GetComponentSubTypesOnItemThatMatchPopulatorTypeCodes_ShouldReturnOneComponentSubType()
    {
        // GIVEN
        populator.setTypeCodes(Arrays.asList(CMSLinkComponentModel._TYPECODE));

        doReturn(subTypeOfCMSItemComponentModel).when(typeService).getComposedTypeForClass(CMSLinkComponentModel.class);
        doReturn(Arrays.asList(subTypeLevel2ComponentModel)).when(subTypeOfCMSItemComponentModel).getAllSubTypes();

        doReturn(new ComponentTypeData()).when(componentTypeDataFactory).getObject();
        doReturn(true).when(subTypeOfCMSItemComponentModel).getAbstract();

        doReturn(CMSLinkComponentModel._TYPECODE).when(subTypeLevel2ComponentModel).getCode();

        mapI18nKeyPopulator();

        // WHEN
        final Map<String, String> subTypes = populator.getComponentSubTypes(CMSLinkComponentModel.class);

        // THEN
        assertThat(subTypes.size(), is(1));
        assertThat(subTypes, hasEntry(CMSLinkComponentModel._TYPECODE, CMSLinkComponentModel._TYPECODE + "Key"));
    }

    @Test
    public void test_GetComponentSubTypes_WithoutSubTypesInPopulator_ShouldReturnTwoSubTypes()
    {
        doReturn(subTypeOfCMSItemComponentModel).when(typeService).getComposedTypeForClass(AbstractCMSComponentModel.class);
        doReturn(Arrays.asList(subTypeLevel2ComponentModel)).when(subTypeOfCMSItemComponentModel).getAllSubTypes();

        doReturn(new ComponentTypeData()).when(componentTypeDataFactory).getObject();
        doReturn(false).when(subTypeOfCMSItemComponentModel).getAbstract();

        doReturn(AbstractCMSComponentModel._TYPECODE).when(subTypeLevel2ComponentModel).getCode();
        doReturn(ComposedTypeModel._TYPECODE).when(subTypeOfCMSItemComponentModel).getCode();

        mapI18nKeyPopulator();

        // WHEN
        final Map<String, String> subTypes = populator.getComponentSubTypes(AbstractCMSComponentModel.class);

        // THEN
        assertThat(subTypes.size(), is(2));
        assertThat(subTypes, hasEntry(AbstractCMSComponentModel._TYPECODE, AbstractCMSComponentModel._TYPECODE + "Key"));
        assertThat(subTypes, hasEntry(ComposedTypeModel._TYPECODE, ComposedTypeModel._TYPECODE + "Key"));
    }

    private void mapI18nKeyPopulator()
    {
        doAnswer((Answer<Void>) invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            ComponentTypeData componentTypeData = (ComponentTypeData)args[1];
            componentTypeData.setI18nKey(((ComposedTypeModel)args[0]).getCode() + "Key");

            return null;
        }).when(i18nComponentTypePopulator).populate(Matchers.any(), Matchers.any(ComponentTypeData.class));
    }
}
