package de.hybris.platform.integration.cis.payment.backoffice.actions;


import de.hybris.platform.integration.cis.payment.model.CisFraudReportCronJobModel;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import com.hybris.backoffice.widgets.notificationarea.NotificationService;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent;
import com.hybris.backoffice.widgets.notificationarea.event.NotificationEventTypes;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import org.zkoss.lang.Strings;
import org.zkoss.zul.Messagebox;


public class ResetCisFraudReportCronJob implements CockpitAction<Object, Object>
{
	private static final String ACTION_RESET_FRAUD_REPORT_ENDTIME_ACTION_SUCCESS = "action.resetfraudreportendtimeaction.success";

	@Resource
	private ModelService modelService;
	@Resource
	private NotificationService notificationService;

	@Override
	public ActionResult<Object> perform(final ActionContext<Object> actionContext)
	{
		final Object data = actionContext.getData();
		if (data instanceof CisFraudReportCronJobModel)
		{

			final CisFraudReportCronJobModel job = (CisFraudReportCronJobModel) data;

			job.setLastFraudReportEndTime(null);
			modelService.save(job);


			getNotificationService()
					.notifyUser(Strings.EMPTY, NotificationEventTypes.EVENT_TYPE_OBJECT_UPDATE, NotificationEvent.Level.SUCCESS,
							actionContext.getLabel(ACTION_RESET_FRAUD_REPORT_ENDTIME_ACTION_SUCCESS));
			return new ActionResult(ActionResult.SUCCESS, job);
		}

		Messagebox.show(data + " (" + ActionResult.ERROR + ")");
		return new ActionResult(ActionResult.ERROR);

	}

	@Override
	public boolean canPerform(final ActionContext<Object> actionContext)
	{
		final Object data = actionContext.getData();
		return data instanceof CisFraudReportCronJobModel;
	}

	@Override
	public boolean needsConfirmation(final ActionContext<Object> actionContext)
	{
		return true;
	}

	@Override
	public String getConfirmationMessage(final ActionContext<Object> actionContext)
	{
		return actionContext.getLabel("action.resetfraudreportendtimeaction.confirm");
	}

	protected NotificationService getNotificationService()
	{
		return notificationService;
	}
}
