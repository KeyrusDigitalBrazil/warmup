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


import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminComponentService;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultNavigationEntryCmsComponentItemModelConversionFunctionTest
{

	@Mock
	private CMSAdminComponentService componentAdminService;

	@InjectMocks
	private DefaultNavigationEntryCmsComponentItemModelConversionFunction conversionFunction;

	@Test
	public void shouldConvertFunctionAccordingToCorrectCmsItemModelType()
	{
		final AbstractCMSComponentModel abstractCmsModel = mock(AbstractCMSComponentModel.class);
		when(componentAdminService.getCMSComponentForId(anyString())).thenReturn(abstractCmsModel);
		final NavigationEntryData navigationEntryData = mock(NavigationEntryData.class);
		conversionFunction.apply(navigationEntryData);
	}

	@Test(expected = ConversionException.class)
	public void shouldThrowAnExceptionWhenInvalidComponentUid()
	{
		when(componentAdminService.getCMSComponentForId(anyString())).thenThrow(new UnknownIdentifierException(""));
		final NavigationEntryData navigationEntryData = mock(NavigationEntryData.class);
		conversionFunction.apply(navigationEntryData);
		fail();
	}
}
