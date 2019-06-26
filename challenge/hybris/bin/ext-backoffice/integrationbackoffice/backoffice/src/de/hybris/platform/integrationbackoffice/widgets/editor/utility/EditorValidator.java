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

import java.util.List;
import java.util.Map;

public final class EditorValidator {

    private EditorValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Validates that each IntegrationObjectItem contains at least one defined attribute.
     * @param definitionMap The map to be evaluated.
     * @return The name of the IntegrationObjectItem that is missing a definition.
     */
    public static String validateDefinitions(final Map<ComposedTypeModel, List<ListItemDTO>> definitionMap) {
        for (final Map.Entry<ComposedTypeModel, List<ListItemDTO>> entry : definitionMap.entrySet()) {
            if (entry.getValue().isEmpty()) {
                return entry.getKey().getCode();
            }
        }
        return "";
    }

    /**
     * Validates that each IntegrationObjectItem contains at least one attribute marked as unique.
     * @param definitionMap The map to be evaluated
     * @return The name of the IntegrationObjectItem that is missing a unique attribute.
     */
    public static String validateHasKey(final Map<ComposedTypeModel, List<ListItemDTO>> definitionMap) {
        String validationError = "";
        for (final Map.Entry<ComposedTypeModel, List<ListItemDTO>> entry : definitionMap.entrySet()) {
            validationError = entry.getKey().getCode();
            for (final ListItemDTO listItemDTO : entry.getValue()) {
                if (listItemDTO.isCustomUnique() || listItemDTO.getAttributeDescriptor().getUnique()) {
                    validationError = "";
                    break;
                }
            }

            if (!("").equals(validationError)) {
                return validationError;
            }
        }
        return validationError;
    }

}
