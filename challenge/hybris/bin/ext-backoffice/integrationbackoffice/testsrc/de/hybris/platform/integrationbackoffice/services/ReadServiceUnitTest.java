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
package de.hybris.platform.integrationbackoffice.services;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ReadServiceUnitTest {

    @Mock
    private FlexibleSearchService flexibleSearchService;
    @Mock
    private TypeService typeService;
    @Mock
    private SchemaGenerator schemaGenerator;

    private ReadService readService;

    @Before
    public void setUp() {
        readService = new ReadService();
        readService.setFlexibleSearchService(flexibleSearchService);
        readService.setTypeService(typeService);
        readService.setODataDefaultSchemaGenerator(schemaGenerator);
        when(typeService.isAssignableFrom("CollectionType", "CollectionType")).thenReturn(true);
        when(typeService.isAssignableFrom("ComposedType", "ComposedType")).thenReturn(true);
        when(typeService.isAssignableFrom("EnumerationMetaType", "EnumerationMetaType")).thenReturn(true);
        when(typeService.isAssignableFrom("AtomicType", "AtomicType")).thenReturn(true);
        when(typeService.isAssignableFrom("MapType", "MapType")).thenReturn(true);
    }

    @Test
    public void testComplexType() {
        final TypeModel typeModel1 = mock(TypeModel.class);
        when(typeModel1.getItemtype()).thenReturn("CollectionType");
        final TypeModel typeModel2 = mock(TypeModel.class);
        when(typeModel2.getItemtype()).thenReturn("ComposedType");
        final TypeModel typeModel3 = mock(TypeModel.class);
        when(typeModel3.getItemtype()).thenReturn("EnumerationMetaType");
        final TypeModel typeModel4 = mock(TypeModel.class);
        when(typeModel4.getItemtype()).thenReturn("AtomicType");
        final TypeModel typeModel5 = mock(TypeModel.class);
        when(typeModel5.getItemtype()).thenReturn("MapType");
        assertFalse(readService.isComplexType(typeModel1));
        assertTrue(readService.isComplexType(typeModel2));
        assertTrue(readService.isComplexType(typeModel3));
        assertFalse(readService.isComplexType(typeModel4));
        assertFalse(readService.isComplexType(typeModel5));
    }

    @Test
    public void testGetComplexTypeForAttributeDescriptor() {
        final AttributeDescriptorModel attributeDescriptorModel1 = new AttributeDescriptorModel();
        final ComposedTypeModel typeModel1 = mock(ComposedTypeModel.class);
        when(typeModel1.getItemtype()).thenReturn("ComposedType");
        attributeDescriptorModel1.setAttributeType(typeModel1);

        final AttributeDescriptorModel attributeDescriptorModel2 = new AttributeDescriptorModel();
        final AtomicTypeModel typeModel2 = mock(AtomicTypeModel.class);
        when(typeModel2.getItemtype()).thenReturn("AtomicType");
        attributeDescriptorModel2.setAttributeType(typeModel2);

        final AttributeDescriptorModel attributeDescriptorModel3 = new AttributeDescriptorModel();
        final CollectionTypeModel typeModel3 = mock(CollectionTypeModel.class);
        when(typeModel3.getItemtype()).thenReturn("CollectionType");
        final AtomicTypeModel elementType1 = mock(AtomicTypeModel.class);
        when(elementType1.getItemtype()).thenReturn("AtomicType");
        when(typeModel3.getElementType()).thenReturn(elementType1);
        attributeDescriptorModel3.setAttributeType(typeModel3);

        final AttributeDescriptorModel attributeDescriptorModel4 = new AttributeDescriptorModel();
        final CollectionTypeModel typeModel4 = mock(CollectionTypeModel.class);
        when(typeModel4.getItemtype()).thenReturn("CollectionType");
        final ComposedTypeModel elementType2 = mock(ComposedTypeModel.class);
        when(elementType2.getItemtype()).thenReturn("ComposedType");
        when(typeModel4.getElementType()).thenReturn(elementType2);
        attributeDescriptorModel4.setAttributeType(typeModel4);

        assertEquals(typeModel1, readService.getComplexTypeForAttributeDescriptor(attributeDescriptorModel1));
        assertNull(readService.getComplexTypeForAttributeDescriptor(attributeDescriptorModel2));
        assertNull(readService.getComplexTypeForAttributeDescriptor(attributeDescriptorModel3));
        assertEquals(elementType2, readService.getComplexTypeForAttributeDescriptor(attributeDescriptorModel4));
    }

    @Test
    public void testGetIntegrationObjectModels() {
        final SearchResult<IntegrationObjectModel> searchResult = mock(SearchResult.class);
        when(flexibleSearchService.<IntegrationObjectModel>search("SELECT PK FROM {IntegrationObject}")).thenReturn(searchResult);
        readService.getIntegrationObjectModels();
        verify(flexibleSearchService, times(1)).<IntegrationObjectModel>search("SELECT PK FROM {IntegrationObject}");
    }

    @Test
    public void testGetAvailableTypes() {
        final SearchResult<ComposedTypeModel> searchResult = mock(SearchResult.class);
        when(flexibleSearchService.<ComposedTypeModel>search("SELECT PK FROM {composedtype} WHERE (p_sourcetype is null AND p_generate =1) OR p_sourcetype = 8796093382738"))
                .thenReturn(searchResult);
        readService.getAvailableTypes();
        verify(flexibleSearchService, times(1)).search("SELECT PK FROM {composedtype} WHERE (p_sourcetype is null AND p_generate =1) OR p_sourcetype = 8796093382738");
    }

    @Test
    public void testGetAttributesForType() {
        ComposedTypeModel composedType = mock(ComposedTypeModel.class);
        readService.getAttributesForType(composedType);
        verify(typeService, times(1)).getAttributeDescriptorsForType(composedType);
    }

    @Test
    public void testGetIntegrationTypes() {
        assertEquals(IntegrationType.INBOUND, readService.getIntegrationTypes().get(0));
    }

    @Test
    public void testGetEDMX() {
        IntegrationObjectModel integrationObject = new IntegrationObjectModel();
        integrationObject.setItems(new HashSet<>());
        Schema schema = new Schema().setNamespace("namespace");
        when(schemaGenerator.generateSchema(integrationObject.getItems())).thenReturn(schema);
        assertNotNull(readService.getEDMX(integrationObject));
    }

    @Test
    public void testGetEDMXException() {
        assertNull(readService.getEDMX(new IntegrationObjectModel()));
    }

}