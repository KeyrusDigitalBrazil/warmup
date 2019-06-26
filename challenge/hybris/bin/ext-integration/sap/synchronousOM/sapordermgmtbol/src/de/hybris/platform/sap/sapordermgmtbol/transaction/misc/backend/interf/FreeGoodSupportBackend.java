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
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.interf;

import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.SalesDocument;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.Schedline;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.ItemList;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


/**
 * Handles support of inclusive free goods for CRM, R/3 and DB backends. <br>
 * For this type of free good, handling in ISA differs from CRM and R/3: no free good sub items will be shown but all
 * sub item related info will be condensed into the main item.
 *
 */
public class FreeGoodSupportBackend
{

	private static final Log4JWrapper sapLogger = Log4JWrapper.getInstance(FreeGoodSupportBackend.class.getName());

	private FreeGoodSupportBackend()
	{

	}

	/**
	 * Checks all free good related sub items, removes them from the sales document and adjusts the corresponding main
	 * items. <br>
	 * Checks the exclusive free good sub items: they are set to be read- only. <br>
	 * Called from all sales document backend implementations after item read.
	 *
	 * @param salesDocument
	 *           the backend's view of the sales document
	 * @return true if there was a inclusive free good item
	 * @throws BackendException
	 *            Is thrown if there is an exception in the back end.
	 */

	public static boolean adjustSalesDocument(final SalesDocument salesDocument) throws BackendException
	{
		sapLogger.entering("adjustSalesDocument()");

		boolean freeGoodFound = false;

		// loop over all items of the document
		final ItemList itemList = salesDocument.getItemList();

		final Iterator<Item> itr = itemList.iterator();
		while (itr.hasNext())
		{

			final Item item = itr.next();

			// is this an inclusive free good related sub item?
			if (item.getItemUsage() == ItemBase.ItemUsage.FREE_GOOD_INCL || item.getItemUsage() == ItemBase.ItemUsage.FREE_GOOD_EXCL)
			{

				if (item.getItemUsage() == ItemBase.ItemUsage.FREE_GOOD_INCL)
				{

					// determine the parent item
					final Item parentItem = itemList.get(item.getParentId());

					freeGoodFound = nullableParentItem(freeGoodFound, item, parentItem);
				}

				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("is exclusive sub item, not changeable");
				}
			}
		}
		sapLogger.exiting();

		return freeGoodFound;

	}

	/**
	 * @param freeGoodFound
	 * @param item
	 * @param parentItem
	 * @return
	 * @throws BackendException
	 */
	private static boolean nullableParentItem(final boolean freeGoodFound, final Item item, final Item parentItem)
			throws BackendException
	{
		if (parentItem != null)
		{

			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("found parent for inclusive free good sub item: " + item.getTechKey());
			}

			final double subItemQuantity = item.getQuantity() != null ? item.getQuantity().doubleValue() : 0;
			final double parentItemQuantity = parentItem.getQuantity().doubleValue();
			double parentFreeQuantity = 0;
			parentFreeQuantity = getParentItemFreeQuantity(parentItem, parentFreeQuantity);

			// add quantities and store them in parent item. Update
			// quantity and old quantity
			final BigDecimal sumItemQuantities = BigDecimal.valueOf(subItemQuantity + parentItemQuantity);
			final BigDecimal sumFreeQuantities = BigDecimal.valueOf(subItemQuantity + parentFreeQuantity);
			// BigDecimal sumRemainingQuantities = new
			// BigDecimal(subItemRemainingQuantity


			itemCompare(parentItem, sumItemQuantities, sumFreeQuantities);
			// remove individual UI-element of skipped item
			return true;
		}
		else
		{
			throw new BackendException("no parent could be determined");
		}
	}

	/**
	 * @param parentItem
	 * @param sumItemQuantities
	 * @param sumFreeQuantities
	 */
	private static void itemCompare(final Item parentItem, final BigDecimal sumItemQuantities, final BigDecimal sumFreeQuantities)
	{
		if (sumItemQuantities.compareTo(sumFreeQuantities) == 0)
		{
			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug(
						"Item quantity is equal free good quantity. Check customizing for the product: " + parentItem.getProductId());
			}
		}
		else
		{


			parentItem.setFreeQuantity(sumFreeQuantities);


			if (sapLogger.isDebugEnabled())
			{
				sapLogger.debug("adjusted quantity for item: " + parentItem.getTechKey() + " . New quantity: "
						+ sumItemQuantities.toPlainString() + ". Free quantity: " + sumFreeQuantities.toPlainString());
			}


		}
	}

	/**
	 * @param parentItem
	 * @param parentFreeQuantity
	 * @return
	 */
	private static double getParentItemFreeQuantity(final Item parentItem, final double parentFreeQuantity)
	{
		double parentQuantity = parentFreeQuantity;
		if (parentItem.getFreeQuantity() != null && !(0 == parentItem.getFreeQuantity().compareTo(BigDecimal.ZERO)))
		{
			parentQuantity = parentItem.getFreeQuantity().doubleValue();
		}
		return parentQuantity;
	}

	/**
	 * Transfers schedule lines from one item to another. If there is no entry for a commited date, a new record is
	 * appended, the quantities are accumulated otherwise.
	 *
	 * @param item
	 *           the current item (source of schedule lines)
	 * @param parentItem
	 *           the target, gets new schedule lines
	 */
	protected static void addScheduleLines(final Item item, final Item parentItem)
	{

		sapLogger.entering("addScheduleLines()");

		// add schedule lines
		final List<Schedline> scheduleLines = item.getScheduleLines();
		final List<Schedline> parentScheduleLines = parentItem.getScheduleLines();

		for (int i = 0; i < scheduleLines.size(); i++)
		{
			// check if commited quantity is already in list
			final Schedline schedlLine = scheduleLines.get(i);
			final Date committedDate = schedlLine.getCommittedDate();

			final Schedline existingSchedLine = getSchedlineForDate(committedDate, parentScheduleLines);
			if (existingSchedLine != null)
			{
				final double oldQuantity = existingSchedLine.getCommittedQuantity().doubleValue();
				final double currentQuantity = schedlLine.getCommittedQuantity().doubleValue();
				final BigDecimal newQuantity = BigDecimal.valueOf(oldQuantity + currentQuantity);
				existingSchedLine.setCommittedQuantity(newQuantity);

				if (sapLogger.isDebugEnabled())
				{
					sapLogger.debug("adjusted schedule, old and current quantity: " + oldQuantity + ", " + currentQuantity);
				}

			}
			else
			{
				parentScheduleLines.add(scheduleLines.get(i));
			}

		}
		sapLogger.exiting();
	}

	/**
	 * Checks if a schedule line is present for a date. Searches via looping through the list of schedule lines.
	 *
	 * @param committedDate
	 *           the date for which we do the check
	 * @param scheduleLines
	 *           the list of schedule lines we look into
	 * @return null if no schedule line was found for this date
	 */
	protected static Schedline getSchedlineForDate(final Date committedDate, final List<Schedline> scheduleLines)
	{

		for (int i = 0; i < scheduleLines.size(); i++)
		{
			// check if commited quantity is already in list
			final Schedline schedlLine = scheduleLines.get(i);
			final Date currentCommittedDate = schedlLine.getCommittedDate();
			if (currentCommittedDate.equals(committedDate))
			{
				return schedlLine;
			}
		}
		return null;
	}

}
