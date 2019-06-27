/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.synchronization.impl;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.SyncItemCronJob;
import de.hybris.platform.catalog.jalo.SyncItemJob;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemCronJobModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import de.hybris.platform.cmsfacades.data.SyncRequestData;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.synchronization.SynchronizationFacade;
import de.hybris.platform.cmsfacades.synchronization.SynchronizationInProgressException;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.enums.JobLogLevel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;


/**
 * Default implementation of the synchronization facade.
 */
public class DefaultSynchronizationFacade implements SynchronizationFacade
{
	private ModelService modelService;

	private CatalogVersionService catalogVersionService;

	private Converter<Optional<CronJobModel>, SyncJobData> syncJobConverter;

	private Populator<Optional<SyncItemJobModel>, SyncJobData> syncItemJobToSyncJobDataPopulator;

	private Validator catalogSynchronizationCompositeValidator;

	private Validator syncJobRequestCompositeValidator;

	private Validator basicSyncJobRequestCompositeValidator;

	private FacadeValidationService facadeValidationService;

	private CronJobService cronJobService;


	@Override
	public SyncJobData getSynchronizationByCatalogSourceTarget(final SyncRequestData syncJobRequest) throws ValidationException
	{
		getFacadeValidationService().validate(getSyncJobRequestCompositeValidator(), syncJobRequest);

		final String catalogId = syncJobRequest.getCatalogId();
		final String sourceCatalogVersionName = syncJobRequest.getSourceVersionId();
		final String targetCatalogVersionName = syncJobRequest.getTargetVersionId();

		final Optional<CronJobModel> latestCronJob = findTheCronJob(catalogId, sourceCatalogVersionName, targetCatalogVersionName);

		return getSyncJobConverter().convert(latestCronJob);
	}

	@Override
	public SyncJobData getLastSynchronizationByCatalogTarget(final SyncRequestData syncJobRequest) throws ValidationException
	{
		getFacadeValidationService().validate(getBasicSyncJobRequestCompositeValidator(), syncJobRequest);

		final String catalogId = syncJobRequest.getCatalogId();
		final String targetCatalogVersionName = syncJobRequest.getTargetVersionId();

		final List<SyncItemJobModel> incomingSynchronizations = getIncomingSynchronizations(catalogId, targetCatalogVersionName);
		final Map<PK, CronJobModel> latestCronJobs = getSynchronizationsLastSuccessfulCronJob(incomingSynchronizations);

		final Optional<SyncItemJobModel> latestSyncItemJobModel = findLatestSyncItemJobModelByTarget(incomingSynchronizations, latestCronJobs);
		final Optional<CronJobModel> latestCronJob = latestSyncItemJobModel.map(syncItemJobModel -> latestCronJobs.get(syncItemJobModel.getPk()));

		final SyncJobData result = getSyncJobConverter().convert(latestCronJob);
		getSyncItemJobToSyncJobDataPopulator().populate(latestSyncItemJobModel, result);

		return result;
	}

	/**
	 * Finds the list of synchronizations targeting the provided catalog version.
	 *
	 * @param catalogId
	 *           a catalog id
	 * @param targetCatalogVersionName
	 *           the target catalog version
	 * @return List<SyncItemJobModel> List of {@link SyncItemJobModel}
	 */
	protected List<SyncItemJobModel> getIncomingSynchronizations( final String catalogId, final String targetCatalogVersionName )
	{
		final CatalogVersionModel catalog = getCatalogVersionService().getCatalogVersion(catalogId, targetCatalogVersionName);
		return catalog.getIncomingSynchronizations();
	}

	/**
	 * Retrieves the last successful cron job of each of the provided synchronization items.
	 *
	 * @param synchronizations
	 *           A list of synchronization items
	 * @return Map<PK, CronJobModel> A map. It contains the last successful cron job of each synchronization item. It's indexed by
	 * the PK of the synchronization item.
	 */
	protected Map<PK, CronJobModel> getSynchronizationsLastSuccessfulCronJob( final List<SyncItemJobModel> synchronizations )
	{
		return synchronizations.stream()
				.filter(syncItemJob -> syncItemJob.getActive())
				.filter(syncItemJob -> getLastSuccessfulCronJob(syncItemJob).isPresent())
				.collect(Collectors.toMap(syncItemJob -> syncItemJob.getPk(), syncItemJob -> getLastSuccessfulCronJob(syncItemJob).get()));
	}

	/**
	 * Retrieves the last successful (SUCCESSFUL and FINISHED) cron job of the provided synchronization item.
	 *
	 * @param syncItemJobModel
	 *           A synchronization item
	 * @return Optional<CronJobModel> The last successful cron job.
	 */
	protected Optional<CronJobModel> getLastSuccessfulCronJob( final SyncItemJobModel syncItemJobModel )
	{
		return syncItemJobModel.getCronJobs().stream()
				.sorted((o2, o1) -> o1.getModifiedtime().compareTo(o2.getModifiedtime()))
				.filter(cronJobModel -> cronJobModel.getStatus().equals(CronJobStatus.FINISHED) && cronJobModel.getResult().equals(CronJobResult.SUCCESS)).findFirst();
	}

