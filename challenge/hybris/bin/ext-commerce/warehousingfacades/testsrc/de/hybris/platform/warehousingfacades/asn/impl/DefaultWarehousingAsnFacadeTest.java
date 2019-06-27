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
package de.hybris.platform.warehousingfacades.asn.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.converters.impl.AbstractConverter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.asn.service.AsnService;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;
import de.hybris.platform.warehousingfacades.asn.data.AsnEntryData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWarehousingAsnFacadeTest
{
	private static final String EXTERNAL_ID = "external_01";
	private static final String INTERNAL_ID = "internal_id1";
	private static final String MONTREAL_POS_NAME = "Montreal_PoS";
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Date ASN_RELEASE_DATE;

	private static final String CAMERA = "camera";
	private static final int CAMERA_STOCK = 5;
	private static final String LENS = "lens";
	private static final int LENS_STOCK = 10;

	@InjectMocks
	private DefaultWarehousingAsnFacade defaultWarehousingAsnFacade;

	@Mock
	private AsnService asnService;
	@Mock
	private ModelService modelService;
	@Mock
	private AsnData asnData;
	@Mock
	private AsnEntryData cameraAdvancedShippingNoticeEntryData;
	@Mock
	private AsnEntryData lensAdvancedShippingNoticeEntryData;
	@Mock
	private AdvancedShippingNoticeModel advancedShippingNotice;
	@Mock
	private AdvancedShippingNoticeEntryModel cameraAdvancedShippingNoticeEntry;
	@Mock
	private AdvancedShippingNoticeEntryModel lensAdvancedShippingNoticeEntry;
	@Mock
	private AbstractConverter<AsnData, AdvancedShippingNoticeModel> asnModelConverter;
	@Mock
	private AbstractConverter<AdvancedShippingNoticeModel, AsnData> asnDataConverter;

	@Before
	public void setUp() throws ParseException
	{
		doNothing().when(modelService).save(advancedShippingNotice);

		when(asnData.getExternalId()).thenReturn(EXTERNAL_ID);
		when(asnData.getPointOfServiceName()).thenReturn(MONTREAL_POS_NAME);
		ASN_RELEASE_DATE = sdf.parse("2027-12-19");
		when(asnData.getReleaseDate()).thenReturn(ASN_RELEASE_DATE);
		when(asnData.getAsnEntries())
				.thenReturn(Lists.newArrayList(cameraAdvancedShippingNoticeEntryData, lensAdvancedShippingNoticeEntryData));
		when(cameraAdvancedShippingNoticeEntryData.getProductCode()).thenReturn(CAMERA);
		when(cameraAdvancedShippingNoticeEntryData.getQuantity()).thenReturn(CAMERA_STOCK);
		when(lensAdvancedShippingNoticeEntryData.getProductCode()).thenReturn(LENS);
		when(cameraAdvancedShippingNoticeEntryData.getQuantity()).thenReturn(LENS_STOCK);

		when(advancedShippingNotice.getInternalId()).thenReturn(INTERNAL_ID);
		when(advancedShippingNotice.getAsnEntries())
				.thenReturn(Lists.newArrayList(cameraAdvancedShippingNoticeEntry, lensAdvancedShippingNoticeEntry));
		when(cameraAdvancedShippingNoticeEntry.getProductCode()).thenReturn(CAMERA);
		when(cameraAdvancedShippingNoticeEntry.getQuantity()).thenReturn(CAMERA_STOCK);
		when(lensAdvancedShippingNoticeEntry.getProductCode()).thenReturn(LENS);
		when(cameraAdvancedShippingNoticeEntry.getQuantity()).thenReturn(LENS_STOCK);

		when(asnModelConverter.convert(asnData)).thenReturn(advancedShippingNotice);
		when(asnDataConverter.convert(advancedShippingNotice)).thenReturn(asnData);
		when(asnService.confirmAsnReceipt(INTERNAL_ID)).thenReturn(advancedShippingNotice);
	}

	@Test
	public void shouldCreateAsn()
	{
		//When
		defaultWarehousingAsnFacade.createAsn(asnData);

		//Then
		verify(asnService).processAsn(advancedShippingNotice);
		verify(modelService).save(advancedShippingNotice);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNoPoS()
	{
		//Given
		when(asnData.getPointOfServiceName()).thenReturn(null);

		//When
		defaultWarehousingAsnFacade.createAsn(asnData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNoAsnEntry()
	{
		//Given
		when(asnData.getAsnEntries()).thenReturn(Collections.emptyList());

		//When
		defaultWarehousingAsnFacade.createAsn(asnData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNoExternalId()
	{
		//Given
		when(asnData.getExternalId()).thenReturn(null);

		//When
		defaultWarehousingAsnFacade.createAsn(asnData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenNoReleaseDate()
	{
		//Given
		when(asnData.getReleaseDate()).thenReturn(null);

		//When
		defaultWarehousingAsnFacade.createAsn(asnData);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailWhenReleaseDateBeforeToday()
	{
		//Given asn's release date is 10 days before today
		final Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -10);
		when(asnData.getReleaseDate()).thenReturn(cal.getTime());

		//When
		defaultWarehousingAsnFacade.createAsn(asnData);
	}

	@Test
	public void shouldConfirmAsnReceipt()
	{
		//When
		defaultWarehousingAsnFacade.confirmAsnReceipt(INTERNAL_ID);

		//Then
		verify(asnService).confirmAsnReceipt(INTERNAL_ID);
		verify(asnDataConverter).convert(advancedShippingNotice);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConfirmAsnReceiptWhenNullId()
	{
		//When
		defaultWarehousingAsnFacade.confirmAsnReceipt(null);
	}
}
