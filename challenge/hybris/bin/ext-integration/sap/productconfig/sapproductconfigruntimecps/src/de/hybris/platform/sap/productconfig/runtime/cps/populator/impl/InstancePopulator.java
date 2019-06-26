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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristicGroup;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSQuantity;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Responsible to populate instances for the configuration runtime. Also orchestrates the conversion of dependent
 * objects (sub instances, groups).<br>
 * <br>
 * This populator breaks our standard pattern that distinguishes between runtime population (accessing static master
 * data in <b>read</b> mode when needed) and populating the master data caches themselves: Here we add the root items'
 * base unit of measure to the master data cache, as we don't get this information from the master data services.
 */
public class InstancePopulator implements ContextualPopulator<CPSItem, InstanceModel, MasterDataContext>
{
	private static final Logger LOG = Logger.getLogger(InstancePopulator.class);

	private ContextualConverter<CPSItem, InstanceModel, MasterDataContext> instanceModelConverter;
	private ContextualConverter<CPSCharacteristicGroup, CsticGroupModel, MasterDataContext> characteristicGroupConverter;
	private ContextualConverter<CPSCharacteristic, CsticModel, MasterDataContext> characteristicConverter;
	private Converter<CPSVariantCondition, VariantConditionModel> variantConditionConverter;


	@Override
	public void populate(final CPSItem source, final InstanceModel target, final MasterDataContext ctxt)
	{
		populateCoreAttributes(source, target);
		populateVariantConditions(source, target);
		populateSubItems(source, target, ctxt);
		populateGroups(source, target, ctxt);
		populateCstics(source, target, ctxt);
		populateRootUOMToMasterDataCache(source, ctxt);
	}

	protected void populateRootUOMToMasterDataCache(final CPSItem source, final MasterDataContext ctxt)
	{
		final CPSConfiguration parentConfiguration = source.getParentConfiguration();
		Preconditions.checkNotNull(parentConfiguration, "CPSConfiguration needs to be present");
		final boolean isRoot = source.getId().equals(parentConfiguration.getRootItem().getId());
		if (isRoot)
		{
			populateUOMToMasterDataCache(source, ctxt);
		}


	}

	protected void populateUOMToMasterDataCache(final CPSItem source, final MasterDataContext ctxt)
	{
		final CPSMasterDataKnowledgeBaseContainer kbContainer = ctxt.getKbCacheContainer();
		Preconditions.checkNotNull(kbContainer, "We were not able to create master data KB container");
		final CPSQuantity quantity = source.getQuantity();
		if (quantity != null)
		{
			final String unit = quantity.getUnit();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Writing to KB cache: " + kbContainer.getHeaderInfo().getId() + ", " + unit);
			}
			kbContainer.setRootUnitOfMeasure(unit);
		}
	}

	protected void populateCoreAttributes(final CPSItem source, final InstanceModel target)
	{
		target.setId(source.getId());
		target.setPosition(source.getBomPosition());
		target.setName(source.getKey());
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
	}


	protected void populateSubItems(final CPSItem source, final InstanceModel target, final MasterDataContext ctxt)
	{
		if (source.getSubItems() != null)
		{
			final List<InstanceModel> subInstances = target.getSubInstances();
			for (final CPSItem subitem : source.getSubItems())
			{
				final InstanceModel subInstance = getInstanceModelConverter().convertWithContext(subitem, ctxt);
				subInstances.add(subInstance);
			}
			target.setSubInstances(subInstances);
		}
	}

	protected void populateGroups(final CPSItem source, final InstanceModel target, final MasterDataContext ctxt)
	{
		for (final CPSCharacteristicGroup characteristicGroup : source.getCharacteristicGroups())
		{
			final String groupId = characteristicGroup.getId();

			// ignore characteristic groups with non-existing id
			if (groupId == null)
			{
				return;
			}

			final CsticGroupModel characteristicGroupModel = characteristicGroupConverter.convertWithContext(characteristicGroup,
					ctxt);

			final List<CsticGroupModel> csticGroupModels = target.getCsticGroups();
			if (groupId.equalsIgnoreCase(SapproductconfigruntimecpsConstants.CPS_GENERAL_GROUP_ID))
			{
				csticGroupModels.add(0, characteristicGroupModel);
			}
			else
			{
				csticGroupModels.add(characteristicGroupModel);
			}
			target.setCsticGroups(csticGroupModels);
		}
	}

	protected void populateCstics(final CPSItem source, final InstanceModel target, final MasterDataContext ctxt)
	{
		if (source.getCharacteristics() != null)
		{
			for (final CPSCharacteristic characteristic : source.getCharacteristics())
			{
				final CsticModel characteristicModel = getCharacteristicConverter().convertWithContext(characteristic, ctxt);
				target.addCstic(characteristicModel);
			}
		}
	}

	protected void populateVariantConditions(final CPSItem source, final InstanceModel target)
	{
		final List<VariantConditionModel> targetVariantConditions = new ArrayList<>();
		for (final CPSVariantCondition variantCondition : source.getVariantConditions())
		{
			final VariantConditionModel variantConditionModel = getVariantConditionConverter().convert(variantCondition);
			targetVariantConditions.add(variantConditionModel);
		}
		target.setVariantConditions(targetVariantConditions);
	}

	/**
	 * @return the variantConditionConverter
	 */
	protected Converter<CPSVariantCondition, VariantConditionModel> getVariantConditionConverter()
	{
		return variantConditionConverter;
	}

	/**
	 * @param variantConditionConverter
	 *           the variantConditionConverter to set
	 */
	public void setVariantConditionConverter(final Converter<CPSVariantCondition, VariantConditionModel> variantConditionConverter)
	{
		this.variantConditionConverter = variantConditionConverter;
	}

	/**
	 * @return the characteristicConverter
	 */
	protected ContextualConverter<CPSCharacteristic, CsticModel, MasterDataContext> getCharacteristicConverter()
	{
		return characteristicConverter;
	}

	/**
	 * @param characteristicConverter
	 *           the characteristicConverter to set
	 */
	public void setCharacteristicConverter(
			final ContextualConverter<CPSCharacteristic, CsticModel, MasterDataContext> characteristicConverter)
	{
		this.characteristicConverter = characteristicConverter;
	}

	/**
	 * @return the characteristicGroupConverter
	 */
	protected Converter<CPSCharacteristicGroup, CsticGroupModel> getCharacteristicGroupConverter()
	{
		return characteristicGroupConverter;
	}

	/**
	 * @param characteristicGroupConverter
	 *           the characteristicGroupConverter to set
	 */
	public void setCharacteristicGroupConverter(
			final ContextualConverter<CPSCharacteristicGroup, CsticGroupModel, MasterDataContext> characteristicGroupConverter)
	{
		this.characteristicGroupConverter = characteristicGroupConverter;
	}

	/**
	 * @return the instanceModelConverter
	 */
	protected ContextualConverter<CPSItem, InstanceModel, MasterDataContext> getInstanceModelConverter()
	{
		return instanceModelConverter;
	}

	/**
	 * @param instanceModelConverter
	 *           the instanceModelConverter to set
	 */
	public void setInstanceModelConverter(
			final ContextualConverter<CPSItem, InstanceModel, MasterDataContext> instanceModelConverter)
	{
		this.instanceModelConverter = instanceModelConverter;
	}
}

