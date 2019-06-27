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
package de.hybris.platform.cms2.version.converter.attribute.impl;

import static de.hybris.platform.core.PK.fromLong;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.data.CMSItemData;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.PK;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CMSItemToDataConverterTest
{

	private static final String PK_VALUE = "123";
	private static final String TRANSACTION_ID = "someTransactionId";

	@InjectMocks
	private CMSItemToDataConverter converter;

	@Mock
	private CMSVersionService cmsVersionService;
	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;
	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CMSItemModel cmsItem;

	@Mock
	private CMSVersionModel versionModel;

	private final PK pk = fromLong(Long.valueOf(PK_VALUE));
	private final PK itemPk = fromLong(Long.valueOf(PK_VALUE));

	@Before
	public void setup()
	{
		when(versionModel.getPk()).thenReturn(pk);
		when(cmsItem.getPk()).thenReturn(itemPk);
		when(cmsVersionService.getTransactionId()).thenReturn(TRANSACTION_ID);
		when(cmsVersionSessionContextProvider.getAllUnsavedVersionedItemsFromCached()).thenReturn(new HashSet<CMSItemData>());
	}

	@Test
	public void whenConvertNullValueReturnsNull()
	{
		assertThat(converter.convert(null), nullValue());
	}

	@Test
	public void shouldConvertValidCMSItemAndReturnPKVersionByCreatingOneAVersionForIt()
	{
		// GIVEN
		when(cmsVersionService.createRevisionForItem(cmsItem)).thenReturn(versionModel);
		when(cmsVersionService.isVersionable(cmsItem)).thenReturn(true);

		// WHEN
		final Object value = converter.convert(cmsItem);

		// THEN
		verify(cmsVersionService).createRevisionForItem(cmsItem);
		assertThat(value, is(pk));
	}

	@Test
	public void shouldReturnPKOfSourceItemIfItemIsNotVersionable()
	{

		// GIVEN
		when(cmsVersionService.createRevisionForItem(cmsItem)).thenReturn(versionModel);
		when(cmsVersionService.isVersionable(cmsItem)).thenReturn(false);

		// WHEN
		final Object value = converter.convert(cmsItem);

		// THEN
		assertThat(value, is(itemPk));
	}

}
