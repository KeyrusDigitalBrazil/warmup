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
package de.hybris.platform.cmsfacades.rendering.attributeconverters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeToDataContentConverterTest
{
	@InjectMocks
	private NavigationNodeToDataContentConverter converter;

	@Mock
	private Converter<CMSNavigationNodeModel, NavigationNodeData> navigationNodeModelToDataRenderingConverter;
	@Mock
	private CMSNavigationNodeModel navigationNodeModel;
	@Mock
	private CMSNavigationNodeModel childNode1;
	@Mock
	private CMSNavigationNodeModel childNode2;
	@Mock
	private NavigationNodeData mockNavigationNodeData;
	@Mock
	private RenderingVisibilityService renderingVisibilityService;

	@Test
	public void shouldReturnNullWhenSourceIsNull()
	{
		// WHEN
		final NavigationNodeData data = converter.convert(null);

		// THEN
		assertThat(data, nullValue());
		verifyZeroInteractions(navigationNodeModelToDataRenderingConverter);
	}

	@Test
	public void shouldReturnNullWhenNodeIsNotVisible()
	{
		// GIVEN
		when(renderingVisibilityService.isVisible(navigationNodeModel)).thenReturn(false);

		// WHEN
		final NavigationNodeData data = converter.convert(null);

		// THEN
		assertThat(data, nullValue());
		verifyZeroInteractions(navigationNodeModelToDataRenderingConverter);
	}

	@Test
	public void shouldConvertNodeModelToData()
	{
		// GIVEN
		when(navigationNodeModelToDataRenderingConverter.convert(navigationNodeModel)).thenReturn(new NavigationNodeData());
		when(navigationNodeModelToDataRenderingConverter.convert(childNode1)).thenReturn(mockNavigationNodeData);
		when(navigationNodeModelToDataRenderingConverter.convert(childNode2)).thenReturn(mockNavigationNodeData);
		when(navigationNodeModel.getChildren()).thenReturn(Arrays.asList(childNode1, childNode2));
		when(renderingVisibilityService.isVisible(navigationNodeModel)).thenReturn(true);
		when(renderingVisibilityService.isVisible(childNode1)).thenReturn(true);
		when(renderingVisibilityService.isVisible(childNode2)).thenReturn(true);

		// WHEN
		final NavigationNodeData data = converter.convert(navigationNodeModel);

		// THEN
		assertThat(data.getChildren(), hasSize(2));
		verify(navigationNodeModelToDataRenderingConverter, times(3)).convert(any());
	}

}
