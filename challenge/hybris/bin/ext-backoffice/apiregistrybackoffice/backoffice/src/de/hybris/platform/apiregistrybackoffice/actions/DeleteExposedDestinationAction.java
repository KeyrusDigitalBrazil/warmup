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
package de.hybris.platform.apiregistrybackoffice.actions;

import de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.delete.DeleteAction;


/**
 * Action responsible for deletion of ExposedDestination model
 */
public class DeleteExposedDestinationAction extends DeleteAction
{
	private static final String DELETE_API_SPECIFICATION_ACTION_CONFIRM = "deleteApiSpecificationAction.confirm";
	private static final String DELETE_API_SPECIFICATION_ACTION_FORBIDDEN = "deleteApiSpecificationAction.forbidden";
	private static final String DELETE_API_SPECIFICATION_ACTION_SUCCESS = "deleteApiSpecificationAction.success";
	private static final String DELETE_API_SPECIFICATION_ACTION_FAILURE = "deleteApiSpecificationAction.failure";

	private static final Logger LOG = LoggerFactory.getLogger(DeleteExposedDestinationAction.class);

	@Override
	public ActionResult<Object> perform(final ActionContext<Object> ctx)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.info(String.format("Deleting ApiConfiguration(s) : %s", ctx.getData()));
		}
		if (isRegisteredApi(ctx.getData()) || isRegisteredApiList(ctx.getData()))
		{
			getNotificationService().notifyUser(getNotificationService().getWidgetNotificationSource(ctx),
					ApiregistrybackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.FAILURE,
					ctx.getLabel(DELETE_API_SPECIFICATION_ACTION_FORBIDDEN));
			LOG.info("Registered API cannot be deleted.");
			return new ActionResult<>(ActionResult.ERROR);
		}
		final ActionResult result = super.perform(ctx);
		if (ActionResult.ERROR.equals(result.getResultCode()))
		{
			getNotificationService().notifyUser(getNotificationService().getWidgetNotificationSource(ctx),
					ApiregistrybackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.FAILURE,
					ctx.getLabel(DELETE_API_SPECIFICATION_ACTION_FAILURE, new String[]
					{ result.getResultMessage() }));
		}
		else
		{
			getNotificationService().notifyUser(getNotificationService().getWidgetNotificationSource(ctx),
					ApiregistrybackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.SUCCESS,
					ctx.getLabel(DELETE_API_SPECIFICATION_ACTION_SUCCESS));
		}
		return result;
	}

	protected boolean isRegisteredApi(final Object object)
	{
		return object instanceof ExposedDestinationModel
				&& StringUtils.isNotEmpty(((ExposedDestinationModel) object).getTargetId());
	}

	protected boolean isRegisteredApiList(final Object object)
	{
		return object instanceof Collection && ((Collection) object).stream().anyMatch(this::isRegisteredApi);
	}

	@Override
	public String getConfirmationMessage(final ActionContext<Object> ctx)
	{
		return ctx.getLabel(DELETE_API_SPECIFICATION_ACTION_CONFIRM);
	}
}

