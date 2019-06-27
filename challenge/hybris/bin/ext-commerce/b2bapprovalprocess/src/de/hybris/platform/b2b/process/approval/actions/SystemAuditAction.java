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

import de.hybris.platform.b2b.enums.PermissionStatus;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderhistory.model.OrderHistoryEntryModel;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.task.RetryLaterException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SystemAuditAction extends AbstractProceduralB2BOrderApproveAuditAction
{
	private static final Logger LOG = Logger.getLogger(SystemAuditAction.class);
	private String messageKey;
	private B2BPermissionResultHelper permissionResultHelper;

	@Override
	public void executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		final OrderModel order = process.getOrder();
		try
		{
			this.getSessionService().executeInLocalView(new SessionExecutionBody()
			{
				@Override
				public void executeWithoutResult()
				{

					if (order.getLocale() != null)
					{
						getI18NService().setCurrentLocale(new Locale(order.getLocale()));
					}

					final OrderHistoryEntryModel historyEntry = createAuditHistory(order, getUserService().getAdminUser(),
							getMessageKey(), getLocalizationArguments(order));
					if (LOG.isDebugEnabled())
					{
						LOG.debug("Created " + historyEntry + " for order " + order.getCode());
					}


				}
			});
		}
		catch (final Exception e)
		{
			// if something failed go on to the ok transition anyway for now.
			handleError(order, e);
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

	@Override
	public Object[] getLocalizationArguments(final OrderModel order)
	{
		/**
		 * localization arguments contains the following index 0: order code 1. order user uid 2. comma separated list of
		 * approver uids 3. comma separated list of permissions either approver permissions or order user's permissions.
		 */
		final B2BCustomerModel orderUser = (B2BCustomerModel) order.getUser();
		Collection<B2BPermissionResultModel> b2BPermissionResultModels = permissionResultHelper
				.filterResultByPermissionStatus(order.getPermissionResults(), PermissionStatus.PENDING_APPROVAL);
		List<B2BCustomerModel> approvers = permissionResultHelper.getApproversWithPermissionStatus(order.getPermissionResults(),
				PermissionStatus.PENDING_APPROVAL);

		// the process has gone into approval state retrieve the approvers and their permissions
		if (order.getWorkflow() != null)
		{
			// extract the approvers permissions
			b2BPermissionResultModels = permissionResultHelper.filterResultByPermissionStatus(order.getPermissionResults(),
					PermissionStatus.PENDING_APPROVAL);
			approvers = permissionResultHelper.getApproversWithPermissionStatus(order.getPermissionResults(),
					PermissionStatus.PENDING_APPROVAL);
		}
		String comment = "";
		for (final B2BPermissionResultModel result : b2BPermissionResultModels)
		{
			if (StringUtils.isNotBlank(result.getNote(this.getI18NService().getCurrentLocale())))
			{
				comment = result.getNote();
				break;
			}
		}

		final String localizedPermissionNames = StringUtils
				.join(CollectionUtils
						.collect(
								CollectionUtils
										.collect(b2BPermissionResultModels,
												new BeanToPropertyValueTransformer(
														B2BPermissionResultModel.PERMISSION + "." + B2BPermissionModel.ITEMTYPE)),
								new Transformer()
								{
									@Override
									public Object transform(final Object o)
									{
										return getL10NService().getLocalizedString("type." + String.valueOf(o).toLowerCase() + ".name");
									}
								})
						.toArray(), ",");

		final String approverUids = StringUtils
				.join(CollectionUtils.collect(approvers, new BeanToPropertyValueTransformer(B2BCustomerModel.UID)).toArray(), ",");

		final Object[] localizationArguments =
		{ order.getCode(), orderUser.getUid(), approverUids, localizedPermissionNames, comment };
		return localizationArguments;
	}


	protected B2BPermissionResultHelper getPermissionResultHelper()
	{
		return permissionResultHelper;
	}

	@Required
	public void setPermissionResultHelper(final B2BPermissionResultHelper permissionResultHelper)
	{
		this.permissionResultHelper = permissionResultHelper;
	}

	protected String getMessageKey()
	{
		return messageKey;
	}

	@Required
	public void setMessageKey(final String messageKey)
	{
		this.messageKey = messageKey;
	}

}
