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
package de.hybris.platform.commerceservices.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.externaltax.ExternalTaxesService;
import de.hybris.platform.commerceservices.order.CommerceCartCalculationStrategy;
import de.hybris.platform.commerceservices.order.hook.CommerceCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CalculationService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default strategy to calculate the cart when not in checkout status.
 */
public class DefaultCommerceCartCalculationStrategy implements CommerceCartCalculationStrategy
{
	private CalculationService calculationService;
	private PromotionsService promotionsService;
	private TimeService timeService;
	private BaseSiteService baseSiteService;
	private List<CommerceCartCalculationMethodHook> commerceCartCalculationMethodHooks;
	private ConfigurationService configurationService;
	private ExternalTaxesService externalTaxesService;
	private boolean calculateExternalTaxes = false;

	/**
	 * @deprecated Since 5.2.
	 */
	@Override
	@Deprecated
	public boolean calculateCart(final CartModel cartModel)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		return this.calculateCart(parameter);
	}

	@Override
	public boolean calculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();

		validateParameterNotNull(cartModel, "Cart model cannot be null");

		final CalculationService calcService = getCalculationService();
		boolean recalculated = false;
		if (calcService.requiresCalculation(cartModel))
		{
			try
			{
				parameter.setRecalculate(false);
				beforeCalculate(parameter);
				calcService.calculate(cartModel);
				getPromotionsService().updatePromotions(getPromotionGroups(), cartModel, true, AutoApplyMode.APPLY_ALL,
						AutoApplyMode.APPLY_ALL, getTimeService().getCurrentTime());
			}
			catch (final CalculationException calculationException)
			{
				throw new IllegalStateException("Cart model " + cartModel.getCode() + " was not calculated due to: "
						+ calculationException.getMessage(), calculationException);
			}
			finally
			{
				afterCalculate(parameter);
			}
			recalculated = true;
		}
		if (calculateExternalTaxes)
		{
			getExternalTaxesService().calculateExternalTaxes(cartModel);
		}
		return recalculated;
	}

	/**
	 * @deprecated Since 5.2.
	 */
	@Override
	@Deprecated
	public boolean recalculateCart(final CartModel cartModel)
	{
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		return this.recalculateCart(parameter);
	}

	@Override
	public boolean recalculateCart(final CommerceCartParameter parameter)
	{
		final CartModel cartModel = parameter.getCart();
		try
		{
			parameter.setRecalculate(true);
			beforeCalculate(parameter);
			getCalculationService().recalculate(cartModel);
			getPromotionsService().updatePromotions(getPromotionGroups(), cartModel, true, AutoApplyMode.APPLY_ALL,
					AutoApplyMode.APPLY_ALL, getTimeService().getCurrentTime());
		}
		catch (final CalculationException calculationException)
		{
			throw new IllegalStateException(String.format("Cart model %s was not calculated due to: %s ", cartModel.getCode(),
					calculationException.getMessage()), calculationException);
		}
		finally
		{
			afterCalculate(parameter);

		}
		return true;
	}

	protected void beforeCalculate(final CommerceCartParameter parameter)
	{
		if (getCommerceCartCalculationMethodHooks() != null
				&& (parameter.isEnableHooks() && getConfigurationService().getConfiguration().getBoolean(
						CommerceServicesConstants.CARTCALCULATIONHOOK_ENABLED, true)))
		{
			for (final CommerceCartCalculationMethodHook commerceCartCalculationMethodHook : getCommerceCartCalculationMethodHooks())
			{
				commerceCartCalculationMethodHook.beforeCalculate(parameter);
			}
		}
	}

	protected void afterCalculate(final CommerceCartParameter parameter)
	{
		if (getCommerceCartCalculationMethodHooks() != null
				&& (parameter.isEnableHooks() && getConfigurationService().getConfiguration().getBoolean(
						CommerceServicesConstants.CARTCALCULATIONHOOK_ENABLED, true)))
		{
			for (final CommerceCartCalculationMethodHook commerceCartCalculationMethodHook : getCommerceCartCalculationMethodHooks())
			{
				commerceCartCalculationMethodHook.afterCalculate(parameter);
			}
		}
	}

	protected Collection<PromotionGroupModel> getPromotionGroups()
	{
		final Collection<PromotionGroupModel> promotionGroupModels = new ArrayList<PromotionGroupModel>();
		if (getBaseSiteService().getCurrentBaseSite() != null
				&& getBaseSiteService().getCurrentBaseSite().getDefaultPromotionGroup() != null)
		{
			promotionGroupModels.add(getBaseSiteService().getCurrentBaseSite().getDefaultPromotionGroup());
		}
		return promotionGroupModels;
	}

	protected CalculationService getCalculationService()
	{
		return calculationService;
	}

	@Required
	public void setCalculationService(final CalculationService calculationService)
	{
		this.calculationService = calculationService;
	}


	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
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

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	protected List<CommerceCartCalculationMethodHook> getCommerceCartCalculationMethodHooks()
	{
		return commerceCartCalculationMethodHooks;
	}

	public void setCommerceCartCalculationMethodHooks(
			final List<CommerceCartCalculationMethodHook> commerceCartCalculationMethodHooks)
	{
		this.commerceCartCalculationMethodHooks = commerceCartCalculationMethodHooks;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the externalTaxesService
	 */
	public ExternalTaxesService getExternalTaxesService()
	{
		return externalTaxesService;
	}

	/**
	 * @param externalTaxesService
	 *           the externalTaxesService to set
	 */
	public void setExternalTaxesService(final ExternalTaxesService externalTaxesService)
	{
		this.externalTaxesService = externalTaxesService;
	}

	/**
	 * @return the calculateExternalTaxes
	 */
	public boolean isCalculateExternalTaxes()
	{
		return calculateExternalTaxes;
	}

	/**
	 * @param calculateExternalTaxes
	 *           the calculateExternalTaxes to set
	 */
	public void setCalculateExternalTaxes(final boolean calculateExternalTaxes)
	{
		this.calculateExternalTaxes = calculateExternalTaxes;
	}
}
