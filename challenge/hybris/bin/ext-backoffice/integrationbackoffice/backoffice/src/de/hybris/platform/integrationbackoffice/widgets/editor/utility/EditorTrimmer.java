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

import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.dto.ListItemDTO;
import de.hybris.platform.integrationbackoffice.services.ReadService;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.integrationbackoffice.widgets.editor.utility.EditorUtils.getQualifierFromLabel;

public final class EditorTrimmer {

    private static ReadService readService;
    private static Map<ComposedTypeModel, List<ListItemDTO>> attributesMap;
    private static Map<ComposedTypeModel, List<ListItemDTO>> trimmedMap;

    private EditorTrimmer() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Trims the map created during the editing process so that it only contains checked/selected items so that it is
     * leaner for the persistence process.
     * @param rs               ReadService to access the type system
     * @param fullMap          The map created during the editing process.
     * @param composedTypeTree The tree representing the structure of ComposedTypes.
     * @return The trimmed down map.
     */
    public static Map<ComposedTypeModel, List<ListItemDTO>> trimMap(final ReadService rs,
                                                                    final Map<ComposedTypeModel, List<ListItemDTO>> fullMap,
                                                                    final Tree composedTypeTree) {
        readService = rs;
        attributesMap = fullMap;
        trimmedMap = new HashMap<>();

        final Treeitem root = composedTypeTree.getItems().iterator().next();
        trim(root);

        return trimmedMap;
    }

    private static void trim(final Treeitem treeItem) {
        final ComposedTypeModel key = treeItem.getValue();
        final List<ListItemDTO> dtoList = attributesMap.get(key);

        final List<ListItemDTO> trimmedList = new ArrayList<>();
        for (final ListItemDTO dto : dtoList) {
            if (dto.isSelected()) {
                trimmedList.add(dto);
            }
        }

        if (treeItem.getTreechildren() != null) {
            final Collection<Treeitem> children = treeItem.getTreechildren().getChildren();
            for (final ListItemDTO dto : trimmedList) {
                if (readService.isComplexType(dto.getType()) || dto.isCollection()) {
                    trimChild(children, dto);
                }
            }
        }

        trimmedMap.put(key, trimmedList);
    }

    private static void trimChild(final Collection<Treeitem> children, final ListItemDTO dto) {
        for (final Treeitem child : children) {
            final String treeItemQualifier = getQualifierFromLabel(child.getLabel());
            final String dtoQualifier = dto.getAttributeDescriptor().getQualifier();
            if (treeItemQualifier.equals(dtoQualifier)) {
                trim(child);
                break;
            }
        }
    }

}
