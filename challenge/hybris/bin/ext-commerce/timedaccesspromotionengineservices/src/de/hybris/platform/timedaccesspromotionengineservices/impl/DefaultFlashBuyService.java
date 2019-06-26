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
package de.hybris.platform.timedaccesspromotionengineservices.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.promotionengineservices.dao.PromotionDao;
import de.hybris.platform.promotionengineservices.model.ProductForPromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.util.RuleMappings;
import de.hybris.platform.ruleengineservices.maintenance.RuleMaintenanceService;
import de.hybris.platform.servicelayer.cronjob.CronJobDao;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.JobDao;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.timedaccesspromotionengineservices.FlashBuyService;
import de.hybris.platform.timedaccesspromotionengineservices.daos.FlashBuyDao;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCouponModel;
import de.hybris.platform.timedaccesspromotionengineservices.model.FlashBuyCronJobModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link FlashBuyService}
 */
public class DefaultFlashBuyService implements FlashBuyService
{

	private static final String PROMOTION_SOURCE_RULE_MUST_NOT_BE_NULL = "Promotion source rule must not be null";
	private static final String SET_MAX_ORDER_QUANTITY_JOB_CODE = "setMaxOrderQuantityJob";
	private static final String RESET_MAX_ORDER_QUANTITY_JOB_CODE = "resetMaxOrderQuantityJob";
	private FlashBuyDao flashBuyDao;
	private RuleMaintenanceService ruleMaintenanceService;
	private PromotionDao promotionDao;
	private JobDao jobDao;
	private CronJobDao cronJobDao;
	private ModelService modelService;
	private CronJobService cronJobService;
	private ProductDao productDao;

	@Override
	public Optional<ProductModel> getProductForPromotion(final AbstractPromotionModel promotion)
	{
		validateParameterNotNull(promotion, "Parameter promotion must not be null");
		return getFlashBuyDao().findProductByPromotion(promotion);
	}

	@Override
	public AbstractPromotionModel getPromotionByCode(final String promotionCode)
	{
		validateParameterNotNull(promotionCode, "Parameter promotionCode must not be null");
		return getPromotionDao().findPromotionByCode(promotionCode);
	}

	@Override
	public Optional<FlashBuyCouponModel> getFlashBuyCouponByPromotionCode(final String code)
	{
		validateParameterNotNull(code, "Promotion code must not be null");
		return getFlashBuyDao().findFlashBuyCouponByPromotionCode(code);
	}

	@Override
	public List<PromotionSourceRuleModel> getPromotionSourceRulesByProductCode(final String productCode)
	{
		validateParameterNotNull(productCode, "Product code must not be null");
		return getFlashBuyDao().findPromotionSourceRuleByProduct(productCode);
	}

	@Override
	public void undeployFlashBuyPromotion(final PromotionSourceRuleModel promotionSourceRule)
	{
		validateParameterNotNull(promotionSourceRule, PROMOTION_SOURCE_RULE_MUST_NOT_BE_NULL);

		promotionSourceRule.getEngineRules().stream().forEach(engineRule -> {
			final String moduleName = getModuleName((DroolsRuleModel) engineRule);
			getRuleMaintenanceService().undeployRules(Arrays.asList(promotionSourceRule), moduleName);
		});
	}

	@Override
	public void createCronJobForFlashBuyCoupon(final FlashBuyCouponModel coupon)
	{
		validateParameterNotNull(coupon, "Parameter coupon must not be null");
		deleteCronJobAndTrigger(coupon);
		if (!coupon.getActive() || coupon.getRule() == null)
		{
			return;
		}
		final List<ProductForPromotionSourceRuleModel> productForPromotionSourceRules = getProductForPromotionSourceRule(coupon
				.getRule());
		if (CollectionUtils.isNotEmpty(productForPromotionSourceRules))
		{
			final ServicelayerJobModel setMaxQtyJob = getFlashBuyJob(SET_MAX_ORDER_QUANTITY_JOB_CODE);
			final FlashBuyCronJobModel setMaxQtyCronJob = createFlashBuyCronJob(coupon, setMaxQtyJob);
			if (coupon.getStartDate() == null || coupon.getStartDate().before(Calendar.getInstance().getTime()))
			{
				getCronJobService().performCronJob(setMaxQtyCronJob, true);
			}
			else
			{
				createSetMaxQtyJobTrigger(coupon, setMaxQtyCronJob);
			}

			final ServicelayerJobModel resetMaxQtyJob = getFlashBuyJob(RESET_MAX_ORDER_QUANTITY_JOB_CODE);
			final FlashBuyCronJobModel resetMaxQtyCronJob = createFlashBuyCronJob(coupon, resetMaxQtyJob);
			if (coupon.getEndDate() != null)
			{
				createResetMaxQtyJobTrigger(coupon, resetMaxQtyCronJob);
			}
		}
	}

