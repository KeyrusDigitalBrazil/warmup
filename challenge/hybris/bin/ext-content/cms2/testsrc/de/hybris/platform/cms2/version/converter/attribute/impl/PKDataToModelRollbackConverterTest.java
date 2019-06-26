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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.version.converter.attribute.data.VersionPayloadDescriptor;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PKDataToModelRollbackConverterTest
{
	private final String PK_VALUE = "123";
	private final String PK_TYPE = "type.pk";

	@InjectMocks
	private PKDataToModelRollbackConverter converter;

	@Mock
	private ModelService modelService;

	@Mock
	private TypeService typeService;

	@Mock
	private CMSVersionService cmsVersionService;

	@Mock
	private CMSVersionModel versionModel;

	@Mock
	private ItemModel itemModel;

	@Mock
	private CMSItemModel cmsItemModel;

	private final VersionPayloadDescriptor payloadDescriptor = new VersionPayloadDescriptor(PK_TYPE, PK_VALUE);

	@Test
	public void willConvertToItemModelIfDataNotInstanceOfVersionModel()
	{
		//GIVEN
		when(modelService.get(PK.parse(PK_VALUE))).thenReturn(itemModel);
		when(itemModel.getItemtype()).thenReturn(CMSVersionModel._TYPECODE);
		when(itemModel.getPk()).thenReturn(PK.parse(PK_VALUE));
		when(typeService.isAssignableFrom(CMSVersionModel._TYPECODE, versionModel.getItemtype())).thenReturn(false);
		when(cmsVersionService.getItemFromVersion(versionModel)).thenReturn(cmsItemModel);

		// WHEN
		final Object value = converter.convert(payloadDescriptor);

		//THEN
		assertThat(value, is(itemModel));
		verify(cmsVersionService, never()).getItemFromVersion(versionModel);
	}

	@Test
	public void willConvertToCMSItemModelIfDataIsInstanceOfVersionModel()
	{
		//GIVEN
		when(modelService.get(PK.parse(PK_VALUE))).thenReturn(versionModel);
		when(versionModel.getItemtype()).thenReturn(CMSVersionModel._TYPECODE);
		when(versionModel.getPk()).thenReturn(PK.parse(PK_VALUE));
		when(typeService.isAssignableFrom(CMSVersionModel._TYPECODE, versionModel.getItemtype())).thenReturn(true);
		when(cmsVersionService.getItemFromVersion(versionModel)).thenReturn(cmsItemModel);

		// WHEN
		final Object value = converter.convert(payloadDescriptor);

		//THEN
		assertThat(value, is(cmsItemModel));
		verify(cmsVersionService).getItemFromVersion(versionModel);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfPrimaryKeyIsNotFound()
	{
		// GIVEN
		when(modelService.get(PK.parse(PK_VALUE))).thenThrow(new ModelLoadingException(""));

		// WHEN
		converter.convert(payloadDescriptor);
	}
}