	/**
	 * Retrieves the synchronization item that triggered the last successful cron job (SUCCESSFUL and FINISHED).
	 *
	 * @param incomingSynchronizations
	 *           A list of synchronization items
	 * @param latestCronJobs
	 * 			A map of the latest synchronization cron jobs. Its indexed by synchronization items PKs.
	 * @return Optional<SyncItemJobModel> The synchronization item
	 */
	protected Optional<SyncItemJobModel> findLatestSyncItemJobModelByTarget( final List<SyncItemJobModel> incomingSynchronizations, final Map<PK, CronJobModel> latestCronJobs )
	{
		return latestCronJobs.entrySet()
				.stream()
				.max(Map.Entry.comparingByValue((o2, o1) -> o1.getModifiedtime().compareTo(o2.getModifiedtime())))
				.map(entry -> incomingSynchronizations.stream().filter(s -> s.getPk().equals(entry.getKey())).findFirst().get());
	}

	@Override
	public SyncJobData createCatalogSynchronization(final SyncRequestData syncJobRequest)
	{
		getFacadeValidationService().validate(getCatalogSynchronizationCompositeValidator(), syncJobRequest);

		final CatalogVersionModel source = getCatalogVersionService().getCatalogVersion(syncJobRequest.getCatalogId(),
				syncJobRequest.getSourceVersionId());
		final CatalogVersionModel target = getCatalogVersionService().getCatalogVersion(syncJobRequest.getCatalogId(),
				syncJobRequest.getTargetVersionId());

		Optional<SyncItemJobModel> latestSyncItemJobModel = findLatestSyncItemJobModel(syncJobRequest.getCatalogId(),
				syncJobRequest.getSourceVersionId(), syncJobRequest.getTargetVersionId());

		if (!latestSyncItemJobModel.isPresent())
		{
			latestSyncItemJobModel = Optional.of(createSyncJobModel(source, target));
		}

		final Optional<CronJobModel> latestCronJob = findTheCronJob(latestSyncItemJobModel);

		final Optional<CronJobStatus> cronJobStatus = getCronJobStatus(latestCronJob);

		validateIfJobIsRunning(cronJobStatus);

		SyncItemCronJobModel newSyncCronJob = null;
		try
		{
			final boolean executeSynchronously = false;
			newSyncCronJob = createSyncCronJob(latestSyncItemJobModel.get());
			getCronJobService().performCronJob(newSyncCronJob, executeSynchronously);
		}
		catch (final Exception e)
		{
			throw new SystemException(e);
		}

		return getSyncJobConverter().convert(Optional.of(newSyncCronJob));
	}

	/**
	 * Check if the a cron job is running
	 *
	 * @param cronJobStatus
	 *           an {@link Optional} of {@link CronJobStatus}
	 */
	protected void validateIfJobIsRunning(final Optional<CronJobStatus> cronJobStatus)
	{
		cronJobStatus.ifPresent(status -> {
			if(CronJobStatus.RUNNING.equals(status))
			{
				throw new SynchronizationInProgressException();
			}
		});
	}

	/**
	 * Create a sync job
	 *
	 * @param source
	 *           the catalog source version
	 * @param target
	 *           the catalog target version.
	 * @return a {@link SyncItemJobModel}
	 */
	protected SyncItemJobModel createSyncJobModel(final CatalogVersionModel source, final CatalogVersionModel target)
	{
		final SyncItemJobModel syncJobModel = getModelService().create(SyncItemJobModel.class);
		syncJobModel.setSourceVersion(source);
		syncJobModel.setTargetVersion(target);
		syncJobModel.setRemoveMissingItems(true);
		syncJobModel.setCreateNewItems(true);
		syncJobModel.setActive(true);
		syncJobModel.setLogLevelFile(JobLogLevel.WARNING);
		syncJobModel.setLogToDatabase(false);
		syncJobModel.setLogToFile(false);
		getModelService().save(syncJobModel);

		return syncJobModel;
	}

	/**
	 * Find the latest SyncItemJobModel
	 *
	 * @param catalogId
	 * @param sourceCatalogVersionName
	 * @param targetTatalogVersionName
	 * @return Optional<CronJobModel>
	 */
	protected Optional<SyncItemJobModel> findLatestSyncItemJobModel(final String catalogId, final String sourceCatalogVersionName,
			final String targetTatalogVersionName)
	{
		final CatalogVersionModel catalog = getCatalogVersionService().getCatalogVersion(catalogId, sourceCatalogVersionName);

		final List<SyncItemJobModel> synchronizations = catalog.getSynchronizations();

		Optional<SyncItemJobModel> syncItemJobModel = Optional.empty();
		if (!synchronizations.isEmpty())
		{
			syncItemJobModel = synchronizations.stream()
					.filter(syncItemJob -> syncItemJob.getTargetVersion().getVersion().equals(targetTatalogVersionName))
					.filter(syncItemJob -> syncItemJob.getActive()).findFirst();
		}
		return syncItemJobModel;
	}

