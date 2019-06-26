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
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemUniqueIdentifierAttributePopulatorTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String SOME_ITEM_ID = "some item id";

	@Mock
	private ItemData itemData;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@InjectMocks
	private CMSItemUniqueIdentifierAttributePopulator uniqueIdentifierAttributePopulator;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(itemData.getItemId()).thenReturn(SOME_ITEM_ID);
		when(uniqueItemIdentifierService.getItemData(any())).thenReturn(Optional.of(itemData));
	}

	// --------------------------------------------------------------------------
	// Test Methods
	// --------------------------------------------------------------------------
	@Test
	public void givenCMSItem_WhenPopulatorIsCalled_ThenItSetsItsUuid()
	{
		// GIVEN
		CMSItemModel itemModel = new CMSItemModel();
		Map<String, Object> resultMap = new HashMap<>();

		// WHEN
		uniqueIdentifierAttributePopulator.populate(itemModel, resultMap);

		// THEN
		assertThat(resultMap.get(FIELD_UUID), is(SOME_ITEM_ID));
	}

	@Test
	public void givenItem_WhenPopulatorIsCalled_ThenItSetsItsUuid()
	{
		// GIVEN
		ItemModel itemModel = new ItemModel();
		Map<String, Object> resultMap = new HashMap<>();

		// WHEN
		uniqueIdentifierAttributePopulator.populate(itemModel, resultMap);

		// THEN
		assertThat(resultMap.get(FIELD_UUID), is(SOME_ITEM_ID));
	}

}
