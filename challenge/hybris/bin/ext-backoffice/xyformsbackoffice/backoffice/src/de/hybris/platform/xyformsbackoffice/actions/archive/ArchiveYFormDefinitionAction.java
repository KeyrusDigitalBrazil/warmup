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
package de.hybris.platform.xyformsbackoffice.actions.archive;

import de.hybris.platform.xyformsservices.enums.YFormDefinitionStatusEnum;
import de.hybris.platform.xyformsservices.form.YFormService;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.dataaccess.facades.permissions.PermissionFacade;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.util.BackofficeSpringUtil;



/**
 * Actions that disables a yForm definition
 */
public class ArchiveYFormDefinitionAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<Object, String>
{
	private static final Logger LOG = Logger.getLogger(ArchiveYFormDefinitionAction.class);
	public static final String YFORM_DEFINITION_SOCKET_OUT = "yformDefinition";
	public static final String ARCHIVE_YFORM_DEFINITION_ENABLE = "archiveYFormDefinitionEnable";
	public static final String ARCHIVE_YFORM_DEFINITION_DISABLE = "archiveYFormDefinitionDisable";
	public static final String ARCHIVE_YFORM_DEFINITION_ERROR = "archiveYFormDefinitionError";

	@Resource(name = "yformService")
	private YFormService yformService;

	@Resource(name = "notificationService")
	private NotificationService notificationService;

	private PermissionFacade permissionFacade;


	@Override
	public ActionResult<String> perform(final ActionContext<Object> ctx)
	{
		try
		{
			final YFormDefinitionModel yformDefinition = (YFormDefinitionModel) ctx.getData();
			final String applicationId = yformDefinition.getApplicationId();
			final String formId = yformDefinition.getFormId();
			if (YFormDefinitionStatusEnum.DISABLED.equals(yformDefinition.getStatus()))
			{
				yformService.setFormDefinitionStatus(applicationId, formId, YFormDefinitionStatusEnum.ENABLED);

				notificationService.notifyUser(notificationService.getWidgetNotificationSource(ctx), ARCHIVE_YFORM_DEFINITION_ENABLE, NotificationEvent.Level.SUCCESS, yformDefinition);
			}
			else
			{
				yformService.setFormDefinitionStatus(applicationId, formId, YFormDefinitionStatusEnum.DISABLED);

				notificationService.notifyUser(notificationService.getWidgetNotificationSource(ctx), ARCHIVE_YFORM_DEFINITION_DISABLE, NotificationEvent.Level.SUCCESS, yformDefinition);
			}

			sendOutput(YFORM_DEFINITION_SOCKET_OUT, yformDefinition);

			return new ActionResult<String>(ActionResult.SUCCESS, yformDefinition.getFormId());
		}
		catch (final Exception e)
		{

			notificationService.notifyUser(notificationService.getWidgetNotificationSource(ctx), ARCHIVE_YFORM_DEFINITION_ERROR, NotificationEvent.Level.FAILURE, e);
			LOG.error(e.getMessage(), e);
			return new ActionResult<String>(ActionResult.ERROR, e.getMessage());
		}
	}

	@Override
	public boolean canPerform(final ActionContext<Object> ctx)
	{

		boolean allowed = false;

		final PermissionFacade permissionFacade = getPermissionFacade();
		if (permissionFacade != null)
		{
			if (ctx.getData() instanceof YFormDefinitionModel)
			{
				allowed = permissionFacade.canChangeInstance(ctx.getData());
			}
		}

		return allowed;
	}

	@Override
	public boolean needsConfirmation(final ActionContext<Object> ctx)
	{
		return true;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<Object> ctx)
	{
		final YFormDefinitionModel yformDefinition = (YFormDefinitionModel) ctx.getData();
		if (YFormDefinitionStatusEnum.DISABLED.equals(yformDefinition.getStatus()))
		{
			return ctx.getLabel("enable.confirm");
		}
		else
		{
			return ctx.getLabel("disable.confirm");
		}
	}


	protected PermissionFacade getPermissionFacade()
	{
		if (permissionFacade == null)
		{
			permissionFacade = BackofficeSpringUtil.getBean("permissionFacade", PermissionFacade.class);
		}
		return permissionFacade;
	}

	public void setPermissionFacade(final PermissionFacade permissionFacade)
	{
		this.permissionFacade = permissionFacade;
	}
}
