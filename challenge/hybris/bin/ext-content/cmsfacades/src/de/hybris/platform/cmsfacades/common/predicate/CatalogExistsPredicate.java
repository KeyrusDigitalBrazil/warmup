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
package de.hybris.platform.cmsfacades.common.predicate;


import de.hybris.platform.catalog.CatalogVersionService;
import org.springframework.beans.factory.annotation.Required;

import java.util.function.Predicate;

/**
 * Predicate to test if a given catalog exists.
 * <p>
 * Returns <tt>TRUE</tt> if the given catalog exists; <tt>FALSE</tt> otherwise.
 * </p>
 */
public class CatalogExistsPredicate implements Predicate<String>
{
    private CatalogVersionService catalogVersionService;

    @Override
    public boolean test(final String catalogId)
    {
        return getCatalogVersionService().getAllCatalogVersions().stream()
            .anyMatch(cat -> cat.getCatalog().getId().equals(catalogId));
    }


    protected CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }

    @Required
    public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
    {
        this.catalogVersionService = catalogVersionService;
    }

}
