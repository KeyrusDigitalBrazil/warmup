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
package de.hybris.platform.payment.commands.impl;

import de.hybris.platform.payment.commands.IsApplicableCommand;
import de.hybris.platform.payment.commands.request.IsApplicableCommandReqest;
import de.hybris.platform.payment.commands.result.IsApplicableCommandResult;


/**
 * Implementation for {@link IsApplicableCommand}
 */
public class IsApplicableMockCommand implements IsApplicableCommand
{
	@Override
	public IsApplicableCommandResult perform(final IsApplicableCommandReqest request)
	{
		return new IsApplicableCommandResult(true);
	}
}