	@Override
	public void performFlashBuyCronJob(final FlashBuyCouponModel coupon)
	{
		validateParameterNotNull(coupon, "Parameter coupon must not be null");
		final String cronJobCode = coupon.getCouponId() + RESET_MAX_ORDER_QUANTITY_JOB_CODE;
		final FlashBuyCronJobModel cronJob = (FlashBuyCronJobModel) getCronJobService().getCronJob(
				cronJobCode);
		if (cronJob != null)
		{
			getCronJobService().performCronJob(cronJob, true);
		}
	}

	@Override
	public void deleteCronJobAndTrigger(final FlashBuyCouponModel coupon)
	{
		final List<CronJobModel> setQuantityCronJobs = getCronJobDao().findCronJobs(
				coupon.getCouponId() + SET_MAX_ORDER_QUANTITY_JOB_CODE);
		final List<CronJobModel> resetQuantityCronJobs = getCronJobDao().findCronJobs(
				coupon.getCouponId() + RESET_MAX_ORDER_QUANTITY_JOB_CODE);

		if (CollectionUtils.isNotEmpty(setQuantityCronJobs))
		{
			setQuantityCronJobs.forEach(cronJob -> {
				final List<TriggerModel> triggerModels = cronJob.getTriggers();
				if (CollectionUtils.isNotEmpty(triggerModels))
				{
					getModelService().removeAll(triggerModels);
				}
			});
			getModelService().removeAll(setQuantityCronJobs);
		}

		if (CollectionUtils.isNotEmpty(resetQuantityCronJobs))
		{
			resetQuantityCronJobs.forEach(cronJob -> {
				final List<TriggerModel> triggerModels = cronJob.getTriggers();
				if (CollectionUtils.isNotEmpty(triggerModels))
				{
					getModelService().removeAll(triggerModels);
				}
			});
			getModelService().removeAll(resetQuantityCronJobs);
		}
	}

	protected void createSetMaxQtyJobTrigger(final FlashBuyCouponModel coupon, final FlashBuyCronJobModel cronJob)
	{
		final TriggerModel triggerModel = new TriggerModel();
		final Date endDate = coupon.getStartDate();

		DateTime date = new DateTime(endDate);
		date = date.minusSeconds(5);
		final String cronExpress = String.format("%d %d %d %d %d ? %d", date.getSecondOfMinute(), date.getMinuteOfHour(),
				date.getHourOfDay(), date.getDayOfMonth(), date.getMonthOfYear(), date.getYear());
		triggerModel.setActive(Boolean.TRUE);
		triggerModel.setCronJob(cronJob);
		triggerModel.setCronExpression(cronExpress);
		getModelService().save(triggerModel);
	}

	protected void createResetMaxQtyJobTrigger(final FlashBuyCouponModel coupon, final FlashBuyCronJobModel cronJob)
	{
		final TriggerModel triggerModel = new TriggerModel();
		final Date endDate = coupon.getEndDate();
		final DateTime date = new DateTime(endDate);
		final String cronExpress = String.format("%d %d %d %d %d ? %d", date.getSecondOfMinute(), date.getMinuteOfHour(),
				date.getHourOfDay(), date.getDayOfMonth(), date.getMonthOfYear(), date.getYear());
		triggerModel.setActive(Boolean.TRUE);
		triggerModel.setCronJob(cronJob);
		triggerModel.setCronExpression(cronExpress);
		getModelService().save(triggerModel);
	}

