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
package de.hybris.platform.b2bapprovalprocessfacades.company.converters.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.process.approval.services.B2BApprovalProcessService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populates {@link B2BUnitModel} to {@link B2BUnitData}.
 */
public class B2BUnitApprovalPopulator implements Populator<B2BUnitModel, B2BUnitData>
{
	private B2BApprovalProcessService b2BApprovalProcessService;
	private UserService userService;
	private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

	@Override
	public void populate(final B2BUnitModel source, final B2BUnitData target)
	{
		validateParameterNotNull(source, "Parameter source cannot be null.");
		validateParameterNotNull(target, "Parameter target cannot be null.");

		target.setApprovalProcessCode(source.getApprovalProcessCode());

		final Map<String, String> businessProcesses = getB2BApprovalProcessService().getProcesses(null);
		target.setApprovalProcessName((businessProcesses == null) ? StringUtils.EMPTY : businessProcesses.get(source
				.getApprovalProcessCode()));

		populateApprovers(source, target);
	}

	protected void populateApprovers(final B2BUnitModel source, final B2BUnitData target)
	{
		if (CollectionUtils.isNotEmpty(source.getApprovers()))
		{
			final UserGroupModel approverGroup = getUserService().getUserGroupForUID(B2BConstants.B2BAPPROVERGROUP);
			target.setApprovers(Converters.convertAll(CollectionUtils.select(source.getApprovers(), new Predicate()
			{
				@Override
				public boolean evaluate(final Object object)
				{
					final B2BCustomerModel b2bCustomerModel = (B2BCustomerModel) object;
					return getUserService().isMemberOfGroup(b2bCustomerModel, approverGroup);
				}
			}), getB2BCustomerConverter()));
		}
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected Converter<B2BCustomerModel, CustomerData> getB2BCustomerConverter()
	{
		return b2BCustomerConverter;
	}

	@Required
	public void setB2BCustomerConverter(final Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter)
	{
		this.b2BCustomerConverter = b2BCustomerConverter;
	}

	protected B2BApprovalProcessService getB2BApprovalProcessService()
	{
		return b2BApprovalProcessService;
	}

	@Required
	public void setB2BApprovalProcessService(final B2BApprovalProcessService b2bApprovalProcessService)
	{
		b2BApprovalProcessService = b2bApprovalProcessService;
	}
}
