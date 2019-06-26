/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousing.asn.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.InStockStatus;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.asn.dao.AsnDao;
import de.hybris.platform.warehousing.asn.service.AsnWorkflowService;
import de.hybris.platform.warehousing.asn.strategy.AsnReleaseDateStrategy;
import de.hybris.platform.warehousing.asn.strategy.BinSelectionStrategy;
import de.hybris.platform.warehousing.enums.AsnStatus;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousing.stock.services.WarehouseStockService;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.Assert.notNull;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAsnServiceTest
{

	private static final String ASN_INTERNAL_ID = "asn_internal_id1";
	private static final String CAMERA_CODE = "camera";
	private static final int CAMERA_AVAILABILITY = 80;

	private static final Date RELEASE_DATE = Calendar.getInstance().getTime();
	private static final Date DELAYED_RELEASE_DATE = new Date(RELEASE_DATE.getTime() + 1);

	@InjectMocks
	private final DefaultAsnService asnService = new DefaultAsnService();

	@Mock
	private ModelService modelService;
	@Mock
	private AsnReleaseDateStrategy asnReleaseDateStrategy;
	@Mock
	private BinSelectionStrategy binSelectionStrategy;
	@Mock
	private WarehouseStockService warehouseStockService;
	@Mock
	private AsnDao asnDao;
	@Mock
	private AsnWorkflowService asnWorkflowService;
	@Mock
	private WarehouseModel warehouse;
	private AdvancedShippingNoticeModel asn;
	private AdvancedShippingNoticeEntryModel asnEntry;
	private StockLevelModel createdStock;

	final Map<String, Integer> bins = new HashMap<>();

	@Before
	public void setUp() throws Exception
	{
		asn = new AdvancedShippingNoticeModel();
		asn.setReleaseDate(RELEASE_DATE);
		asn.setWarehouse(warehouse);
		asn.setStatus(AsnStatus.CREATED);
		asn.setInternalId(ASN_INTERNAL_ID);
		asnEntry = new AdvancedShippingNoticeEntryModel();
		asnEntry.setProductCode(CAMERA_CODE);
		asnEntry.setQuantity(CAMERA_AVAILABILITY);
		asn.setAsnEntries(Collections.singletonList(asnEntry));

		createdStock = new StockLevelModel();
		createdStock.setProductCode(CAMERA_CODE);
		createdStock.setAvailable(CAMERA_AVAILABILITY);
		createdStock.setReleaseDate(DELAYED_RELEASE_DATE);
		createdStock.setWarehouse(warehouse);
		createdStock.setBin(null);

		bins.put(null, asnEntry.getQuantity());
		when(binSelectionStrategy.getBinsForAsnEntry(any())).thenReturn(bins);
		when(asnReleaseDateStrategy.getReleaseDateForStockLevel(any())).thenReturn(DELAYED_RELEASE_DATE);
		when(warehouseStockService
				.createStockLevel(eq(CAMERA_CODE), eq(warehouse), eq(CAMERA_AVAILABILITY), eq(InStockStatus.NOTSPECIFIED),
						eq(DELAYED_RELEASE_DATE), any())).thenReturn(createdStock);
		when(asnDao.getAsnForInternalId(ASN_INTERNAL_ID)).thenReturn(asn);
		when(asnDao.getStockLevelsForAsn(asn)).thenReturn(Collections.singletonList(createdStock));
	}

	@Test
	public void shouldProcessAsn()
	{
		//When
		asnService.processAsn(asn);

		//Then
		verify(modelService, times(1)).save(createdStock);
		assertEquals(asnEntry, createdStock.getAsnEntry());
		verify(warehouseStockService)
				.createStockLevel(CAMERA_CODE, warehouse, CAMERA_AVAILABILITY, InStockStatus.NOTSPECIFIED, DELAYED_RELEASE_DATE,
						null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotProcessAsnWhenNoEntry()
	{
		//Given
		asn.setAsnEntries(Collections.emptyList());

		//When
		asnService.processAsn(asn);
	}

	@Test
	public void shouldConfirmAsnReceipt()
	{
		//When
		final AdvancedShippingNoticeModel updatedAsn = asnService.confirmAsnReceipt(ASN_INTERNAL_ID);

		//Then
		verify(asnDao).getAsnForInternalId(ASN_INTERNAL_ID);
		verify(modelService).save(asn);
		assertEquals(AsnStatus.RECEIVED, updatedAsn.getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConfirmAsnReceiptWhenNullId()
	{
		//When
		asnService.confirmAsnReceipt(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailConfirmAsnReceiptWhenWrongStatus()
	{
		//Given
		asn.setStatus(AsnStatus.CANCELLED);

		//When
		asnService.confirmAsnReceipt(null);
	}

	@Test
	public void shouldGetAsn()
	{
		//When
		final AdvancedShippingNoticeModel asn = asnService.getAsnForInternalId(ASN_INTERNAL_ID);

		verify(asnDao).getAsnForInternalId(ASN_INTERNAL_ID);
		notNull(asn);
		assertEquals(ASN_INTERNAL_ID, asn.getInternalId());
	}

	@Test
	public void shouldFailGetAsnWhenWrongInternalId()
	{
		//When
		final AdvancedShippingNoticeModel asn = asnService.getAsnForInternalId("abc");

		//Then
		assertEquals(null, asn);
	}

	@Test
	public void shouldGetStockLevelsForAsn()
	{
		//When
		final List<StockLevelModel> stockLevels = asnService.getStockLevelsForAsn(asn);

		//Then
		verify(asnDao).getStockLevelsForAsn(asn);
		assertEquals(1, stockLevels.size());
	}

	@Test
	public void shouldCancelAsn()
	{
		//When
		final AdvancedShippingNoticeModel cancelledAsn = asnService.cancelAsn(ASN_INTERNAL_ID);

		//Then
		verify(asnDao).getAsnForInternalId(ASN_INTERNAL_ID);
		verify(asnWorkflowService).startAsnCancellationWorkflow(asn);
		verify(modelService).save(asn);
		assertEquals(AsnStatus.CANCELLED, cancelledAsn.getStatus());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelAsnWrongStatus()
	{
		//Given
		asn.setStatus(AsnStatus.RECEIVED);

		//When
		asnService.cancelAsn(ASN_INTERNAL_ID);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCancelAsnNullId()
	{
		//When
		asnService.cancelAsn(null);
	}

	@Test
	public void shouldCreateStockLevel()
	{
		//When
		asnService.createStockLevel(asnEntry, warehouse, DELAYED_RELEASE_DATE);

		//Then
		verify(binSelectionStrategy).getBinsForAsnEntry(asnEntry);
		verify(warehouseStockService)
				.createStockLevel(CAMERA_CODE, warehouse, CAMERA_AVAILABILITY, InStockStatus.NOTSPECIFIED, DELAYED_RELEASE_DATE,
						null);
	}

}
