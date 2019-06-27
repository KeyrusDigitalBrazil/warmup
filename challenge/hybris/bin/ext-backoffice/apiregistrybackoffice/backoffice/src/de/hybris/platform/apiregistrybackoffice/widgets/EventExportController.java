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
package de.hybris.platform.apiregistrybackoffice.widgets;

import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import com.hybris.cockpitng.annotations.SocketEvent;
import de.hybris.platform.apiregistrybackoffice.constants.ApiregistrybackofficeConstants;
import de.hybris.platform.apiregistryservices.event.EventExportDisabledEvent;
import de.hybris.platform.apiregistryservices.event.EventExportEnabledEvent;
import de.hybris.platform.apiregistryservices.utils.EventExportUtils;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.util.Config;
import org.zkoss.zul.Messagebox;

import com.hybris.cockpitng.annotations.ViewEvent;
import com.hybris.cockpitng.util.DefaultWidgetController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;

import javax.annotation.Resource;


/**
 * Event Export Configuration Widget Controller
 */
public class EventExportController extends DefaultWidgetController
{
	private static final long serialVersionUID = 1L;
	private static final String ENABLED = "label.event.state.enabled";
	private static final String DISABLED = "label.event.state.disabled";
	private static final String NOTIFICATION_CLUSTER = "label.currentState.cluster.notification";
	private static final String NOTIFICATION_NODE = "label.currentState.node.notification";
	private static final String CONFIRMATION = "button.label.disable.confirmation";
	private static final String WIDGET_TITLE = "label.eventexportwidget.title";

	@Resource(name = "eventService")
	private transient EventService eventService;
	@Resource
	private transient NotificationService notificationService;

	@Wire
	private Label stateLabel;
	@Wire
	private Button enableCluster;
	@Wire
	private Button disableCluster;
	@Wire
	private Button enableNode;
	@Wire
	private Button disableNode;

	public void onTimer$timer(final Event e)
	{
		this.stateLabel.setValue(getStateTitle());
	}

	@SocketEvent(socketId = "openWidget")
	public void initializeWithContext(final Object data)
	{
		setWidgetTitle(getLabel(WIDGET_TITLE, new String[]{Registry.getCurrentTenant().getTenantID()}));
		this.stateLabel.setValue(getStateTitle());
	}

	@ViewEvent(componentID = "enableCluster", eventName = Events.ON_CLICK)
	public void enableCluster()
	{
		eventService.publishEvent(new EventExportEnabledEvent());
		notificationService.notifyUser(getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS, getLabel(NOTIFICATION_CLUSTER, new Object[]
				{ getLabel(ENABLED) }));

	}

	@ViewEvent(componentID = "disableCluster", eventName = Events.ON_CLICK)
	public void disableCluster()
	{
		Messagebox.show(null, getLabel(CONFIRMATION), new Messagebox.Button[]
		{ Messagebox.Button.OK, Messagebox.Button.CANCEL }, null, null, null, clickEvent -> {
			if (clickEvent.getButton() != null && Messagebox.Button.OK.equals(clickEvent.getButton()))
			{
				eventService.publishEvent(new EventExportDisabledEvent());
				notificationService.notifyUser(getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.SUCCESS, getLabel(NOTIFICATION_CLUSTER, new Object[]
				{ getLabel(DISABLED) }));
			}
		});
	}

	@ViewEvent(componentID = "enableNode", eventName = Events.ON_CLICK)
	public void enableNode()
	{
		Config.setParameter(EventExportUtils.EXPORTING_PROP, String.valueOf(true));
		notificationService.notifyUser(getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS, getLabel(NOTIFICATION_NODE, new Object[]
				{ getLabel(ENABLED) }));
	}

	@ViewEvent(componentID = "disableNode", eventName = Events.ON_CLICK)
	public void disableNode()
	{
		Messagebox.show(null, getLabel(CONFIRMATION), new Messagebox.Button[]
		{ Messagebox.Button.OK, Messagebox.Button.CANCEL }, null, null, null, clickEvent -> {
			if (clickEvent.getButton() != null && Messagebox.Button.OK.equals(clickEvent.getButton()))
			{
				Config.setParameter(EventExportUtils.EXPORTING_PROP, String.valueOf(false));
				notificationService.notifyUser(getWidgetInstanceManager(), ApiregistrybackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.SUCCESS, getLabel(NOTIFICATION_NODE, new Object[]
				{ getLabel(DISABLED) }));
			}
		});

	}

	protected String getStateTitle()
	{
		final String key = Config.getBoolean(EventExportUtils.EXPORTING_PROP, false) ? ENABLED : DISABLED;
		return getLabel(key);
	}
}
