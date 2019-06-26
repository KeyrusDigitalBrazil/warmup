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
package de.hybris.platform.accountsummaryaddon.document.criteria.validator.impl;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.accountsummaryaddon.document.criteria.validator.CriteriaValidator;
import de.hybris.platform.accountsummaryaddon.utils.AccountSummaryAddonUtils;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;


/**
 *
 */
public class AmountCriteriaValidator implements CriteriaValidator
{
	private static final Logger LOG = Logger.getLogger(AmountCriteriaValidator.class);

	@Override
	public boolean isValid(final String startRange, final String endRange, final Model model)
	{

		Optional<BigDecimal> parsedStartRange = Optional.empty();
		Optional<BigDecimal> parsedEndRange = Optional.empty();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("validating amount ranges");
		}

		if (StringUtils.isNotBlank(startRange))
		{
			parsedStartRange = AccountSummaryAddonUtils.parseBigDecimal(startRange);
			if (!parsedStartRange.isPresent())
			{
				GlobalMessages.addErrorMessage(model, "text.company.accountsummary.criteria.amount.format.from.invalid");
				return false;
			}
		}

		if (StringUtils.isNotBlank(endRange))
		{
			parsedEndRange = AccountSummaryAddonUtils.parseBigDecimal(endRange);
			if (!parsedEndRange.isPresent())
			{
				GlobalMessages.addErrorMessage(model, "text.company.accountsummary.criteria.amount.format.to.invalid");
				return false;
			}
		}

		if (parsedStartRange.isPresent() && parsedEndRange.isPresent()
				&& parsedStartRange.get().compareTo(parsedEndRange.get()) > 0)
		{
			GlobalMessages.addErrorMessage(model, "text.company.accountsummary.criteria.amount.invalid");
			return false;
		}

		return true;
	}
}