	/**
	 * Find a cron job based a catalog and version.
	 *
	 * @param catalogId
	 *           a catalog id
	 * @param sourceCatalogVersionName
	 *           the source catalog version
	 * @param targetCatalogVersionName
	 *           the target catalog version
	 * @return Optional<CronJobModel> a {@link Optional} of {@link CronJobModel}
	 */
	protected Optional<CronJobModel> findTheCronJob(final String catalogId, final String sourceCatalogVersionName,
			final String targetCatalogVersionName)
	{
		final Optional<SyncItemJobModel> latestSyncItemJobModel = findLatestSyncItemJobModel(catalogId, sourceCatalogVersionName,
				targetCatalogVersionName);

		return findTheCronJob(latestSyncItemJobModel);
	}

	/**
	 * Return the latest cron job of a syncItemJobModel
	 *
	 * @param syncItemJobModel
	 *           an {@link Optional} of {@link SyncItemJobModel}
	 * @return a {@link Optional} of {@link CronJobModel}
	 */
	protected Optional<CronJobModel> findTheCronJob(final Optional<SyncItemJobModel> syncItemJobModel)
	{
		return syncItemJobModel.map(SyncItemJobModel::getCronJobs).orElse(Collections.emptyList()).stream()
				.sorted((o2, o1) -> o1.getModifiedtime().compareTo(o2.getModifiedtime())).findFirst();
	}

	/**
	 * Get a cron job status
	 *
	 * @param cronJobModel
	 *           {@link Optional} of {@link CronJobModel}
	 * @return {@link Optional} of {@link CronJobStatus}
	 */
	protected Optional<CronJobStatus> getCronJobStatus(final Optional<CronJobModel> cronJobModel)
	{
		Optional<CronJobStatus> result = Optional.empty();
		if (cronJobModel.isPresent())
		{
			result = Optional.of(cronJobModel.get().getStatus());
		}
		return result;
	}

	/**
	 * Creates a new {@link SyncItemCronJobModel} for the same {@link SyncItemJobModel}
	 *
	 * @param job
	 *           the SyncItemJobModel that will hold the new instance of the cron Job
	 * @return a new SyncItemCronJobModel
	 */
	protected SyncItemCronJobModel createSyncCronJob(final SyncItemJobModel job)
	{
		final SyncItemJob jobItem = modelService.getSource(job);
		final SyncItemCronJob cronJob = jobItem.newExecution();
		jobItem.configureFullVersionSync(cronJob);

		final SyncItemCronJobModel syncItemCronJobModel = modelService.get(cronJob.getPK());
		modelService.save(syncItemCronJobModel);
		modelService.refresh(syncItemCronJobModel);
		return modelService.get(cronJob.getPK());
	}

	protected CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	@Required
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	protected Converter<Optional<CronJobModel>, SyncJobData> getSyncJobConverter()
	{
		return syncJobConverter;
	}

	@Required
	public void setSyncJobConverter(final Converter<Optional<CronJobModel>, SyncJobData> syncJobConverter)
	{
		this.syncJobConverter = syncJobConverter;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}


	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected Validator getCatalogSynchronizationCompositeValidator()
	{
		return catalogSynchronizationCompositeValidator;
	}

	@Required
	public void setCatalogSynchronizationCompositeValidator(
			final Validator catalogSynchronizationCompositeValidator)
	{
		this.catalogSynchronizationCompositeValidator = catalogSynchronizationCompositeValidator;
	}

	protected Validator getSyncJobRequestCompositeValidator()
	{
		return syncJobRequestCompositeValidator;
	}

	@Required
	public void setSyncJobRequestCompositeValidator(final Validator syncJobRequestCompositeValidator)
	{
		this.syncJobRequestCompositeValidator = syncJobRequestCompositeValidator;
	}

	protected Validator getBasicSyncJobRequestCompositeValidator()
	{
		return basicSyncJobRequestCompositeValidator;
	}

	@Required
	public void setBasicSyncJobRequestCompositeValidator(final Validator basicSyncJobRequestCompositeValidator)
	{
		this.basicSyncJobRequestCompositeValidator = basicSyncJobRequestCompositeValidator;
	}

	protected CronJobService getCronJobService()
	{
		return cronJobService;
	}

	@Required
	public void setCronJobService(final CronJobService cronJobService)
	{
		this.cronJobService = cronJobService;
	}

	protected Populator<Optional<SyncItemJobModel>, SyncJobData> getSyncItemJobToSyncJobDataPopulator()
	{
		return syncItemJobToSyncJobDataPopulator;
	}

	@Required
	public void setSyncItemJobToSyncJobDataPopulator(final Populator<Optional<SyncItemJobModel>, SyncJobData> syncItemJobToSyncJobDataPopulator)
	{
		this.syncItemJobToSyncJobDataPopulator = syncItemJobToSyncJobDataPopulator;
	}
}
