/**
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.merchandising.exporter;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.merchandising.dao.MerchSynchronizationConfigDao;
import com.hybris.merchandising.model.MerchSynchronizationConfigModel;
import com.hybris.merchandising.service.MerchCatalogService;
import com.hybris.merchandising.yaas.client.CategoryHierarchyWrapper;
import com.hybris.merchandising.yaas.client.MerchCatalogServiceClient;
import com.hybris.platform.merchandising.yaas.CategoryHierarchy;

import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.impl.InMemoryChangesCollector;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.site.BaseSiteService;

/**
 * DefaultMerchCategoryExporter is a default implementation of {@link MerchCategoryExporter} for the purposes of
 * exporting the categories from a configured catalog / catalog version to Merch v2.
 *
 */
public class DefaultMerchCategoryExporter extends AbstractJobPerformable<CronJobModel> implements MerchCategoryExporter {
	private MerchSynchronizationConfigDao merchSynchronizationConfigDao;
	private MerchCatalogService merchCatalogService;
	private MerchCatalogServiceClient client;
	private TypeService typeService;
	private ChangeDetectionService changeDetectionService;
	private BaseSiteService baseSiteService;

	private static final Logger LOG = LoggerFactory.getLogger(DefaultMerchCategoryExporter.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportCategories() {
		final Collection<MerchSynchronizationConfigModel> syncConfig = merchSynchronizationConfigDao.findAllMerchSynchronizationConfig();
		syncConfig.stream().filter(MerchSynchronizationConfigModel::isEnabled).forEach(config -> {
			LOG.debug("Setting base site to the one from the config: {}", config.getBaseSite().getName());
			baseSiteService.setCurrentBaseSite(config.getBaseSite(), true);
			retrieveAndSendCategories(config);
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exportCategoriesForCurrentBaseSite() {
		final Collection<MerchSynchronizationConfigModel> syncConfig = merchSynchronizationConfigDao.findAllMerchSynchronizationConfig();
		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		LOG.debug("Exporting categories for current base site: {}", currentBaseSite.getName());
		syncConfig.stream().filter(MerchSynchronizationConfigModel::isEnabled)
							.filter(config -> config.getBaseSite().getUid().equals(currentBaseSite.getUid()))
							.forEach(config -> retrieveAndSendCategories(config));
	}

	/**
	 * retrieveAndSendCategories is a method for retrieving the category hierarchy and sending it to the configured downstream service.
	 * @param config {@link MerchSynchronizationConfigModel config}.
	 */
	private void retrieveAndSendCategories(final MerchSynchronizationConfigModel config) {
		final List<CategoryHierarchy> categoryHierarchy = merchCatalogService.getCategories(config.getBaseSite().getUid(), config.getCatalog().getId(), config.getCatalogVersion().getVersion(), config.getBaseCategoryUrl());
		client.handleCategories(new CategoryHierarchyWrapper(categoryHierarchy));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PerformResult perform(final CronJobModel cronJob) {
		final InMemoryChangesCollector collector = new InMemoryChangesCollector();
		return perform(collector);
	}

	/**
	 * perform is a method for actually performing the delta detection functionality.
	 * @param collector the {@link InMemoryChangesCollector} to hold the changed.
	 * @return an instance of {@link PerformResult} representing the result of the delta detection job.
	 */
	protected PerformResult perform(final InMemoryChangesCollector collector) {
		changeDetectionService.collectChangesForType(typeService.getComposedTypeForClass(CategoryModel.class),
				"categoryExportStream", collector);
		final List<ItemChangeDTO> changes = collector.getChanges();
		if(LOG.isDebugEnabled()) {
			changes.forEach(itemChangeDto -> LOG.debug("Changed CategoryModel found - " + itemChangeDto.getItemPK()
					+ " itemChangeDto info=" + itemChangeDto.getInfo()));
		}
		if(!changes.isEmpty()) {
			LOG.debug("Changes is not empty. Exporting category tree");
			exportCategories();
		}
		changeDetectionService.consumeChanges(changes);
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	/**
	 * Retrieves the configured DAO to retrieve the configuration.
	 * @return the configured {@link MerchSynchronizationConfigDao}.
	 */
	protected MerchSynchronizationConfigDao getMerchSynchronizationConfigDao() {
		return merchSynchronizationConfigDao;
	}

	/**
	 * Sets the configured DAO to retrieve the configuration.
	 * @param merchSynchronizationConfigDao 
	 */
	public void setMerchSynchronizationConfigDao(final MerchSynchronizationConfigDao merchSynchronizationConfigDao) {
		this.merchSynchronizationConfigDao = merchSynchronizationConfigDao;
	}

	/**
	 * Gets the configured catalog service.
	 * @return the configured {@link MerchCatalogService}.
	 */
	protected MerchCatalogService getMerchCatalogService() {
		return merchCatalogService;
	}

	/**
	 * Sets the configured catalog service.
	 * @param merchCatalogService the configured {@link MerchCatalogService}.
	 */
	public void setMerchCatalogService(final MerchCatalogService merchCatalogService) {
		this.merchCatalogService = merchCatalogService;
	}

	/**
	 * Gets the configured client for handling the sending of the categories to Merch v2.
	 * @return the configured {@link MerchCatalogServiceClient}.
	 */
	protected MerchCatalogServiceClient getClient() {
		return client;
	}

	/**
	 * Sets the configured client for handling the sending of categories to Merch v2.
	 * @param client the configured {@link MerchCatalogServiceClient} to send categories as.
	 */
	public void setClient(final MerchCatalogServiceClient client) {
		this.client = client;
	}

	/**
	 * Gets the configured {@link TypeService} instance for retrieving composed type for class.
	 * @return the configured {@link TypeService}.
	 */
	protected TypeService getTypeService() {
		return typeService;
	}

	/**
	 * Sets the configured instance of {@link TypeService} for use.
	 * @param typeService the {@link TypeService} to inject.
	 */
	public void setTypeService(final TypeService typeService) {
		this.typeService = typeService;
	}

	/**
	 * Gets the configured instance of {@link ChangeDetectionService} for use with this.
	 * @return configured {@link ChangeDetectionService}.
	 */
	protected ChangeDetectionService getChangeDetectionService() {
		return changeDetectionService;
	}

	/**
	 * Sets the configured instance of {@link ChangeDetectionService}.
	 * @param changeDetectionService - the {@link ChangeDetectionService} to inject.
	 */
	public void setChangeDetectionService(final ChangeDetectionService changeDetectionService) {
		this.changeDetectionService = changeDetectionService;
	}

	/**
	 * Gets the configured instance of {@link BaseSiteService} for use with this.
	 * @return configured {@link BaseSiteService}.
	 */
	protected BaseSiteService getBaseSiteService() {
		return baseSiteService;
	}

	/**
	 * Sets the configured instance of {@link BaseSiteService}.
	 * @param changeDetectionService - the {@link BaseSiteService} to inject.
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService) {
		this.baseSiteService = baseSiteService;
	}
}
