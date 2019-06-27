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
package de.hybris.platform.cmsfacades.rendering.populators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.navigation.CMSNavigationEntryModel;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cmsfacades.data.NavigationEntryData;
import de.hybris.platform.cmsfacades.data.NavigationNodeData;
import de.hybris.platform.cmsfacades.rendering.visibility.RenderingVisibilityService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class NavigationNodeModelToDataRenderingPopulatorTest
{
	private static final String TEST_UID = "test-uid";
	private static final String TEST_NAME = "test-name";
	private static final String TEST_TITLE = "test-title";

	@InjectMocks
	private NavigationNodeModelToDataRenderingPopulator populator;

	@Mock
	private Converter<CMSNavigationEntryModel, NavigationEntryData> navigationEntryModelToDataConverter;
	@Mock
	private CMSNavigationNodeModel navigationNodeModel;
	@Mock
	private RenderingVisibilityService renderingVisibilityService;
	@Mock
	private CMSNavigationEntryModel visibleNavigationEntryModel;
	@Mock
	private ItemModel visibleNavigationEntryItemModel;
	@Mock
	private CMSNavigationEntryModel notVisibleNavigationEntryModel;
	@Mock
	private ItemModel notVisibleNavigationEntryItemModel;

	private NavigationNodeData navigationNodeData;

	@Test
	public void shouldPopulateAllProperties()
	{
		// GIVEN
		navigationNodeData = new NavigationNodeData();
		when(navigationNodeModel.getUid()).thenReturn(TEST_UID);
		when(navigationNodeModel.getName()).thenReturn(TEST_NAME);
		when(navigationNodeModel.getTitle()).thenReturn(TEST_TITLE);
		when(navigationNodeModel.getEntries()).thenReturn(Arrays.asList(visibleNavigationEntryModel, notVisibleNavigationEntryModel));
		when(visibleNavigationEntryModel.getItem()).thenReturn(visibleNavigationEntryItemModel);
		when(notVisibleNavigationEntryModel.getItem()).thenReturn(notVisibleNavigationEntryItemModel);
		when(renderingVisibilityService.isVisible(visibleNavigationEntryItemModel)).thenReturn(true);
		when(renderingVisibilityService.isVisible(notVisibleNavigationEntryModel)).thenReturn(false);

		when(navigationEntryModelToDataConverter.convert(any())).thenReturn(new NavigationEntryData());

		// WHEN
		populator.populate(navigationNodeModel, navigationNodeData);

		// THEN
		assertThat(navigationNodeData.getUid(), equalTo(TEST_UID));
		assertThat(navigationNodeData.getName(), equalTo(TEST_NAME));
		assertThat(navigationNodeData.getLocalizedTitle(), equalTo(TEST_TITLE));
		assertThat(navigationNodeData.getEntries(), hasSize(1));
	}

}
