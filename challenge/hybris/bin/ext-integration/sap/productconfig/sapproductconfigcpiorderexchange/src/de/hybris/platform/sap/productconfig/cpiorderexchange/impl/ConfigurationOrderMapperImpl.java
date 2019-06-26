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
package de.hybris.platform.sap.productconfig.cpiorderexchange.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.productconfig.cpiorderexchange.ConfigurationOrderEntryMapper;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigConditionModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHeaderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigHierarchyModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigInstanceModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemConfigValueModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderMapperService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


public class ConfigurationOrderMapperImpl implements SapCpiOrderMapperService<OrderModel, SAPCpiOutboundOrderModel>
{
	private ConfigurationOrderEntryMapper orderEntryMapper;


	@Override
	public void map(final OrderModel source, final SAPCpiOutboundOrderModel target)
	{
		final List<AbstractOrderEntryModel> orderEntries = source.getEntries();

		if (isConfigurationMappingNeeded(orderEntries, target))
		{
			int entryNumberShifted = source.getEntries().get(0).getEntryNumber().intValue();
			final Map<String, String> entryNumberMapping = new HashMap();

			for (final AbstractOrderEntryModel entry : orderEntries)
			{
				final SAPCpiOutboundOrderItemModel outboundItem = findOutboundItem(target, entry);
				entryNumberMapping.put(outboundItem.getEntryNumber(), String.valueOf(entryNumberShifted));

				if (getOrderEntryMapper().isMapperApplicable(entry, outboundItem))
				{
					initProductConfigSets(target);
					final int numberOfInstances = getOrderEntryMapper().mapConfiguration(entry, target,
							String.valueOf(entryNumberShifted));
					entryNumberShifted += numberOfInstances;
				}
				else
				{
					entryNumberShifted++;
				}
			}
			target.getSapCpiOutboundOrderItems().stream()
					.forEach(item -> item.setEntryNumber(entryNumberMapping.get(item.getEntryNumber())));
		}
	}

	protected boolean isConfigurationMappingNeeded(final List<AbstractOrderEntryModel> orderEntries,
			final SAPCpiOutboundOrderModel target)
	{
		return orderEntries.stream()
				.filter(entry -> getOrderEntryMapper().isMapperApplicable(entry, findOutboundItem(target, entry))).findFirst()
				.isPresent();
	}

	protected void initProductConfigSets(final SAPCpiOutboundOrderModel target)
	{
		if (target.getProductConfigHeaders() == null)
		{
			target.setProductConfigHeaders(new HashSet<SAPCpiOutboundOrderItemConfigHeaderModel>());
			target.setProductConfigInstances(new HashSet<SAPCpiOutboundOrderItemConfigInstanceModel>());
			target.setProductConfigHierarchies(new HashSet<SAPCpiOutboundOrderItemConfigHierarchyModel>());
			target.setProductConfigValues(new HashSet<SAPCpiOutboundOrderItemConfigValueModel>());
			target.setProductConfigConditions(new HashSet<SAPCpiOutboundOrderItemConfigConditionModel>());
		}
	}

	protected SAPCpiOutboundOrderItemModel findOutboundItem(final SAPCpiOutboundOrderModel target,
			final AbstractOrderEntryModel entry)
	{
		return target.getSapCpiOutboundOrderItems().stream()
				.filter(e -> e.getEntryNumber().equals(entry.getEntryNumber().toString())).findFirst().orElse(null);
	}

	protected ConfigurationOrderEntryMapper getOrderEntryMapper()
	{
		return orderEntryMapper;
	}

	@Required
	public void setOrderEntryMapper(final ConfigurationOrderEntryMapper orderEntryMapper)
	{
		this.orderEntryMapper = orderEntryMapper;
	}
}