	protected FlashBuyCronJobModel createFlashBuyCronJob(final FlashBuyCouponModel coupon, final ServicelayerJobModel job)
	{
		final String jobCode = coupon.getCouponId() + job.getCode();
		final FlashBuyCronJobModel cronJob = getModelService().create(FlashBuyCronJobModel.class);
		cronJob.setFlashBuyCoupon(coupon);
		cronJob.setCode(jobCode);
		cronJob.setRemoveOnExit(Boolean.TRUE);
		cronJob.setJob(job);
		getModelService().save(cronJob);
		return cronJob;
	}

	@Override
	public List<ProductForPromotionSourceRuleModel> getProductForPromotionSourceRule(final PromotionSourceRuleModel sourceRule)
	{
		validateParameterNotNull(sourceRule, PROMOTION_SOURCE_RULE_MUST_NOT_BE_NULL);
		return getFlashBuyDao().findProductForPromotionSourceRules(sourceRule);
	}

	@Override
	public List<ProductModel> getAllProductsByPromotionSourceRule(final PromotionSourceRuleModel rule)
	{
		validateParameterNotNull(rule, PROMOTION_SOURCE_RULE_MUST_NOT_BE_NULL);
		return getFlashBuyDao().findAllProductsByPromotionSourceRule(rule);
	}

	@Override
	public List<FlashBuyCouponModel> getFlashBuyCouponByProduct(final ProductModel product)
	{
		validateParameterNotNull(product, "Product source rule must not be null");
		return getFlashBuyDao().findFlashBuyCouponByProduct(product);
	}

	protected ServicelayerJobModel getFlashBuyJob(final String jobCode)
	{
		ServicelayerJobModel job = null;
		final List<JobModel> jobs = getJobDao().findJobs(jobCode);
		if (CollectionUtils.isEmpty(jobs))
		{
			job = new ServicelayerJobModel();
			job.setSpringId(jobCode);
			job.setCode(jobCode);
			getModelService().save(job);
		}
		else
		{
			job = (ServicelayerJobModel) jobs.get(0);
		}
		return job;
	}

	@Override
	public List<ProductModel> getProductForCode(final String productCode)
	{
		validateParameterNotNull(productCode, "Parameter productCode must not be null");
		final List<ProductModel> productList = getProductDao().findProductsByCode(productCode);
		if (CollectionUtils.isNotEmpty(productList))
		{
			return productList;
		}
		else
		{
			return new ArrayList<>();
		}

	}

	protected <T extends DroolsRuleModel> String getModuleName(final T rule)
	{
		return RuleMappings.moduleName((DroolsRuleModel) rule);
	}

	protected RuleMaintenanceService getRuleMaintenanceService()
	{
		return ruleMaintenanceService;
	}

	@Required
	public void setRuleMaintenanceService(final RuleMaintenanceService ruleMaintenanceService)
	{
		this.ruleMaintenanceService = ruleMaintenanceService;
	}


	protected FlashBuyDao getFlashBuyDao()
	{
		return flashBuyDao;
	}

	@Required
	public void setFlashBuyDao(final FlashBuyDao flashBuyDao)
	{
		this.flashBuyDao = flashBuyDao;
	}


	protected PromotionDao getPromotionDao()
	{
		return promotionDao;
	}

	@Required
	public void setPromotionDao(final PromotionDao promotionDao)
	{
		this.promotionDao = promotionDao;
	}

	protected JobDao getJobDao()
	{
		return jobDao;
	}

	@Required
	public void setJobDao(final JobDao jobDao)
	{
		this.jobDao = jobDao;
	}

	protected CronJobDao getCronJobDao()
	{
		return cronJobDao;
	}

	@Required
	public void setCronJobDao(final CronJobDao cronJobDao)
	{
		this.cronJobDao = cronJobDao;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected ProductDao getProductDao()
	{
		return productDao;
	}

	@Required
	public void setProductDao(final ProductDao productDao)
	{
		this.productDao = productDao;
	}

}
