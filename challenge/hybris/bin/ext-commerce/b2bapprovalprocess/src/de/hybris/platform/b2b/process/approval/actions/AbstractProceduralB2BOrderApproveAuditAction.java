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

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.OrderHistoryService;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractProceduralB2BOrderApproveAuditAction extends AbstractProceduralB2BOrderApproveAction
{

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(AbstractProceduralB2BOrderApproveAuditAction.class);
	private L10NService l10NService;
	private OrderHistoryService orderHistoryService;
	private UserService userService;
	private I18NService i18NService;
	private SessionService sessionService;

	/**
	 * Creates a {@link OrderHistoryEntryModel} for a given order with a snapshot of the order see
	 * {@link OrderHistoryService#createHistorySnapshot(de.hybris.platform.core.model.order.OrderModel)}.
	 * 
	 * @param order
	 *           Original Order
	 * @param historyEntryOwner
	 *           Will be set as the owner of the {@link OrderHistoryEntryModel}
	 * @param messageKey
	 *           the resource key to the Resource Bundle see {@link L10NService#getLocalizedString(String, Object[])}
	 * @param localizationArguments
	 *           A list of values for the text arguments (see MessageFormat.format(String, Object[]))
	 * @return An {@link OrderHistoryEntryModel}
	 */
	public OrderHistoryEntryModel createAuditHistory(final OrderModel order, final ItemModel historyEntryOwner,
			final String messageKey, final Object[] localizationArguments)
	{
		final String auditMessage = getL10NService().getLocalizedString(messageKey, localizationArguments);
		final OrderModel snapshot = getOrderHistoryService().createHistorySnapshot(order);
		final OrderHistoryEntryModel historyEntry = modelService.create(OrderHistoryEntryModel.class);
		historyEntry.setTimestamp(new Date());
		historyEntry.setOrder(order);
		historyEntry.setDescription(auditMessage);
		historyEntry.setPreviousOrderVersion(snapshot);
		historyEntry.setOwner(historyEntryOwner);
		getOrderHistoryService().saveHistorySnapshot(snapshot);
		modelService.save(historyEntry);
		return historyEntry;
	}

	public abstract Object[] getLocalizationArguments(final OrderModel order);


	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected OrderHistoryService getOrderHistoryService()
	{
		return orderHistoryService;
	}

	@Required
	public void setOrderHistoryService(final OrderHistoryService orderHistoryService)
	{
		this.orderHistoryService = orderHistoryService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}
}
