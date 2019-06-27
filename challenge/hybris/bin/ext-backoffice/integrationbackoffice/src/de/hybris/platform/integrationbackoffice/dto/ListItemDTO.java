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
package de.hybris.platform.integrationbackoffice.dto;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.TypeModel;

/**
 * Data to be handled by the editor. Only front-end specific fields have setters.
 */
public final class ListItemDTO {

    private AttributeDescriptorModel attributeDescriptor;
    private TypeModel type;
    private boolean isCollection;
    private boolean isCustomUnique;
    private boolean isSelected;
    private boolean autocreate;
    private String description;

    public ListItemDTO(final AttributeDescriptorModel attributeDescriptor, final boolean isCustomUnique,
                       final boolean isSelected, final boolean isCollection, final boolean autocreate) {
        this.attributeDescriptor = attributeDescriptor;
        this.isCollection = isCollection;
        this.isCustomUnique = isCustomUnique;
        this.isSelected = isSelected;
        this.autocreate = autocreate;
        findType();
        createDescription();
    }

    private void findType() {
        if (isCollection) {
            type = ((CollectionTypeModel) attributeDescriptor.getAttributeType()).getElementType();
        } else {
            type = attributeDescriptor.getAttributeType();
        }
    }

    private void createDescription() {
        if (isCollection) {
            description = String.format("Collection [%s]", type.getCode());
        } else {
            description = type.getCode();
        }
    }

    public AttributeDescriptorModel getAttributeDescriptor() {
        return attributeDescriptor;
    }

    public TypeModel getType() {
        return type;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public boolean isCustomUnique() {
        return isCustomUnique;
    }

    public void setCustomUnique(final boolean customUnique) {
        isCustomUnique = customUnique;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(final boolean selected) {
        isSelected = selected;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAutocreate() { return autocreate; }

    public void setAutocreate(boolean autocreate) { this.autocreate = autocreate; }

}
