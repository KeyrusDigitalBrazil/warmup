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
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;

import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemAttributeModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class EditorUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditorUtils.class);

    private EditorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Comboitem createComboItem(final String label, final Object value) {
        final Comboitem item = new Comboitem(label);
        item.setValue(value);
        return item;
    }

    public static Treeitem createTreeItem(final String qualifier, final ComposedTypeModel value, final boolean expanded) {
        final String label = (qualifier == null) ? value.getCode() : (qualifier + " [" + value.getCode() + "]");
        final Treeitem treeitem = new Treeitem();
        final Treerow treerow = new Treerow();
        final Treecell treecell = new Treecell(label);
        treerow.appendChild(treecell);
        treeitem.appendChild(treerow);
        treeitem.setValue(value);
        treeitem.setOpen(expanded);
        return treeitem;
    }

    public static Listitem createListItem(final ListItemDTO dto, final boolean isComplex) {
        final Listitem child = new Listitem();
        final Listcell attrLabel = new Listcell(dto.getAttributeDescriptor().getQualifier());
        final Listcell attrDataType = new Listcell(dto.getDescription());
        final Listcell attrUnique = new Listcell();
        final Checkbox attrUniqueCheckbox = new Checkbox();
        final Listcell attrAutocreate = new Listcell();
        final Checkbox attrAutocreateCheckbox = new Checkbox();

        attrUnique.setSclass("yw-integrationbackoffice-editor-listbox-checkbox-unique");
        attrAutocreate.setSclass("yw-integrationbackoffice-editor-listbox-checkbox-autocreate");

        final boolean isUnique = dto.getAttributeDescriptor().getUnique();
        final boolean isNullable = dto.getAttributeDescriptor().getOptional();
        final boolean isRequired = isUnique && !isNullable;

        // Unique checkbox rules
        if (dto.isCollection()) {
            attrUniqueCheckbox.setDisabled(true);
        } else if (isUnique) {
            attrUniqueCheckbox.setChecked(true);
            attrUniqueCheckbox.setDisabled(true);
        } else {
            attrUniqueCheckbox.setChecked(dto.isCustomUnique());
        }

        // Autocreate checkbox rules
        if (!isComplex || isUnique) {
            attrAutocreateCheckbox.setDisabled(true);
        } else {
            attrAutocreateCheckbox.setChecked(dto.isAutocreate());
        }

        // Listitem rules
        if (isRequired) {
            child.setSelected(true);
            child.setDisabled(true);
        } else if (dto.isCustomUnique()) {
            child.setSelected(true);
        } else {
            child.setSelected(dto.isSelected());
        }

        attrUnique.appendChild(attrUniqueCheckbox);
        attrAutocreate.appendChild(attrAutocreateCheckbox);

        child.appendChild(attrLabel);
        child.appendChild(attrDataType);
        child.appendChild(attrUnique);
        child.appendChild(attrAutocreate);

        child.setValue(dto);

        return child;
    }

    public static String getQualifierFromLabel(final String label) {
        return label.substring(0, label.indexOf(' '));
    }

    public static boolean isInTreeChildren(final String label, final Treechildren treechildren) {
        if (treechildren != null) {
            for (final Treeitem treeitem : treechildren.getItems()) {
                if (getQualifierFromLabel(treeitem.getLabel()).equals(label)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determine the root ComposedType from a IntegrationObject's IntegrationObjectItems. The root is the IntegrationObjectItem which
     * has no parent IntegrationObjectItem. Algorithm: every IntegrationObjectItem is also an IntegrationObjectItemAttribute
     * with ReturnIntegrationObjectItem != null, except for the root.
     */
    public static IntegrationObjectItemModel getRootIntegrationObjectItem(final IntegrationObjectModel integrationObject) {
        final Set<IntegrationObjectItemModel> allItems = new HashSet<>(integrationObject.getItems());
        final Set<IntegrationObjectItemModel> allItemsExcludingRoots = new HashSet<>();

        for (final IntegrationObjectItemModel item : allItems) {
            for (final IntegrationObjectItemAttributeModel attribute : item.getAttributes()) {
                if (attribute.getReturnIntegrationObjectItem() != null) {
                    allItemsExcludingRoots.add(attribute.getReturnIntegrationObjectItem());
                }
            }
        }

        allItems.removeAll(allItemsExcludingRoots);

        if (allItems.size() != 1) {
            LOGGER.error("Error in finding root composed type.");
            return null;
        } else {
            return new ArrayList<>(allItems).get(0);
        }
    }

    /**
     * Filters a set of attribute descriptors for Composed and Enumeration types that are not on the blacklist
     *
     * @param readService the IntegrationBackofficeReadService to read from the type system
     * @param parentType  the parent ComposedTypeModel to get the attribute descriptors to filter
     * @return a filtered set of AttributeDescriptorModel
     */
    public static Set<AttributeDescriptorModel> filterAttributesForTree(final ReadService readService,
                                                                        final ComposedTypeModel parentType) {
        return readService.getAttributesForType(parentType).stream()
                .filter(attributeDescriptor -> !EditorBlacklists.getAttributeBlackList().contains(attributeDescriptor.getQualifier()))
                .filter(attributeDescriptor -> readService.isComplexType(attributeDescriptor.getAttributeType()))
                .distinct()
                .collect(Collectors.toSet());
    }

    /**
     * Filters a set of attribute descriptors for types that are not on the blacklist
     *
     * @param readService the IntegrationBackofficeReadService to read from the type system
     * @param parentType  the parent ComposedTypeModel to get the attribute descriptors to filter
     * @return a filtered and sorted set of AttributeDescriptorModel
     */
    public static Set<AttributeDescriptorModel> filterAttributesForAttributesMap(final ReadService readService,
                                                                                 final ComposedTypeModel parentType) {
        return readService.getAttributesForType(parentType).stream()
                .filter(attribute -> {
                    final String itemType = attribute.getAttributeType().getItemtype();
                    return (readService.isComplexType(attribute.getAttributeType()) ||
                            readService.isAtomicType(itemType) ||
                            readService.isMapType(itemType) ||
                            readService.isCollectionType(itemType));
                }).filter(attribute -> !EditorBlacklists.getAttributeBlackList().contains(attribute.getQualifier()))
                .distinct()
                .sorted((attribute1, attribute2) -> attribute1.getQualifier().compareToIgnoreCase(attribute2.getQualifier()))
                .collect(Collectors.toSet());
    }

    /**
     * Updates attributes of a list of DTOs by getting the attributes of another list of DTOs
     *
     * @param oldDTOs a list of DTOs with attributes to update
     * @param newDTOs a list of DTOs containing updated attributes
     * @return a list of DTOs with updated attributes
     */
    public static List<ListItemDTO> updateDTOs(final List<ListItemDTO> oldDTOs, final List<ListItemDTO> newDTOs) {
        newDTOs.forEach(newDTO -> oldDTOs.forEach(oldDTO -> {
            if (oldDTO.getAttributeDescriptor().getQualifier().equals(newDTO.getAttributeDescriptor().getQualifier())) {
                oldDTO.setSelected(newDTO.isSelected());
                oldDTO.setCustomUnique(newDTO.isCustomUnique());
                oldDTO.setAutocreate(newDTO.isAutocreate());
            }
        }));
        return oldDTOs;
    }

    /**
     * Converts an integration object's contents to a map of lists of DTOs
     *
     * @param readService       the ReadService to read from the type system
     * @param integrationObject an integration object to convert
     * @return a map of lists of DTOs
     */
    public static Map<ComposedTypeModel, List<ListItemDTO>> convertIntegrationObjectToDTOMap(final ReadService readService,
                                                                                             final IntegrationObjectModel integrationObject) {
        return integrationObject.getItems().stream()
                .collect(Collectors.toMap(IntegrationObjectItemModel::getType, item -> getItemDTOS(readService, item)));
    }

    private static List<ListItemDTO> getItemDTOS(final ReadService readService, final IntegrationObjectItemModel item) {
        return item.getAttributes().stream()
                .map(attr -> toListItemDTO(readService, attr))
                .collect(Collectors.toList());
    }

    private static ListItemDTO toListItemDTO(final ReadService readService, final IntegrationObjectItemAttributeModel attribute) {
        final AttributeDescriptorModel attributeDescriptor = attribute.getAttributeDescriptor();
        final boolean isCustomUnique = BooleanUtils.isTrue(attribute.getUnique()) && BooleanUtils.isNotTrue(attributeDescriptor.getUnique());
        final boolean isCollection = readService.isCollectionType(attributeDescriptor.getAttributeType().getItemtype());
        final boolean autocreate = BooleanUtils.isTrue(attribute.getAutoCreate());
        return new ListItemDTO(attributeDescriptor, isCustomUnique, true, isCollection, autocreate);
    }

    /**
     * Gets the attribute descriptors of collection attributes from a list of DTOs
     *
     * @param dtoList a list of ListItemDTO
     * @return a set of AttributeDescriptorModel of CollectionType attributes
     */
    public static Set<AttributeDescriptorModel> getCollectionAttributes(final List<ListItemDTO> dtoList) {
        if (dtoList != null) {
            return dtoList.stream()
                    .filter(ListItemDTO::isCollection)
                    .map(ListItemDTO::getAttributeDescriptor)
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
    }

}
