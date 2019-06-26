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
package de.hybris.platform.cmsfacades.version.populator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminItemService;
import de.hybris.platform.cmsfacades.data.CMSVersionData;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;




@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSVersionDataPopulatorTest
{
	private static final String ITEM_UID = "item-uid";
	private static final String ITEM_UUID = "item-uuid";
	private static final String VERSION_UID = "version-uid";

	@InjectMocks
	private CMSVersionDataPopulator versionDataPopulator;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private CMSAdminItemService cmsAdminItemService;
	@Mock
	private CMSVersionModel source;
	@Mock
	private CMSVersionData target;
	@Mock
	private CMSItemModel cmsItemModel;
	@Mock
	private ItemData itemItemData;

	@Before
	public void setUp()
	{
		when(source.getItemUid()).thenReturn(ITEM_UID);
		when(source.getUid()).thenReturn(VERSION_UID);
		when(itemItemData.getItemId()).thenReturn(ITEM_UUID);
		when(uniqueItemIdentifierService.getItemData(any())).thenReturn(Optional.of(itemItemData));
	}

	@Test
	public void shouldPopulateVersionAndItemUUIDs() throws CMSItemNotFoundException
	{
		when(cmsAdminItemService.findByUid(ITEM_UID)).thenReturn(cmsItemModel);

		versionDataPopulator.populate(source, target);

		verify(target).setUid(VERSION_UID);
		verify(target).setItemUUID(ITEM_UUID);
	}

	@Test(expected = ConversionException.class)
	public void shouldFailPopulateItemNotFound() throws CMSItemNotFoundException
	{
		when(cmsAdminItemService.findByUid(ITEM_UID)).thenThrow(new CMSItemNotFoundException("Invalid item uid"));

		versionDataPopulator.populate(source, target);
	}
}
