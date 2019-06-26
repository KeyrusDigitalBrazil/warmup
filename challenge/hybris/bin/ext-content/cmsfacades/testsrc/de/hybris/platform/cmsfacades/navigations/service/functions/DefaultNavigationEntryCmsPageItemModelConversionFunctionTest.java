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
package de.hybris.platform.cmsfacades.navigations.service.functions;

import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryCmsPageItemModelConversionFunctionTest
{

	@Mock
	private CMSAdminPageService cmsAdminPageService;

	@InjectMocks
	private DefaultNavigationEntryCmsPageItemModelConversionFunction conversionFunction;

	@Test
	public void testGetPageFromNavigationEntry()
	{
		final NavigationEntryData navigationEntry = Mockito.mock(NavigationEntryData.class);
		conversionFunction.apply(navigationEntry);
		verify(cmsAdminPageService).getPageForIdFromActiveCatalogVersion(Mockito.anyString());
	}
}
