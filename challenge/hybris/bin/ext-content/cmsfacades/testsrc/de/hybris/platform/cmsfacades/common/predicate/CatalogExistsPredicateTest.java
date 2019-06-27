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

import com.google.common.collect.Lists;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CatalogExistsPredicateTest
{
    private String VALID_CATALOG_ID = "catalog";
    private String INVALID_CATALOG_ID = "invalid";
    private List<CatalogVersionModel> catalogsList;

    @InjectMocks
    private CatalogExistsPredicate predicate;

    @Mock
    private CatalogVersionService catalogVersionService;

    @Mock
    private CatalogVersionModel catalogVersionModel;

    @Mock
    private CatalogModel catalogModel;

    @Before
    public void setUp()
    {
        catalogsList = Lists.newArrayList(catalogVersionModel);

        when(catalogModel.getId()).thenReturn(VALID_CATALOG_ID);
        when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
        when(catalogVersionService.getAllCatalogVersions())
                .thenReturn(catalogsList);
    }

    @Test
    public void predicate_shouldFail_whenCatalogIsNull()
    {
        // Act
        boolean result = predicate.test(null);

        // Assert
        assertThat(result, is(false));
    }

    @Test
    public void predicate_shouldFail_whenCatalogDoesntExist()
    {
        // Act
        boolean result = predicate.test(INVALID_CATALOG_ID);

        // Assert
        assertThat(result, is(false));
    }


    @Test
    public  void predicate_shouldPass_whenGivenValidCatalog()
    {
        // Arrange

        // Act
        boolean result = predicate.test(VALID_CATALOG_ID);

        // Assert
        assertThat(result, is(true));
    }

}
