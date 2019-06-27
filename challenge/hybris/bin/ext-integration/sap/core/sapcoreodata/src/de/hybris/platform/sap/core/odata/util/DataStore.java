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
package de.hybris.platform.sap.core.odata.util;

import java.util.List;
import java.util.Map;


/**
 * This class is used for callback purpose. To file to inline data such as scenarios, basketObjects and leadingObjects
 * @deprecated Since 6.4, replace with extension sapymktcommon
 */
@Deprecated
public class DataStore
{
	Map<String, Object> data;

	/**
	 * Default constuctor
	 */
	public DataStore()
	{
	};

	/**
	 * @param data
	 *           (RecommendationScenario map)
	 */
	public DataStore(final Map<String, Object> data)
	{
		this.data = data; // TODO : NULL CHECKS
	}

	/**
	 * @return list of maps of Scenarios
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getEntities(String entityName)//getScenarios()
	{
		return (List<Map<String, Object>>) data.get(entityName);//"Scenarios");
	}

	/**
	 * 
	 * @param parentEntityName
	 * @param parentEntityIdAttribute
	 * @param parentEntityId
	 * @param subEntityName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getSubEntityList(final String parentEntityName, final String parentEntityIdAttribute,
			final String parentEntityId, final String subEntityName)//getBasketObjects(final String scenarioId)
	{

		for (final Map<String, Object> parentEntity : (List<Map<String, Object>>) data.get(parentEntityName))//("Scenarios"))
		{
			if (parentEntity.get(parentEntityIdAttribute).equals(parentEntityId))//(scenarioId))
			{
				return (List<Map<String, Object>>) parentEntity.get(subEntityName);//("BasketObjects");
			}
		}
		return null;
	}

	//
	/**
	 * @param scenarioId
	 * @return list of maps of LeadingObjects
	 */
	/*
	 * @SuppressWarnings("unchecked") public List<Map<String, Object>> getLeadingObjects(final String scenarioId) { for
	 * (final Map<String, Object> scenario : (List<Map<String, Object>>) data.get("Scenarios")) { if
	 * (scenario.get("ScenarioId").equals(scenarioId)) { return (List<Map<String, Object>>)
	 * scenario.get("LeadingObjects"); } } return null; }
	 */

	/**
	 * @return data (RecommendationScenario map)
	 */
	public Map<String, Object> getData()
	{
		return data; // TODO : NULL CHECKS
	}

	/**
	 * @param data
	 *           (RecommendationScenario map)
	 */
	public void setData(final Map<String, Object> data)
	{
		this.data = data;
	}

}
