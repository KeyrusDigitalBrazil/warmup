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
package de.hybris.platform.sap.productconfig.runtime.interf.model;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;



/**
 * Represents the configuration model.
 */
public interface ConfigModel extends BaseModel
{

	/**
	 * @return configuration id
	 */
	String getId();

	/**
	 * @param id
	 *           configuration id
	 */
	void setId(String id);

	/**
	 * @return configuration version
	 */
	String getVersion();

	/**
	 * @param version
	 *           configuration version
	 */
	void setVersion(String version);

	/**
	 * @return configuration name
	 */
	String getName();

	/**
	 * @param name
	 *           configuration name
	 */
	void setName(String name);

	/**
	 * @return root instance
	 */
	InstanceModel getRootInstance();

	/**
	 * @param rootInstance
	 *           root instance
	 */
	void setRootInstance(InstanceModel rootInstance);

	/**
	 * @return true if configuration is consistent
	 */
	boolean isConsistent();

	/**
	 * @param isConsistent
	 *           flag indicating whether configuration is cosistent
	 */
	void setConsistent(boolean isConsistent);

	/**
	 * @return true if configuration is complete
	 */
	boolean isComplete();

	/**
	 * @param isComplete
	 *           flag indicating whether configuration is complete
	 */
	void setComplete(boolean isComplete);

	/**
	 * @return configuration base price
	 */
	PriceModel getBasePrice();

	/**
	 * @param basePrice
	 *           configuration base price
	 */
	void setBasePrice(PriceModel basePrice);

	/**
	 * @return price of selected options
	 */
	PriceModel getSelectedOptionsPrice();

	/**
	 * @param selectedOptionsPrice
	 *           price of selected options
	 */
	void setSelectedOptionsPrice(PriceModel selectedOptionsPrice);

	/**
	 * @return configuration current total price
	 */
	PriceModel getCurrentTotalPrice();

	/**
	 * @param currentTotalPrice
	 *           configuration current total price
	 */
	void setCurrentTotalPrice(PriceModel currentTotalPrice);

	/**
	 * @return true if configuration is single-level
	 */
	boolean isSingleLevel();

	/**
	 * @param singleLevel
	 *           flag indicating whether configuration is single-level
	 */
	void setSingleLevel(boolean singleLevel);

	/**
	 *
	 * @param solvableConflicts
	 */
	void setSolvableConflicts(List<SolvableConflictModel> solvableConflicts);

	/**
	 * @return List of solvable conflicts
	 */
	List<SolvableConflictModel> getSolvableConflicts();

	/**
	 * @return List of messages
	 */
	default Set<ProductConfigMessage> getMessages()
	{
		return Collections.emptySet();
	}

	/**
	 * @param messages
	 */
	default void setMessages(final Set<ProductConfigMessage> messages)
	{
		throw new NotImplementedException("setMessages not implemented");
	}

	/**
	 * @param csticValueDeltas
	 */
	void setCsticValueDeltas(List<CsticValueDelta> csticValueDeltas);

	/**
	 * @return list of cstic value deltas
	 */
	List<CsticValueDelta> getCsticValueDeltas();

	/**
	 * @return KB ID of the configuration model
	 */
	default String getKbId()
	{
		return null;
	}

	/**
	 * @param kbId
	 */
	default void setKbId(final String kbId)
	{
		throw new NotImplementedException("setKbId not implemented");
	}

	/**
	 * @param pricingError
	 *           flag indicating whether configuration has had an error retrieving prices
	 */
	void setPricingError(boolean pricingError);

	/**
	 * @return whether the configuration has had an error retrieving prices
	 */
	boolean hasPricingError();

	/**
	 * @param kbKey
	 *           knowledgebase key
	 */
	void setKbKey(KBKey kbKey);

	/**
	 * @return knowledgebase key
	 */
	KBKey getKbKey();


	/**
	 * @return The saving value
	 */
	PriceModel getCurrentTotalSavings();

	/**
	 * @param currentTotalSavings
	 *           the saving value
	 */
	void setCurrentTotalSavings(final PriceModel currentTotalSavings);

}
