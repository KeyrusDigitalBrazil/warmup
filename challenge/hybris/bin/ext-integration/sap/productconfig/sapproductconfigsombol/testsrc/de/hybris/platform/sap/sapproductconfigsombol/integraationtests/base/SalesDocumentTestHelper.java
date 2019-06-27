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
package de.hybris.platform.sap.sapproductconfigsombol.integraationtests.base;

import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.core.common.TechKey;
import de.hybris.platform.sap.core.common.exceptions.ApplicationBaseRuntimeException;
import de.hybris.platform.sap.sapordermgmtbol.order.businessobject.interf.PartnerList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl.ShipToImpl;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Basket;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Order;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.header.businessobject.interf.Header;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.OrderImpl;

import java.math.BigDecimal;
import java.util.Iterator;


@SuppressWarnings("javadoc")
public enum SalesDocumentTestHelper
{
	// use of enum makes it a safe singleton .
	INSTANCE;

	public Order createAndPrepareOrder(final String soldToParty)
	{
		final Order order = new OrderImpl();


		prepareOrder(soldToParty, order);

		return order;
	}

	public Item getItemAtPostion(final ItemList items, final Integer posNumber)
	{
		final Iterator<Item> i = items.iterator();
		Item nextItem = null;
		Item returnItem = null;
		while (i.hasNext())
		{
			nextItem = i.next();
			final Integer posNr = nextItem.getNumberInt();
			if (posNr.equals(posNumber))
			{
				returnItem = nextItem;
				break;
			}

		}
		return returnItem;
	}

	public void prepareOrder(final String soldToParty, final Order order)
	{

		final PartnerList partnerList = preparePartnerList(soldToParty);
		System.out.println("SoldTo Party: " + soldToParty);

		try
		{
			order.init(partnerList);
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);
		}

		final ShipToImpl shipTo = new ShipToImpl();
		final Header header = order.getHeader();
		header.setShipTo(shipTo);

	}

	public void prepareCart(final String soldToParty, final Basket cart)
	{

		final PartnerList partnerList = preparePartnerList(soldToParty);
		System.out.println("SoldTo Party: " + soldToParty);

		try
		{
			cart.init(partnerList);
		}
		catch (final CommunicationException e)
		{
			throw new ApplicationBaseRuntimeException("Not handled '" + e.getClass().getName() + "' exception.", e);
		}

		final ShipToImpl shipTo = new ShipToImpl();
		final Header header = cart.getHeader();
		header.setShipTo(shipTo);

	}


	//	/**
	//	 * Initializes the RfQ.<br>
	//	 * 
	//	 * @param config
	//	 * @param soldToParty
	//	 * @param bupama
	//	 * @param rfq
	//	 * @param processType
	//	 */
	//	public void prepare(final TransactionConfiguration config,
	//			final String soldToParty, final BusinessPartnerManager bupama,
	//			final RfQ rfq, final String processType) {
	//
	//		rfq.setTransactionConfiguration(config);
	//
	//		try {
	//			rfq.init(preparePartnerList(soldToParty), processType);
	//		} catch (final CommunicationException e) {
	//			throw new ApplicationBaseRuntimeException("Not handled '"
	//					+ e.getClass().getName() + "' exception.", e);
	//		}
	//
	//		rfq.getHeader().setShipTo(new ShipToImpl());
	//	}

	public PartnerList preparePartnerList(final String soldToParty)
	{

		final de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListImpl partnerList = new de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListImpl();
		final TechKey tk = new TechKey(soldToParty);
		final de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListEntryImpl partnerListEntry = new de.hybris.platform.sap.sapordermgmtbol.transaction.order.businessobject.impl.PartnerListEntryImpl();
		partnerListEntry.setPartnerTechKey(tk);
		partnerListEntry.setPartnerId(soldToParty);
		partnerList.setSoldTo(partnerListEntry);
		partnerList.setPartner("SHIP_TO", partnerListEntry);

		return partnerList;
	}

	public Item addItemTo(final SalesDocument salesDoc, final String productId, final String quantity, final String handle)
	{
		final Item item = salesDoc.createItem();
		System.out.println("ProductID:" + productId);
		item.setProductId(productId);
		item.setQuantity(new BigDecimal(quantity));
		item.setParentId(TechKey.EMPTY_KEY);
		item.setHandle(handle);
		salesDoc.addItem(item);
		return item;
	}

	public Item addItemTo(final SalesDocument salesDoc, final String productId, final String quantity, final String handle,
			final String unit)
	{

		final Item item = addItemTo(salesDoc, productId, quantity, handle);
		item.setUnit(unit);

		return item;
	}

}
