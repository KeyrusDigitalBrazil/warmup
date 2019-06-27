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
package de.hybris.platform.b2b.services.impl;

import de.hybris.platform.b2b.mail.OrderInfoContextDtoFactory;
import de.hybris.platform.b2b.mail.impl.OrderInfoContextDto;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BEmailService;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.order.OrderModel;
import java.io.StringWriter;
import java.util.Collections;
import javax.mail.internet.InternetAddress;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * Default implementation of the {@link B2BEmailService}.
 * 
 * @spring.bean b2bEmailService
 */
public class DefaultB2BEmailService implements B2BEmailService
{

	private static final Logger LOG = Logger.getLogger(DefaultB2BEmailService.class);
	private RendererService rendererService;
	private OrderInfoContextDtoFactory<OrderInfoContextDto> orderInfoContextDtoFactory;

	@Override
	public HtmlEmail createOrderApprovalEmail(final String emailTemplateCode, final OrderModel order, final B2BCustomerModel user,
			final InternetAddress from, final String subject) throws EmailException
	{
		// creates a mail instance with default properties
		final HtmlEmail email = (HtmlEmail) de.hybris.platform.util.mail.MailUtils.getPreConfiguredEmail();

		// clear the reply to list populated by hybris MailUtils with the senders address.
		email.setReplyTo(Collections.singletonList(from));
		email.setFrom(from.getAddress(), from.getPersonal());
		email.addTo(user.getEmail());
		email.setSubject(subject);

		final RendererTemplateModel emailTemplate = getRendererService().getRendererTemplateForCode(emailTemplateCode);
		Assert.notNull(emailTemplate, String.format("Email template with code '%s' was not found in Hybris.", emailTemplateCode));

		// Create a writer where the rendered text will be written to
		final OrderInfoContextDto ctx = getOrderInfoContextDtoFactory().createOrderInfoContextDto(order);
		final StringWriter mailMessageWriter = new StringWriter();
		// Render the template using the context object
		getRendererService().render(emailTemplate, ctx, mailMessageWriter);
		email.setHtmlMsg(mailMessageWriter.toString());
		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("created approval email %s", ReflectionToStringBuilder.toString(email)));
		}
		return email;
	}

	@Override
	public HtmlEmail createOrderRejectionEmail(final String emailTemplateCode, final OrderModel order,
			final B2BCustomerModel user, final InternetAddress from, final String subject) throws EmailException
	{
		// creates a mail instance with default properties
		final HtmlEmail email = (HtmlEmail) de.hybris.platform.util.mail.MailUtils.getPreConfiguredEmail();
		// clear the reply to list populated by hybris MailUtils with the senders address.
		email.setReplyTo(Collections.singletonList(from));
		email.setFrom(from.getAddress(), from.getPersonal());
		email.addTo(user.getEmail());
		email.setSubject("Order Rejected");

		final RendererTemplateModel emailTemplate = getRendererService().getRendererTemplateForCode(emailTemplateCode);
		final OrderInfoContextDto ctx = getOrderInfoContextDtoFactory().createOrderInfoContextDto(order);
		final StringWriter mailMessageWriter = new StringWriter();
		// Render the template using the context object
		getRendererService().render(emailTemplate, ctx, mailMessageWriter);
		email.setHtmlMsg(mailMessageWriter.toString());
		if (LOG.isInfoEnabled())
		{
			LOG.info(String.format("Sending rejection email %s", ReflectionToStringBuilder.toString(email)));
		}
		return email;
	}

	@Override
	public void sendEmail(final HtmlEmail email) throws EmailException
	{
		email.send();
	}

	protected RendererService getRendererService()
	{
		return rendererService;
	}

	@Required
	public void setRendererService(final RendererService rendererService)
	{
		this.rendererService = rendererService;
	}

	protected OrderInfoContextDtoFactory<OrderInfoContextDto> getOrderInfoContextDtoFactory()
	{
		return orderInfoContextDtoFactory;
	}

	@Required
	public void setOrderInfoContextDtoFactory(final OrderInfoContextDtoFactory<OrderInfoContextDto> orderInfoContextDtoFactory)
	{
		this.orderInfoContextDtoFactory = orderInfoContextDtoFactory;
	}
}
