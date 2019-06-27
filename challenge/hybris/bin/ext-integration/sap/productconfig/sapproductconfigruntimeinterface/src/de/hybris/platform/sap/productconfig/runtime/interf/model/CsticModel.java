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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;


/**
 * Represents the characteristic model.
 */
//Refactoring the constants below into an Enum or own class would be a incompatible change, which we want to avoid.
public interface CsticModel extends BaseModel
{

	/** unknown type */
	int TYPE_UNDEFINED = -1;
	/** Value type for String */
	int TYPE_STRING = 0;
	/** Value type for integer */
	int TYPE_INTEGER = 1;
	/** Value type for float */
	int TYPE_FLOAT = 2;
	/** Value type for boolean */
	int TYPE_BOOLEAN = 3;
	/** Value type for date */
	int TYPE_DATE = 4;
	/** Value type for time */
	int TYPE_TIME = 5;
	/** Value type for currency */
	int TYPE_CURRENCY = 6;
	/** Value type for object class (materials) */
	int TYPE_CLASS = 7;

	/**
	 * Indicating that the value of this cstic was automatically assigned by the configuration engine.
	 */
	String AUTHOR_SYSTEM = "S";
	/**
	 * Indicating that the value of this cstic was assigned by the user.
	 */
	String AUTHOR_USER = "U";
	/**
	 * Indicating that the value of this cstic was defaulted by the system.
	 */
	String AUTHOR_DEFAULT = "D";
	/**
	 * Indicating that there is no author information available
	 */
	String AUTHOR_NOAUTHOR = "N";

	/**
	 * @return the characteristic name
	 */
	String getName();

	/**
	 * @param name
	 *           characteristic name
	 */
	void setName(String name);

	/**
	 * @return the characteristic language dependent name
	 */
	String getLanguageDependentName();

	/**
	 * @param languageDependentName
	 *           characteristic language dependent name
	 */
	void setLanguageDependentName(String languageDependentName);

	/**
	 * Get the long text description for a cstic, which will be displayed under the cstic name in the UI
	 *
	 * @return The long text value
	 */
	String getLongText();

	/**
	 * Set the long text, which will be displayed under the Cstic name in the UI
	 *
	 * @param longText
	 *           Description for the cstic
	 */
	void setLongText(String longText);

	/**
	 * @return an unmodifiable list of all assigned values
	 */
	List<CsticValueModel> getAssignedValues();

	/**
	 * sets assigned value without to check whether the characteristic was changed
	 *
	 * @param assignedValues
	 *           list of all assigned values
	 */
	void setAssignedValuesWithoutCheckForChange(List<CsticValueModel> assignedValues);

	/**
	 * @param assignedValues
	 *           list of all assigned values
	 */
	void setAssignedValues(List<CsticValueModel> assignedValues);

	/**
	 * @return an unmodifiable list of all assignable values
	 */
	List<CsticValueModel> getAssignableValues();

	/**
	 * @param assignableValues
	 *           list of all assignable values
	 */
	void setAssignableValues(List<CsticValueModel> assignableValues);


	/**
	 * @return the value type
	 */
	int getValueType();

	/**
	 * @param valueType
	 *           value type
	 */
	void setValueType(int valueType);

	/**
	 * @return the length of the characteristic value type
	 */
	int getTypeLength();

	/**
	 * @param typeLength
	 *           length of the characteristic value type
	 */
	void setTypeLength(int typeLength);

	/**
	 * @return the number scale
	 */
	int getNumberScale();

	/**
	 * @param numberScale
	 *           the number scale
	 */
	void setNumberScale(int numberScale);

	/**
	 * @return true if the characteristic is visible
	 */
	boolean isVisible();

	/**
	 * @param visble
	 *           flag indicating whether the characteristic is visible
	 */
	void setVisible(boolean visble);

	/**
	 * @return true if the characteristic is consistent
	 */
	boolean isConsistent();

	/**
	 * @param consistent
	 *           flag indicating whether the characteristic is consistent
	 */
	void setConsistent(boolean consistent);

	/**
	 * @return true if the characteristic is complete
	 */
	boolean isComplete();

	/**
	 * @param complete
	 *           flag indicating whether the characteristic is complete
	 */
	void setComplete(boolean complete);

	/**
	 * @return true if the characteristic is read only
	 */
	boolean isReadonly();

	/**
	 * @param readonly
	 *           flag indicating whether the characteristic is read only
	 */
	void setReadonly(boolean readonly);

	/**
	 * @return true if the characteristic is required
	 */
	boolean isRequired();

	/**
	 * @param required
	 *           flag indicating whether the characteristic is required
	 */
	void setRequired(boolean required);

	/**
	 * @return true if the characteristic is multivalued
	 */
	boolean isMultivalued();

