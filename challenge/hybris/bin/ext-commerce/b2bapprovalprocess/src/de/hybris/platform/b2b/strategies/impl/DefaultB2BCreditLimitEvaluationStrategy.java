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
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.platform.b2b.dao.B2BOrderDao;
import de.hybris.platform.b2b.enums.B2BRateType;
import de.hybris.platform.b2b.enums.MerchantCheckStatus;
import de.hybris.platform.b2b.enums.MerchantCheckStatusEmail;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCreditCheckResultModel;
import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BMerchantCheckResultModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BCurrencyConversionService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.EvaluateStrategy;
import de.hybris.platform.b2b.util.B2BDateUtils;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.StandardDateRange;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultB2BCreditLimitEvaluationStrategy extends AbstractEvaluationStrategy<B2BCreditLimitModel> implements
		EvaluateStrategy<Set<B2BMerchantCheckResultModel>, AbstractOrderModel, B2BCustomerModel>
{
	private final MathContext MONEY_HALF_UP = new MathContext(16, RoundingMode.HALF_UP);
	private final BigDecimal ZERO = (new BigDecimal("0", MONEY_HALF_UP)).setScale(2);


	private static final Logger LOG = Logger.getLogger(DefaultB2BCreditLimitEvaluationStrategy.class);
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private B2BCurrencyConversionService b2bCurrencyConversionService;
	private B2BOrderDao b2bOrderDao;
	private B2BCostCenterService b2bCostCenterService;
	private B2BDateUtils b2bDateUtils;

	@Override
	public Set<B2BMerchantCheckResultModel> evaluate(final AbstractOrderModel order, final B2BCustomerModel customer)
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Evaluating B2BCreditLimitResultModel for employee: " + customer.getUid());
		}
		final Set<B2BMerchantCheckResultModel> merchantCheckResults = new HashSet<B2BMerchantCheckResultModel>();

		final Set<B2BUnitModel> unitsWithCreditLimit = new HashSet();
		//Split the order by Cost Centers
		final Set<B2BCostCenterModel> b2bCostCenters = getB2bCostCenterService().getB2BCostCenters(order.getEntries());
		for (final B2BCostCenterModel b2bCostCenterModel : b2bCostCenters)
		{
			//If Unit is not null, this means we have a Parent unit with credit limit.
			final B2BUnitModel unitWithCreditLimit = getB2bUnitService().getUnitWithCreditLimit(b2bCostCenterModel.getUnit());
			if (unitWithCreditLimit != null)
			{
				unitsWithCreditLimit.add(unitWithCreditLimit);
			}
		}

		for (final B2BUnitModel b2bUnitWithCreditLimit : unitsWithCreditLimit)
		{

			final List<B2BCostCenterModel> costCenters = this.getB2bCostCenterService().getCostCentersForUnitBranch(
					b2bUnitWithCreditLimit, order.getCurrency());

			final B2BCreditLimitModel creditLimit = b2bUnitWithCreditLimit.getCreditLimit();


			final B2BCreditCheckResultModel creditLimitResult = this.getModelService().create(B2BCreditCheckResultModel.class);
			creditLimitResult.setStatus(MerchantCheckStatus.OPEN);

			final List<AbstractOrderEntryModel> entries = order.getEntries();


			final StandardDateRange creditLimitDateRange = this.getDateRangeForCreditLimit(b2bUnitWithCreditLimit.getCreditLimit());
			BigDecimal totalCost = BigDecimal.valueOf(this.getTotalCost(costCenters, creditLimitDateRange).doubleValue());


			for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
			{
				if (costCenters.contains(abstractOrderEntryModel.getCostCenter()))
				{
					totalCost = totalCost.add(getOrderEntryTotal(abstractOrderEntryModel));
				}
			}

			final BigDecimal convertedCreditAmount = BigDecimal.valueOf(getB2bCurrencyConversionService().convertAmount(
					Double.valueOf(creditLimit.getAmount().doubleValue()), creditLimit.getCurrency(), order.getCurrency())
					.doubleValue());

			if (convertedCreditAmount.compareTo(totalCost) > 0)
			{
				creditLimitResult.setStatus(MerchantCheckStatus.APPROVED);

				if (shouldCreditLimitTriggerAlert(totalCost, creditLimit, creditLimitDateRange))
				{
					creditLimitResult.setStatusEmail(MerchantCheckStatusEmail.ALERT);
				}
			}
			else
			{
				creditLimitResult.setStatus(MerchantCheckStatus.REJECTED);
			}
			creditLimitResult.setCreditLimit(creditLimit.getAmount());
			creditLimitResult.setAmountUtilised(creditLimit.getAmount().subtract(totalCost));
			creditLimitResult.setCurrency(creditLimit.getCurrency());
			merchantCheckResults.add(creditLimitResult);

			if (LOG.isDebugEnabled())
			{
				LOG.debug(ReflectionToStringBuilder.toString(creditLimitResult, ToStringStyle.DEFAULT_STYLE));
			}
		}

		return merchantCheckResults;
	}

	protected B2BCreditLimitModel getCreditLimitForCostCenter(final B2BCostCenterModel costCenter, final CurrencyModel currency)
	{
		final B2BUnitModel unit = getB2bUnitService().getUnitWithCreditLimit(costCenter.getUnit());
		return unit.getCreditLimit();
	}

	protected List<B2BCostCenterModel> getCostCentersForUnitWithCreditLimit(final B2BUnitModel unit, final CurrencyModel currency)
	{
		return getB2bCostCenterService().getCostCentersForUnitBranch(unit, currency);
	}


	protected BigDecimal getTotalOfEntriesWithCostCenter(final B2BCostCenterModel costCenter,
			final List<AbstractOrderEntryModel> entries)
	{
		BigDecimal total = BigDecimal.ZERO;
		for (final AbstractOrderEntryModel abstractOrderEntryModel : entries)
		{
			if (abstractOrderEntryModel.getCostCenter().equals(costCenter))
			{
				total = total.add(getOrderEntryTotal(abstractOrderEntryModel));
			}
		}
		return total;
	}


	protected BigDecimal getOrderEntryTotal(final AbstractOrderEntryModel entry)
	{
		return this.toMoney(new Double(entry.getTotalPrice().doubleValue()
				+ (entry.getOrder().getNet().booleanValue() ? getTotalTax(entry).doubleValue() : 0)));
	}

	protected BigDecimal getTotalTax(final AbstractOrderEntryModel orderEntry)
	{
		BigDecimal totalTax = this.ZERO;
		for (final TaxValue taxValue : orderEntry.getTaxValues())
		{
			totalTax = totalTax.add(BigDecimal.valueOf(taxValue.getAppliedValue()), this.MONEY_HALF_UP);
		}
		return totalTax;
	}


	protected Double getTotalCost(final List<B2BCostCenterModel> costCenters, final StandardDateRange standardDateRange)
	{
		double totalCost = 0;
		for (final B2BCostCenterModel costCenter : costCenters)
		{
			totalCost = totalCost + this.getB2bCostCenterService().getTotalCost(costCenter, //
					standardDateRange.getStart(), standardDateRange.getEnd()).doubleValue();
		}
		return Double.valueOf(totalCost);
	}



	@Override
	public Set<B2BCreditLimitModel> getTypesToEvaluate(final B2BCustomerModel customer, final AbstractOrderModel order)
	{

		final B2BUnitModel unitWithCreditLimit = getB2bUnitService().getUnitWithCreditLimit(order.getUnit());
		return Collections.singleton(unitWithCreditLimit.getCreditLimit());
	}

	/**
	 * To determine if an alert is to be sent to the B2BCustomer based on exceeding a limit and alertSentDate and update
	 * alertSentDate
	 *
	 * @param orderTotals
	 *           the total amount of all orders
	 * @param creditLimit
	 *           the limit type (currency or percentage) which also provides the limit amount
	 * @return true if the credit limit has been exceeded
	 */
	protected boolean shouldCreditLimitTriggerAlert(final BigDecimal orderTotals, final B2BCreditLimitModel creditLimit,
			final StandardDateRange creditLimitDateRange)
	{
		boolean sendAlert = false;
		final boolean alertSent = alertSent(creditLimit, creditLimitDateRange);

		if (!alertSent)
		{
			if (B2BRateType.CURRENCY.equals(creditLimit.getAlertRateType()))
			{
				if (creditLimit.getAlertThreshold().compareTo(orderTotals) <= 0)
				{
					sendAlert = true;
				}
			}
			else if (B2BRateType.PERCENTAGE.equals(creditLimit.getAlertRateType()))
			{
				final BigDecimal thresholdValue = creditLimit.getAmount().multiply(creditLimit.getAlertThreshold())
						.divide(BigDecimal.valueOf(100), MathContext.DECIMAL128);

				if (thresholdValue.compareTo(orderTotals) <= 0)
				{
					sendAlert = true;
				}
			}

			if (sendAlert)
			{
				creditLimit.setAlertSentDate(new Date());
			}
			else
			{
				creditLimit.setAlertSentDate(null);
				LOG.info(String.format("CreditLimit alert not triggered! Alert Type: %s Alert threshold is: %s, order total is: %s, credit limit amount %s",
						creditLimit.getAlertRateType(), creditLimit.getAlertThreshold(), orderTotals, creditLimit.getAmount()));
			}

			getModelService().save(creditLimit);

		}
		return sendAlert;
	}

	/**
	 * To determine if credit limit alert is already sent for current date range period
	 *
	 * @param creditLimit
	 *           Current credit limit to work on
	 * @param creditLimitDateRange
	 *           Date range for current credit limit
	 * @return true if the credit limit alert check should be avoided
	 */
	protected boolean alertSent(final B2BCreditLimitModel creditLimit, final StandardDateRange creditLimitDateRange)
	{
		boolean alertSent = false;

		if (creditLimit.getAlertSentDate() != null && creditLimitDateRange.getStart().before(creditLimit.getAlertSentDate()))
		{
			alertSent = true;
		}
		return alertSent;
	}

	protected StandardDateRange getDateRangeForCreditLimit(final B2BCreditLimitModel creditLimit)
	{

		if (creditLimit.getDateRange() != null)
		{
			return getB2bDateUtils().createDateRange(creditLimit.getDateRange());
		}
		else
		{
			return creditLimit.getDatePeriod();
		}
	}


	protected BigDecimal toMoney(final Double orderTotal)
	{
		final BigDecimal totalForOrders = BigDecimal.valueOf(orderTotal.doubleValue())
				.round(new MathContext(16, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
		return totalForOrders;
	}

	@Required
	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}


	@Required
	public void setB2bCurrencyConversionService(final B2BCurrencyConversionService b2bCurrencyConversionService)
	{
		this.b2bCurrencyConversionService = b2bCurrencyConversionService;
	}

	protected B2BCurrencyConversionService getB2bCurrencyConversionService()
	{
		return b2bCurrencyConversionService;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	protected B2BOrderDao getB2bOrderDao()
	{
		return b2bOrderDao;
	}

	@Required
	public void setB2bOrderDao(final B2BOrderDao b2bOrderDao)
	{
		this.b2bOrderDao = b2bOrderDao;
	}


	public B2BCostCenterService getB2bCostCenterService()
	{
		return b2bCostCenterService;
	}

	@Required
	public void setB2bCostCenterService(final B2BCostCenterService b2bCostCenterService)
	{
		this.b2bCostCenterService = b2bCostCenterService;
	}

	protected B2BDateUtils getB2bDateUtils()
	{
		return b2bDateUtils;
	}

	@Required
	public void setB2bDateUtils(final B2BDateUtils b2bDateUtils)
	{
		this.b2bDateUtils = b2bDateUtils;
	}
}
