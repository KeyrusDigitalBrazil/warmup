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

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.services.B2BEmailService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.task.RetryLaterException;

import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Sends an order approval email.
 */
public class InformOfOrderApproval extends AbstractProceduralB2BOrderAproveAction
{

	/**
	 * The Constant LOG.
	 */
	private static final Logger LOG = Logger.getLogger(InformOfOrderApproval.class);
	private B2BEmailService b2bEmailService;
	private String fromAddress;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.b2b.process.approval.actions.AbstractProceduralB2BOrderAproveAction#executeAction(de.hybris
	 * .platform.b2b.process.approval.model.B2BApprovalProcessModel)
	 */
	@Override
	public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel order = null;
		try
		{
			order = process.getOrder();
			Assert.notNull(order, String.format("Order of BusinessProcess %s should have be set", process));
			final B2BCustomerModel user = (B2BCustomerModel) order.getUser();
			if (LOG.isDebugEnabled())
			{
				LOG.debug(String.format("Process: %s in step %s order: %s user: %s ", process.getCode(), getClass(), order.getUnit(),
						user.getUid()));
			}
			try
			{
				final InternetAddress from = new InternetAddress(this.getFromAddress());
				final String emailTemplateCode = "order_confirmation";
				final String subject = "Order Approved";
				getB2bEmailService().sendEmail(
						getB2bEmailService().createOrderApprovalEmail(emailTemplateCode, order, user, from, subject));
			}
			catch (final Exception e)
			{
				LOG.error("failed to send email. ", e);
			}
		}
		catch (final Exception e)
		{
			LOG.error(e.getMessage(), e);
			this.handleError(order, e);

			throw new IllegalStateException(e.getMessage(), e);
		}
	}


	protected void handleError(final OrderModel order, final Exception e)
	{

		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(e.getMessage(), e);
	}

	public String getFromAddress()
	{
		return fromAddress;
	}

	@Required
	public void setFromAddress(final String fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	public B2BEmailService getB2bEmailService()
	{
		return b2bEmailService;
	}

	@Required
	public void setB2bEmailService(final B2BEmailService b2bEmailService)
	{
		this.b2bEmailService = b2bEmailService;
	}
}
