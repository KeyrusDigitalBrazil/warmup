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

import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.types.populator.CMSItemDropdownComponentTypeAttributePopulator;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Populator that set the CMSNavigationEntry 'params' typeCodes value and the 'subTypes' values from configuration.
 * The dropdown populate the list by using the attribute "params.typeCodes" value.
 * The attribute "subTypes" is used when creating a new Component.
 */
public class CMSNavigationEntryItemPopulator extends CMSItemDropdownComponentTypeAttributePopulator
{
    private List<String> typeCodes;

    private static final String TYPE_CODE = "typeCode";
    private static final String TYPE_CODES = "typeCodes";

    private static final String SORT_KEY = "sort";
    private static final String SORT_VALUE = "itemtype:%1$s,name:%1$s";

    @Override
    protected Map<String, String> getComponentParams(final Class<?> type, final ComponentTypeAttributeData target)
    {
        final Map<String, String> paramsMap = super.getComponentParams(type, target);
        if (CollectionUtils.isNotEmpty(getTypeCodes()))
        {
            paramsMap.remove(TYPE_CODE);
            paramsMap.put(TYPE_CODES, getTypeCodes().stream().collect(Collectors.joining(",")));
        }
        paramsMap.put(SORT_KEY, String.format(SORT_VALUE, SortDirection.ASC));
        return paramsMap;
    }

    @Override
    protected Map<String, String> getComponentSubTypes(final Class<?> type)
    {
        if (CollectionUtils.isNotEmpty(getTypeCodes()))
        {
            return super.getComponentSubTypes(type)
                    .entrySet()
                    .stream()
                    .filter(subType -> getTypeCodes().stream().anyMatch(subType.getKey()::equals))
                    .collect(Collectors.toMap(subType -> subType.getKey(), subType -> subType.getValue()));
        }
        return super.getComponentSubTypes(type);
    }

    @Required
    public void setTypeCodes(final List<String> typeCodes)
    {
        this.typeCodes = typeCodes;
    }

    protected List<String> getTypeCodes()
    {
        return typeCodes;
    }
}
