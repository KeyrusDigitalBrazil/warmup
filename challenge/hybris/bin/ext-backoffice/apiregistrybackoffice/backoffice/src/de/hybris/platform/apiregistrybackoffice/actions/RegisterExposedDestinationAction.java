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
import de.hybris.platform.apiregistryservices.exceptions.ApiRegistrationException;
import de.hybris.platform.apiregistryservices.exceptions.DestinationNotFoundException;
import de.hybris.platform.apiregistryservices.model.ExposedDestinationModel;
import de.hybris.platform.apiregistryservices.services.ApiRegistrationService;

import java.util.Collections;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.dataaccess.context.impl.DefaultContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.util.CockpitGlobalEventPublisher;
import com.hybris.cockpitng.util.BackofficeSpringUtil;
import com.hybris.cockpitng.widgets.collectionbrowser.CollectionBrowserController;


/**
 * Action responsible for registering API
 */
public class RegisterExposedDestinationAction implements CockpitAction<ExposedDestinationModel, String>
{
	private static final String API_CONFIGURATION_REGISTER_ACTION_CONFIRMATION = "registerApiSpecificationAction.confirmation";
	private static final String API_CONFIGURATION_REGISTER_SUCCESS = "registerApiSpecificationAction.success";
	private static final String API_CONFIGURATION_REGISTER_FAILURE = "registerApiSpecificationAction.failure";
	private static final String API_CONFIGURATION_UPDATE_FAILED = "registerApiSpecificationAction.update.failure";
	private static final Logger LOG = LoggerFactory.getLogger(RegisterExposedDestinationAction.class);

	@Resource
	private ApiRegistrationService registrationService;

	@Resource
	private NotificationService notificationService;

	@Override
	public ActionResult<String> perform(final ActionContext<ExposedDestinationModel> ctx)
	{
		final ExposedDestinationModel destination = ctx.getData();

		LOG.info("Triggering Register Exposed Destination Action\n\tExposed Destination : [{}]", destination.getId());

		try
		{
			getRegistrationService().registerExposedDestination(destination);
		}
		catch (final DestinationNotFoundException e)
		{
			LOG.warn("Registering the Exposed Destination with obsolete targetId", e);
			return registerExposedDestinationWithObsoleteTargetId(destination, ctx);
		}
		catch (final ApiRegistrationException e)
		{
			return registrationFailed(ctx, destination, e);
		}

		final String successMessage = ctx.getLabel(API_CONFIGURATION_REGISTER_SUCCESS, new String[]
		{ destination.getUrl() });
		getNotificationService().notifyUser(getNotificationService().getWidgetNotificationSource(ctx),
				ApiregistrybackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.SUCCESS, successMessage);
		LOG.info(successMessage);

		final DefaultContext context = new DefaultContext();
		context.addAttribute(CollectionBrowserController.SHOULD_RELOAD_AFTER_UPDATE, Boolean.TRUE);
		final CockpitGlobalEventPublisher publisher = BackofficeSpringUtil.getBean("eventPublisher");
		publisher.publish(ObjectCRUDHandler.OBJECTS_UPDATED_EVENT, Collections.singleton(destination), context);

		return new ActionResult<>(ActionResult.SUCCESS);
	}

	protected ActionResult<String> registrationFailed(final ActionContext<ExposedDestinationModel> ctx,
			final ExposedDestinationModel destination, final ApiRegistrationException e)
	{
		final String errorMessage = ctx.getLabel(API_CONFIGURATION_REGISTER_FAILURE, new String[]
		{ destination.getId() }) + ' ' + e.getMessage();

		getNotificationService().notifyUser(getNotificationService().getWidgetNotificationSource(ctx),
				ApiregistrybackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.FAILURE, errorMessage);
		LOG.error(errorMessage, e);
		return new ActionResult<>(ActionResult.ERROR);
	}

	private ActionResult<String> registerExposedDestinationWithObsoleteTargetId(final ExposedDestinationModel destination,
			final ActionContext<ExposedDestinationModel> ctx)
	{
		destination.setTargetId(null);
		try
		{
			getRegistrationService().registerExposedDestination(destination);
		}
		catch (final ApiRegistrationException e)
		{
			return registrationFailed(ctx, destination, e);
		}

		final String notificationMessage = ctx.getLabel(API_CONFIGURATION_UPDATE_FAILED, new String[]
		{ destination.getId() });

		getNotificationService().notifyUser(getNotificationService().getWidgetNotificationSource(ctx),
				ApiregistrybackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING, notificationMessage);
		LOG.warn(notificationMessage);
		return new ActionResult<>(ActionResult.SUCCESS);
	}

	@Override
	public boolean canPerform(final ActionContext<ExposedDestinationModel> ctx)
	{
		final ExposedDestinationModel apiConfiguration = ctx.getData();
		return apiConfiguration != null && apiConfiguration.isActive();
	}

	@Override
	public boolean needsConfirmation(final ActionContext<ExposedDestinationModel> ctx)
	{
		return ctx != null && ctx.getData() != null;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<ExposedDestinationModel> ctx)
	{
		final ExposedDestinationModel apiConfiguration = ctx.getData();
		return ctx.getLabel(API_CONFIGURATION_REGISTER_ACTION_CONFIRMATION, new String[]
		{ apiConfiguration.getId() });
	}

	protected ApiRegistrationService getRegistrationService()
	{
		return registrationService;
	}

	protected NotificationService getNotificationService()
	{
		return notificationService;
	}
}
