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
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSNavigationNodePopulatorTest
{

    @Mock
    private CMSNavigationService cmsNavigationService;

    @InjectMocks
    private CMSNavigationNodePopulator populator;

    private CatalogVersionModel catalogVersionModel;

    @Before
    public void setup() {
        catalogVersionModel = new CatalogVersionModel();
        catalogVersionModel.setVersion("MOCKED_CATALOG_VERSION");
    }

    protected CMSNavigationNodeModel createNavigationNode(String uid, CMSNavigationNodeModel parentNavigationNodeModel)
    {
        CMSNavigationNodeModel navigationNodeModel = new CMSNavigationNodeModel();
        navigationNodeModel.setUid(uid);
        navigationNodeModel.setCatalogVersion(catalogVersionModel);
        navigationNodeModel.setParent(parentNavigationNodeModel);

        return navigationNodeModel;
    }

    @Test
    public void shouldNotPopulateWhenParentExists()
    {
        CMSNavigationNodeModel rootNavigationNode = createNavigationNode(Cms2Constants.ROOT, null);
        CMSNavigationNodeModel navigationNode = createNavigationNode("NODE_1", rootNavigationNode);

        when(cmsNavigationService.isSuperRootNavigationNode(navigationNode)).thenReturn(false);

        populator.populate(null, navigationNode);

        verify(cmsNavigationService, times(0)).setSuperRootNodeOnNavigationNode(navigationNode, catalogVersionModel);
    }

    @Test
    public void shouldPopulateWhenParentDoesNotExists()
    {
        CMSNavigationNodeModel navigationNode = createNavigationNode("NODE_1", null);

        when(cmsNavigationService.isSuperRootNavigationNode(navigationNode)).thenReturn(false);

        populator.populate(null, navigationNode);

        verify(cmsNavigationService, times(1)).setSuperRootNodeOnNavigationNode(navigationNode, catalogVersionModel);
    }

    @Test
    public void shouldNotPopulateWhenNodeIsRoot()
    {
        CMSNavigationNodeModel rootNavigationNode =  createNavigationNode(Cms2Constants.ROOT, null);

        when(cmsNavigationService.isSuperRootNavigationNode(rootNavigationNode)).thenReturn(true);

        populator.populate(null, rootNavigationNode);

        verify(cmsNavigationService, times(0)).setSuperRootNodeOnNavigationNode(rootNavigationNode, catalogVersionModel);
    }

    @Test(expected = ConversionException.class)
    public void shouldFailWhenNodeIsNull()
    {
        populator.populate(null, null);
    }

}