	/**
	 * @param multivalued
	 *           flag indicating whether the characteristic is multivalued
	 */
	void setMultivalued(boolean multivalued);

	/**
	 * @return true if the characteristic is changed by front end
	 */
	boolean isChangedByFrontend();

	/**
	 * @param changedByFrontend
	 *           flag indicating whether the characteristic is changed by front end
	 */
	void setChangedByFrontend(boolean changedByFrontend);

	/**
	 * @return the characteristic author
	 */
	String getAuthor();

	/**
	 * @param author
	 *           haracteristic author
	 */
	void setAuthor(String author);

	/**
	 * Assigns the given value to the characteristic, overwriting any previous value assignments.<br>
	 * This is a typical operation for single valued characteristics.
	 *
	 * @param valueName
	 *           the value to set
	 */
	void setSingleValue(String valueName);


	/**
	 * Assigns the given value to the characteristic, while keeping any previous value assignments.<br>
	 * This is a typical operation for multi valued characteristics.
	 *
	 * @param valueName
	 *           the value to add
	 */
	void addValue(String valueName);

	/**
	 * Remove the given value from the assigned values, while keeping the other value assignments.<br>
	 * This is a typical operation for multi valued characteristics.
	 *
	 * @param valueName
	 *           the value to add
	 */
	void removeValue(String valueName);

	/**
	 * Gets the first value of the assigned Values if existing, or null otherwise. This is a typical operation for single
	 * valued characteristics.
	 *
	 * @return first value of assigned values
	 */
	String getSingleValue();

	/**
	 * clears all assigned Values, same as setting an empty List as assigned value list
	 */
	void clearValues();

	/**
	 * @param booleanValue
	 *           <code>true</code>, only if this characteristic allow additional values
	 */
	void setAllowsAdditionalValues(boolean booleanValue);


	/**
	 * @return the characteristic entry field mask for user input
	 */
	String getEntryFieldMask();

	/**
	 * @return true only if this characteristic allow additional values
	 */
	boolean isAllowsAdditionalValues();

	/**
	 * @param csticEntryFieldMask
	 *           characteristic entry field mask for user input
	 */
	void setEntryFieldMask(String csticEntryFieldMask);


	/**
	 * @return true if the characteristic values are intervals in domain
	 */
	boolean isIntervalInDomain();


	/**
	 * @param intervalInDomain
	 *           flag indicating whether the characteristic values are intervals in domain
	 */
	void setIntervalInDomain(boolean intervalInDomain);


	/**
	 * @return True if the characteristic is constrained. This means for us: It carries a static domain (at runtime this
	 *         domain might be gone due to restrictable characteristics!), and no additional values are allowed
	 */
	boolean isConstrained();

	/**
	 * @param constrained
	 *           Flag indicating whether the characteristic is constrained. This means for us: It carries a static domain
	 *           (at runtime this domain might be gone due to restrictable characteristics!), and no additional values
	 *           are allowed
	 */
	void setConstrained(boolean constrained);

	/**
	 * @return the length of the characteristic static domain
	 */
	int getStaticDomainLength();

	/**
	 * @param staticDomainLength
	 *           length of the characteristic static domain
	 */
	void setStaticDomainLength(final int staticDomainLength);

	/**
	 * @return the place holder for input field
	 */
	String getPlaceholder();

	/**
	 * @param placeHolder
	 *           place holder for input field
	 */
	void setPlaceholder(String placeHolder);

	/**
	 * @param instanceId
	 *           ID of corresponding instance. See {@link InstanceModel#getId()}
	 */
	void setInstanceId(String instanceId);

	/**
	 * @return ID of corresponding instance. See {@link InstanceModel#getId()}
	 */
	String getInstanceId();

	/**
	 * @param b
	 *           This characteristic is supposed to be retracted i.e. the user inputs are withdrawn
	 */
	void setRetractTriggered(boolean b);

	/**
	 * @return Is this characteristic supposed to be retracted?
	 */
	boolean isRetractTriggered();

	/**
	 * @param valueName
	 *           value to be removed from the list of assignable values
	 * @return true if the value was removed from the list of assignable values
	 */
	boolean removeAssignableValue(final String valueName);

	/**
	 * @return messages valid for this characteristic
	 */
	default Set<ProductConfigMessage> getMessages()
	{
		return Collections.emptySet();
	}

	/**
	 * @param messages
	 *           valid for this characteristic
	 */
	default void setMessages(final Set<ProductConfigMessage> messages)
	{
		throw new NotImplementedException();
	}

	/**
	 * Sets name of hosting instance. This can be a product key or a class identifier
	 *
	 * @param instanceName
	 */
	void setInstanceName(String instanceName);

	/**
	 * @return Name of hosting instance. This can be a product key or a class identifier
	 */
	String getInstanceName();

}
