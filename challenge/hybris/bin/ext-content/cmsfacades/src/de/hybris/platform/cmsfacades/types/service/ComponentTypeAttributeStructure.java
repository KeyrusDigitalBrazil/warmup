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
package de.hybris.platform.cmsfacades.types.service;

import de.hybris.platform.cmsfacades.data.ComponentTypeAttributeData;
import de.hybris.platform.cmsfacades.data.StructureTypeMode;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;

import java.util.List;

/**
 * Represents meta-information about an <code>AttributeDescriptorModel</code> and the populators required to convert
 * this information to a <code>ComponentTypeAttributeData</code>.
 */
public interface ComponentTypeAttributeStructure
{
	/**
	 * Get the qualifier identifying the <code>AttributeDescriptorModel</code>.
	 *
	 * @return the qualifier
	 */
	String getQualifier();

	/**
	 * Sets the qualifier identifying the <code>AttributeDescriptorModel</code>.
	 *
	 * @param qualifier
	 *           - the qualifier
	 */
	void setQualifier(String qualifier);

	/**
	 * Get the typecode identifying the <code>ComposedTypeModel</code> that this attribute belongs to.
	 *
	 * @return the typecode
	 */
	String getTypecode();

	/**
	 * Sets the typecode identifying the <code>ComposedTypeModel</code> that this attribute belongs to.
	 *
	 * @param typecode
	 *           - the typecode
	 */
	void setTypecode(String typecode);

	/**
	 * Get the populators to be used when converting the representing <code>AttributeDescriptorModel</code> to a
	 * <code>ComponentTypeAttributeData</code>.
	 *
	 * @return the populators
	 */
	List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> getPopulators();

	/**
	 * Sets the populators to be used when converting the representing <code>AttributeDescriptorModel</code> to a
	 * <code>ComponentTypeAttributeData</code>.
	 *
	 * @param populators
	 *           - the populators
	 */
	void setPopulators(List<Populator<AttributeDescriptorModel, ComponentTypeAttributeData>> populators);

	/**
	 * Position of this attribute compared with the position of the other attributes
	 * @return
	 */
	Integer getPosition();
}
