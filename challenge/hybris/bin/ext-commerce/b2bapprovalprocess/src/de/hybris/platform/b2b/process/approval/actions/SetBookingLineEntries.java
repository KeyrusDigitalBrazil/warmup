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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.enums.B2BBookingLineStatus;
import de.hybris.platform.b2b.model.B2BBookingLineEntryModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;


/**
 * Creates {@link B2BBookingLineEntryModel} of an approved order.
 */
public class SetBookingLineEntries extends AbstractSimpleB2BApproveOrderDecisionAction
{

	/** The Constant LOG. */
	private static final Logger LOG = Logger.getLogger(SetBookingLineEntries.class);

	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		modelService.refresh(order);
		try
		{
			for (final AbstractOrderEntryModel entry : order.getEntries())
			{
				final B2BBookingLineEntryModel bookingLineEntryModel = modelService.create(B2BBookingLineEntryModel.class);
				modelService.refresh(entry);
				bookingLineEntryModel.setOrderEntry((OrderEntryModel) entry);
				bookingLineEntryModel.setCostCenter(entry.getCostCenter());
				bookingLineEntryModel.setAmount(BigDecimal.valueOf(entry.getTotalPrice().doubleValue()));
				bookingLineEntryModel.setCurrency(order.getCurrency());
				bookingLineEntryModel.setOrderID(order.getCode());
				bookingLineEntryModel.setBookingStatus(B2BBookingLineStatus.OPEN);
				bookingLineEntryModel.setOrderEntryNr(entry.getEntryNumber());
				bookingLineEntryModel.setProduct(entry.getProduct().getCode());
				bookingLineEntryModel.setQuantity(entry.getQuantity());
				bookingLineEntryModel.setBookingDate(new Date());
				modelService.save(bookingLineEntryModel);
				if (LOG.isDebugEnabled())
				{
					LOG.debug(ToStringBuilder.reflectionToString(bookingLineEntryModel, ToStringStyle.SHORT_PREFIX_STYLE, false,
							B2BBookingLineEntryModel.class));
				}
			}

			return Transition.OK;
		}
		catch (final Exception e)
		{
			this.handleError(order, e);
		}
		return Transition.NOK;
	}

	protected void handleError(final OrderModel order, final Exception e)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(e.getMessage(), e);
	}

}
