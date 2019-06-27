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
package de.hybris.platform.b2b.services;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.model.order.OrderModel;
import javax.mail.internet.InternetAddress;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;


/**
 * The Interface B2BEmailService. Service is responsible for sending Approval and Rejection emails to a B2BCustomer.
 * 
 * @spring.bean b2bEmailService
 */
public interface B2BEmailService
{

	/**
	 * Creates an Approval email to be sent to the B2BCustomer.
	 * 
	 * @param emailTemplateCode
	 *           used to determine the {@link RendererTemplateModel} to be used
	 * @param order
	 *           the order
	 * @param user
	 *           the user the email will be sent to
	 * @param from
	 *           the email address of the sender
	 * @param subject
	 *           the title of the email message
	 * @return the html encoded email message
	 * @throws EmailException
	 */
	HtmlEmail createOrderApprovalEmail(String emailTemplateCode, OrderModel order, B2BCustomerModel user, InternetAddress from,
			String subject) throws EmailException;

	/**
	 * Creates a Rejection email to be sent to the B2BCustomer.
	 * 
	 * @param emailTemplateCode
	 *           used to determine the {@link RendererTemplateModel} to be used
	 * @param order
	 * @param user
	 *           the user the email will be sent to
	 * @param from
	 *           the email address of the sender
	 * @param subject
	 *           the title of the email message
	 * @return the html encoded email message
	 * @throws EmailException
	 */
	HtmlEmail createOrderRejectionEmail(String emailTemplateCode, OrderModel order, B2BCustomerModel user, InternetAddress from,
			String subject) throws EmailException;

	/**
	 * Sends the email out to the B2BCustomer
	 * 
	 * @param email
	 *           the html email message to be sent
	 * @throws EmailException
	 */
	void sendEmail(HtmlEmail email) throws EmailException;
}
