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
package de.hybris.platform.sap.sapproductconfigsombol.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.impl.HeaderSalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.impl.ItemListImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItemSalesDoc;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.backend.impl.erp.strategy.ProductConfigurationStrategyImplTest;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.salesdocument.backend.interf.erp.strategy.ProductConfigurationStrategy;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@UnitTest
public class CPQSalesDocumentERPHookTest
{

	private static final String SALES_DOCUMENT_NUMBER = "1232352353535";
	private SalesDocument salesDocument = null;
	private CPQItem item;
	private CPQSalesDocumentERPHook classUnderTest;

	@Mock
	private ProductConfigurationStrategy productConfigurationStrategy;
	@Mock
	private JCoConnection jcoConnection;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CPQSalesDocumentERPHook();
		classUnderTest.setProductConfigurationStrategy(productConfigurationStrategy);
		salesDocument = new OrderImpl();
		salesDocument.setHeader(new HeaderSalesDocument());
		final ItemListImpl itemList = new ItemListImpl();
		item = new CPQItemSalesDoc();
		item.setProductConfiguration(ProductConfigurationStrategyImplTest.getConfigModel(null));
		item.setConfigurable(true);
		item.setQuantity(new BigDecimal(2));
		itemList.add(item);
		salesDocument.setItemList(itemList);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.sap.sapproductconfigsombol.hook.CPQSalesDocumentERPHook#determineConfigurableItems(de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument)}.
	 */
	@Test
	public void testDetermineConfigurableItems()
	{
		final List<String> itemHandleList = classUnderTest.determineConfigurableItems(salesDocument);
		assertNotNull(itemHandleList);
		assertEquals(1, itemHandleList.size());
	}

	@Test
	public void testIsOrderNull()
	{
		assertFalse(classUnderTest.isOrder(salesDocument));
	}

	@Test
	public void testIsOrderCart()
	{
		salesDocument.getHeader().setSalesDocNumber("");
		assertFalse(classUnderTest.isOrder(salesDocument));
	}

	@Test
	public void testIsOrder()
	{
		salesDocument.getHeader().setSalesDocNumber(SALES_DOCUMENT_NUMBER);
		assertTrue(classUnderTest.isOrder(salesDocument));
	}

	@Test
	public void testAfterReadFromBackendOrderHistory()
	{
		salesDocument.getHeader().setSalesDocNumber(SALES_DOCUMENT_NUMBER);
		classUnderTest.afterReadFromBackend(salesDocument, jcoConnection);
		verify(productConfigurationStrategy).readConfiguration(Mockito.eq(jcoConnection), Mockito.eq(salesDocument), Mockito.any());
	}

	@Test
	public void testAfterReadFromBackendCart()
	{
		salesDocument.getHeader().setSalesDocNumber("");
		classUnderTest.afterReadFromBackend(salesDocument, jcoConnection);
		verify(productConfigurationStrategy, times(0)).readConfiguration(Mockito.eq(jcoConnection), Mockito.eq(salesDocument),
				Mockito.any());
	}

}
