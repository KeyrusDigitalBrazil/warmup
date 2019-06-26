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
package de.hybris.platform.acceleratorservices.document.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.ordersplitting.model.ConsignmentProcessModel;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultDocumentCatalogFetchStrategyTest
{
	@InjectMocks
	private DefaultDocumentCatalogFetchStrategy documentCatalogFetchStrategy;

	@Mock
	private OrderProcessModel orderProcessModel;
	@Mock
	private ConsignmentProcessModel consignmentProcessModel;
	@Mock
	private ReturnProcessModel returnProcessModel;
	@Mock
	private OrderModel orderModel;
	@Mock
	private ConsignmentModel consignmentModel;
	@Mock
	private ReturnRequestModel returnRequestModel;
	@Mock
	private CMSSiteModel cmsSiteModel;
	@Mock
	private ContentCatalogModel contentCatalogModel;
	@Mock
	private CatalogVersionModel catalogVersionModel;

	@Before
	public void setUp()
	{
		when(orderProcessModel.getOrder()).thenReturn(orderModel);
		when(consignmentProcessModel.getConsignment()).thenReturn(consignmentModel);
		when(returnProcessModel.getReturnRequest()).thenReturn(returnRequestModel);

		when(consignmentModel.getOrder()).thenReturn(orderModel);
		when(returnRequestModel.getOrder()).thenReturn(orderModel);

		when(orderModel.getSite()).thenReturn(cmsSiteModel);
		when(cmsSiteModel.getContentCatalogs()).thenReturn(Collections.singletonList(contentCatalogModel));
		when(contentCatalogModel.getActiveCatalogVersion()).thenReturn(catalogVersionModel);
	}

	@Test
	public void testFetchWithOrderProcess()
	{
		//When
		final CatalogVersionModel fetch = documentCatalogFetchStrategy.fetch(orderProcessModel);

		//Then
		assertEquals(catalogVersionModel,fetch);
	}

}
