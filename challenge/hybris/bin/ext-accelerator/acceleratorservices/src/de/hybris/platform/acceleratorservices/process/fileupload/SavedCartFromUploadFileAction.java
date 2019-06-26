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
package de.hybris.platform.acceleratorservices.process.fileupload;


import de.hybris.platform.acceleratorservices.cartfileupload.data.SavedCartFileUploadReportData;
import de.hybris.platform.acceleratorservices.enums.ImportStatus;
import de.hybris.platform.acceleratorservices.model.process.SavedCartFileUploadProcessModel;
import de.hybris.platform.acceleratorservices.process.strategies.SavedCartFileUploadStrategy;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.ProcessTaskLogModel;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.task.RetryLaterException;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SavedCartFromUploadFileAction extends AbstractSimpleDecisionAction<SavedCartFileUploadProcessModel>
{
	private static final Logger LOG = Logger.getLogger(SavedCartFromUploadFileAction.class);
	private TimeService timeService;
	private SavedCartFileUploadStrategy savedCartFileUploadStrategy;
	private ImpersonationService impersonationService;
	private L10NService l10NService;
	private String summaryMessageKey;
	private String cannotImportErrorMessageKey;
	private String statusCodeErrorMessageKey;
	private ClusterService clusterService;

	@Override
	public Transition executeAction(final SavedCartFileUploadProcessModel cartFileUploadProcessModel) throws RetryLaterException
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Started SavedCartFromUploadFileAction for SavedCartFileUploadProcess:" + cartFileUploadProcessModel.getCode());
		}
		final SavedCartFileUploadReportData savedCartFileUploadReportData = getImpersonationService().executeInContext(
				createImpersonationContext(cartFileUploadProcessModel),
				new ImpersonationService.Executor<SavedCartFileUploadReportData, ImpersonationService.Nothing>()
				{
					@Override
					public SavedCartFileUploadReportData execute() throws ImpersonationService.Nothing
					{
						final CartModel savedCart = cartFileUploadProcessModel.getSavedCart();
						try
						{
							return getSavedCartFileUploadStrategy().createSavedCartFromFile(
									cartFileUploadProcessModel.getUploadedFile(), savedCart);
						}
						catch (final IOException e)
						{
							setImportStatus(savedCart, ImportStatus.COMPLETED);
							LOG.error("Failed due to read the uploaded file:", e);
							throw new SystemException("Error when processing the upload file");
						}
					}
				});

		populateSavedCartDescriptionWithUploadReport(savedCartFileUploadReportData, cartFileUploadProcessModel);
		setImportStatus(cartFileUploadProcessModel.getSavedCart(), ImportStatus.COMPLETED);
		return Transition.OK;
	}

	protected void setImportStatus(final CartModel cartModel, final ImportStatus importStatus)
	{
		cartModel.setImportStatus(importStatus);
		getModelService().save(cartModel);
	}

	protected void populateSavedCartDescriptionWithUploadReport(final SavedCartFileUploadReportData savedCartFileUploadReportData,
			final SavedCartFileUploadProcessModel cartFileUploadProcessModel)
	{
		writeDebugLog("Imported the CSV file." + " Success: " + savedCartFileUploadReportData.getSuccessCount() + " Failed: "
				+ savedCartFileUploadReportData.getFailureCount());
		final CartModel cartModel = cartFileUploadProcessModel.getSavedCart();
		final Object[] localizationArguments =
		{ cartModel.getName(), savedCartFileUploadReportData.getSuccessCount(),
				savedCartFileUploadReportData.getPartialImportCount(), savedCartFileUploadReportData.getFailureCount() };
		final String message = getL10NService().getLocalizedString(getSummaryMessageKey(), localizationArguments);
		cartModel.setDescription(message);
		getModelService().save(cartModel);
		logErrorMessagesIntoProcessLogs(savedCartFileUploadReportData, cartFileUploadProcessModel);
	}

	protected void logErrorMessagesIntoProcessLogs(final SavedCartFileUploadReportData savedCartFileUploadReportData,
			final SavedCartFileUploadProcessModel savedCartFileUploadProcessModel)
	{
		final StringBuilder logMsgBuilder = new StringBuilder();
		for (final CommerceCartModification commerceCartModification : savedCartFileUploadReportData.getErrorModificationList())
		{
			final String productMsg = (commerceCartModification.getEntry() != null) ? commerceCartModification.getEntry()
					.getProduct().getCode() : "";
			logMsgBuilder.append(getL10NService().getLocalizedString(getCannotImportErrorMessageKey(), new String[] {productMsg}));
			if (commerceCartModification.getEntry() != null)
			{
				final String msgKey = getStatusCodeErrorMessageKey() + "." + commerceCartModification.getStatusCode().toLowerCase();
				logMsgBuilder.append(getL10NService().getLocalizedString(msgKey));
			}
			else
			{
				logMsgBuilder.append(commerceCartModification.getStatusCode());
			}
			logMsgBuilder.append(";");
		}

		final ProcessTaskLogModel processTaskLogModel = getModelService().create(ProcessTaskLogModel.class);
		processTaskLogModel.setStartDate(getTimeService().getCurrentTime());
		processTaskLogModel.setClusterId(Integer.valueOf(getClusterService().getClusterId()));
		processTaskLogModel.setActionId("savedCartFromUploadFileAction-importError");
		processTaskLogModel.setLogMessages(logMsgBuilder.toString());
		processTaskLogModel.setProcess(savedCartFileUploadProcessModel);
		processTaskLogModel.setEndDate(getTimeService().getCurrentTime());
		getModelService().save(processTaskLogModel);
	}

	protected ImpersonationContext createImpersonationContext(final SavedCartFileUploadProcessModel cartFileUploadProcessModel)
	{
		final ImpersonationContext impersonationContext = new ImpersonationContext();
		impersonationContext.setCurrency(cartFileUploadProcessModel.getCurrency());
		impersonationContext.setLanguage(cartFileUploadProcessModel.getLanguage());
		impersonationContext.setUser(cartFileUploadProcessModel.getUser());
		impersonationContext.setSite(cartFileUploadProcessModel.getSite());
		impersonationContext.setOrder(cartFileUploadProcessModel.getSavedCart());
		return impersonationContext;
	}


	protected void writeDebugLog(final String message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message);
		}
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	protected SavedCartFileUploadStrategy getSavedCartFileUploadStrategy()
	{
		return savedCartFileUploadStrategy;
	}

	@Required
	public void setSavedCartFileUploadStrategy(final SavedCartFileUploadStrategy savedCartFileUploadStrategy)
	{
		this.savedCartFileUploadStrategy = savedCartFileUploadStrategy;
	}

	protected ImpersonationService getImpersonationService()
	{
		return impersonationService;
	}

	@Required
	public void setImpersonationService(final ImpersonationService impersonationService)
	{
		this.impersonationService = impersonationService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected String getSummaryMessageKey()
	{
		return summaryMessageKey;
	}

	@Required
	public void setSummaryMessageKey(final String summaryMessageKey)
	{
		this.summaryMessageKey = summaryMessageKey;
	}

	protected String getCannotImportErrorMessageKey()
	{
		return cannotImportErrorMessageKey;
	}

	@Required
	public void setCannotImportErrorMessageKey(final String cannotImportErrorMessageKey)
	{
		this.cannotImportErrorMessageKey = cannotImportErrorMessageKey;
	}

	protected ClusterService getClusterService()
	{
		return clusterService;
	}

	@Required
	public void setClusterService(final ClusterService clusterService)
	{
		this.clusterService = clusterService;
	}

	protected String getStatusCodeErrorMessageKey()
	{
		return statusCodeErrorMessageKey;
	}

	@Required
	public void setStatusCodeErrorMessageKey(final String statusCodeErrorMessageKey)
	{
		this.statusCodeErrorMessageKey = statusCodeErrorMessageKey;
	}

}
