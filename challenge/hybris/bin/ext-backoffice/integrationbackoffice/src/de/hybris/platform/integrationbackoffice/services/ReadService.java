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

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException;
import de.hybris.platform.odata2services.odata.schema.SchemaGenerator;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.type.TypeService;
import org.apache.olingo.odata2.api.edm.provider.DataServices;
import org.apache.olingo.odata2.api.edm.provider.Schema;
import org.apache.olingo.odata2.core.commons.XmlHelper;
import org.apache.olingo.odata2.core.ep.producer.XmlMetadataProducer;
import org.apache.olingo.odata2.core.ep.util.CircleStreamBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Handles the read requests from the extension's widgets
 */
public class ReadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadService.class);
    private FlexibleSearchService flexibleSearchService;
    private TypeService typeService;
    private SchemaGenerator oDataDefaultSchemaGenerator;

    public void setODataDefaultSchemaGenerator(final SchemaGenerator oDataDefaultSchemaGenerator) {
        this.oDataDefaultSchemaGenerator = oDataDefaultSchemaGenerator;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public void setTypeService(final TypeService typeService) {
        this.typeService = typeService;
    }

    /**
     * Checks whether a TypeModel's attribute type is a collection type or a flexible collection type
     * @param attributeType the attribute type to evaluate
     * @return if the attribute type is of CollectionType
     */
    public boolean isCollectionType(final String attributeType) {
        return typeService.isAssignableFrom("CollectionType", attributeType);
    }

    /**
     * Checks whether a TypeModel's attribute type is a composed type or a flexible composed type
     * @param attributeType the attribute type to evaluate
     * @return if the attribute type is of ComposedType
     */
    public boolean isComposedType(final String attributeType) {
        return typeService.isAssignableFrom("ComposedType", attributeType);
    }

    /**
     * Checks whether a TypeModel's attribute type is an enumeration meta type or a flexible enumeration meta type
     * @param attributeType the attribute type to evaluate
     * @return if the attribute type is of EnumerationMetaType
     */
    public boolean isEnumerationMetaType(final String attributeType) {
        return typeService.isAssignableFrom("EnumerationMetaType", attributeType);
    }

    /**
     * Checks whether a TypeModel's attribute type is an atomic type or a flexible atomic type
     * @param attributeType the attribute type to evaluate
     * @return if the attribute type is of AtomicType
     */
    public boolean isAtomicType(final String attributeType) {
        return typeService.isAssignableFrom("AtomicType", attributeType);
    }

    /**
     * Checks whether a TypeModel's attribute type is a map type or a flexible map type
     * @param attributeType the attribute type to evaluate
     * @return if the attribute type is of MapType
     */
    public boolean isMapType(final String attributeType) {
        return typeService.isAssignableFrom("MapType", attributeType);
    }

    /**
     * Checks whether a TypeModel is a ComposedType or an EnumerationMetaType
     * @param typeModel the type model to evaluate
     * @return if the attribute is a complex type
     */
    public boolean isComplexType(final TypeModel typeModel) {
        return isComposedType(typeModel.getItemtype()) || isEnumerationMetaType(typeModel.getItemtype());
    }

    /**
     * Gets a CollectionType's element's ComposedTypeModel (if it is a collection of complex types)
     * @param attributeDescriptorModel the attribute descriptor of the collection
     * @return the collection's element's ComposedTypeModel
     */
    public ComposedTypeModel getComplexTypeForAttributeDescriptor(final AttributeDescriptorModel attributeDescriptorModel) {
        final TypeModel typeModel = attributeDescriptorModel.getAttributeType();
        if (isComplexType(typeModel)) {
            return (ComposedTypeModel) typeModel;
        } else if (isCollectionType(typeModel.getItemtype())) {
            final TypeModel elementTypeModel = ((CollectionTypeModel) typeModel).getElementType();
            if (isComplexType(elementTypeModel)) {
                return (ComposedTypeModel) elementTypeModel;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Retrieves all IntegrationObjectModels
     * @return list of all IntegrationObjectModels
     */
    public List<IntegrationObjectModel> getIntegrationObjectModels() {
        return flexibleSearchService.<IntegrationObjectModel>search("SELECT PK FROM {IntegrationObject}").getResult();
    }

    /**
     * Retrieves all available IntegrationTypes
     * @return list of IntegrationTypes
     */
    public List<IntegrationType> getIntegrationTypes() {
        // This will become a list query once more IntegrationTypes are released
        final List<IntegrationType> types = new ArrayList<>();
        types.add(IntegrationType.INBOUND);
        return types;
    }

    /**
     * Get ComposedTypedModels from type system
     * @return list of ComposedTypeModels
     */
    public List<ComposedTypeModel> getAvailableTypes() {
        final SearchResult<ComposedTypeModel> composedTypeSearchResult = flexibleSearchService.search(
                "SELECT PK FROM {composedtype} WHERE (p_sourcetype is null AND p_generate =1) OR p_sourcetype = 8796093382738");
        return composedTypeSearchResult.getResult();
    }

    /**
     * Get the set of AttributeDescriptionModel for a given ComposedTypeModel
     * @param type a ComposedTypeModel object
     * @return the set of AttributeDescriptorModel of the ComposedTypeModel's attributes
     */
    public Set<AttributeDescriptorModel> getAttributesForType(final ComposedTypeModel type){
        return typeService.getAttributeDescriptorsForType(type);
    }

    /**
     * Get an EDMX representation of a given integration object
     * @param integrationObject an integration object to represent
     * @return an input stream containing the EDMX representation of the integration object
     * @throws InvalidODataSchemaException when schema generator fails
     */
    public InputStream getEDMX(final IntegrationObjectModel integrationObject) throws InvalidODataSchemaException {
        final Schema schema = oDataDefaultSchemaGenerator.generateSchema(integrationObject.getItems());

        final OutputStreamWriter writer;
        final CircleStreamBuffer csb = new CircleStreamBuffer();
        final DataServices metadata = (new DataServices()).setSchemas(Collections.singletonList(schema)).setDataServiceVersion("2.0");

        InputStream inputStream = null;
        try {
            writer = new OutputStreamWriter(csb.getOutputStream(), "UTF-8");
            final XMLStreamWriter xmlStreamWriter = XmlHelper.getXMLOutputFactory().createXMLStreamWriter(writer);
            XmlMetadataProducer.writeMetadata(metadata, xmlStreamWriter, null);
            inputStream = csb.getInputStream();
            writer.close();
        } catch (Exception e) {
            LOGGER.error("Failed to generate EDMX", e);
        }

        return inputStream;
    }

}