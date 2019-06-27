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
package de.hybris.platform.cmsfacades.navigations.populator.data;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.cmsfacades.common.populator.LocalizedPopulator;
import de.hybris.platform.cmsfacades.constants.CmsfacadesConstants;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeDataToModelParentPopulatorTest
{

	private static final String NEW_PARENT_UID = "new-parent-uid";
	private static final java.lang.String CURRENT_PARENT_UID = "current-parent-uid";

	@Mock
	private CMSNavigationService navigationService;

	@Mock
	private LocalizedPopulator localizedPopulator;

	@Mock
	private CMSAdminSiteService adminSiteService;

	@InjectMocks
	private NavigationNodeDataToModelParentPopulator populator;

	private final CMSNavigationNodeModel newParentNode = mock(CMSNavigationNodeModel.class);
	private final CMSNavigationNodeModel superRootNode = mock(CMSNavigationNodeModel.class);
	private final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);

	@Before
	public void setup() throws CMSItemNotFoundException
	{
		when(navigationService.getNavigationNodeForId(NEW_PARENT_UID)).thenReturn(newParentNode);


		when(adminSiteService.getActiveCatalogVersion()).thenReturn(catalogVersion);
		when(navigationService.getSuperRootNavigationNode(catalogVersion)).thenReturn(superRootNode);
	}

	@Test
	public void testChangingParentUidShouldMoveToAnotherParent() throws CMSItemNotFoundException
	{
		final NavigationNodeData source = mock(NavigationNodeData.class);
		when(source.getParentUid()).thenReturn(NEW_PARENT_UID);

		final CMSNavigationNodeModel target = mock(CMSNavigationNodeModel.class);
		final CMSNavigationNodeModel currentParentNode = mock(CMSNavigationNodeModel.class);
		when(currentParentNode.getUid()).thenReturn(CURRENT_PARENT_UID);
		when(target.getParent()).thenReturn(currentParentNode);

		populator.populate(source, target);

		verify(navigationService).getNavigationNodeForId(NEW_PARENT_UID);
		verify(navigationService).move(target, newParentNode);
	}

	@Test
	public void testChangingParentRootShouldMoveToAnotherParent() throws CMSItemNotFoundException
	{
		final NavigationNodeData source = mock(NavigationNodeData.class);
		when(source.getParentUid()).thenReturn(Cms2Constants.ROOT);

		final CMSNavigationNodeModel target = mock(CMSNavigationNodeModel.class);
		final CMSNavigationNodeModel currentParentNode = mock(CMSNavigationNodeModel.class);
		when(currentParentNode.getUid()).thenReturn(CURRENT_PARENT_UID);
		when(target.getParent()).thenReturn(currentParentNode);

		populator.populate(source, target);

		verify(adminSiteService).getActiveCatalogVersion();
		verify(navigationService).getSuperRootNavigationNode(catalogVersion);
		verify(navigationService).move(target, superRootNode);
	}

}
