/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice.excel.template.populator.typesheet;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.backoffice.excel.data.ExcelExportResult;
import com.hybris.backoffice.excel.exporting.ExcelExportDivider;
import com.hybris.backoffice.excel.template.populator.ExcelSheetPopulator;


@RunWith(MockitoJUnitRunner.class)
public class PermissionAwareTypeSystemSheetPopulatorTest
{
	@Mock
	ExcelExportDivider mockedExcelExportDivider;
	@Mock
	PermissionCRUDService mockedPermissionCRUDService;
	@Mock
	ExcelSheetPopulator mockedPopulator;
	@InjectMocks
	PermissionAwareTypeSystemSheetPopulator permissionAwareTypeSystemSheetPopulator;

	@Test
	public void shouldFilterOutItemsThatUserHasNoAccessTo()
	{
		// given
		final ArgumentCaptor<ExcelExportResult> resultCaptor = ArgumentCaptor.forClass(ExcelExportResult.class);
		final ItemModel allowedItem = mock(ItemModel.class);
		final ItemModel forbiddenItem = mock(ItemModel.class);
		final Collection<ItemModel> items = Arrays.asList(allowedItem, forbiddenItem);

		final ExcelExportResult excelExportResult = mock(ExcelExportResult.class);
		given(excelExportResult.getSelectedItems()).willReturn(items);

		given(mockedExcelExportDivider.groupItemsByType(items)).willReturn(new HashMap<String, Set<ItemModel>>()
		{
			{
				put("AllowedItemType", new HashSet<ItemModel>()
				{
					{
						add(allowedItem);
					}
				});
				put("ForbiddenItemType", new HashSet<ItemModel>()
				{
					{
						add(forbiddenItem);
					}
				});
			}
		});
		given(mockedPermissionCRUDService.canReadType("AllowedItemType")).willReturn(true);
		given(mockedPermissionCRUDService.canReadType("ForbiddenItemType")).willReturn(false);

		// when
		permissionAwareTypeSystemSheetPopulator.populate(excelExportResult);

		// then
		verify(mockedPopulator).populate(resultCaptor.capture());
		final ExcelExportResult result = resultCaptor.getValue();
		assertThat(result.getSelectedItems()).containsOnly(allowedItem);
	}
}
