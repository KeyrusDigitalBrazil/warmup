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
package de.hybris.platform.sap.productconfig.runtime.interf.model.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.CollectionUtils;


/**
 * Default implementation of the {@link CsticModel}
 */
public class CsticModelImpl extends BaseModelImpl implements CsticModel
{
	private String name;
	private String languageDependentName;
	private String longText;

	private List<CsticValueModel> assignedValues = Collections.emptyList();
	private List<CsticValueModel> assignableValues = Collections.emptyList();

	private int valueType;
	private int typeLength;
	private int numberScale;

	private boolean complete;
	private boolean consistent;
	private boolean constrained;
	private boolean multivalued;
	private boolean readonly;
	private boolean required;
	private boolean visible;

	private boolean changedByFrontend = false;
	private boolean allowsAdditionalValues;
	private String entryFieldMask;
	private String author;
	private boolean intervalInDomain;

	private int staticDomainLength;


	private String placeholder;
	private String instanceId;
	private boolean retractTriggered;
	private Set<ProductConfigMessage> messages;
	private String instanceName;

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(final String name)
	{
		this.name = name;
	}

	@Override
	public String getLanguageDependentName()
	{
		return languageDependentName;
	}

	@Override
	public void setLanguageDependentName(final String languageDependentName)
	{
		this.languageDependentName = languageDependentName;
	}

	@Override
	public String getLongText()
	{
		return longText;
	}

	@Override
	public void setLongText(final String longText)
	{
		this.longText = longText;
	}

	@Override
	public List<CsticValueModel> getAssignableValues()
	{
		return Optional.ofNullable(assignableValues).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public void setAssignableValues(final List<CsticValueModel> assignableValues)
	{
		this.assignableValues = Optional.ofNullable(assignableValues).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	@Override
	public List<CsticValueModel> getAssignedValues()
	{
		return Optional.ofNullable(assignedValues).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public void setAssignedValues(final List<CsticValueModel> assignedValues)
	{
		if (!this.assignedValues.equals(assignedValues))
		{
			changedByFrontend = true;
			if (assignedValues.size() == 1)
			{
				assignedValues.get(0).setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
			}
			this.setAssignedValuesWithoutCheckForChange(assignedValues);
		}
	}

	@Override
	public void setAssignedValuesWithoutCheckForChange(final List<CsticValueModel> assignedValues)
	{
		this.assignedValues = Optional.ofNullable(assignedValues).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	@Override
	public int getValueType()
	{
		return valueType;
	}

	@Override
	public void setValueType(final int valueType)
	{
		this.valueType = valueType;
	}

	@Override
	public int getTypeLength()
	{
		return typeLength;
	}

	@Override
	public void setTypeLength(final int typeLength)
	{
		this.typeLength = typeLength;
	}

	@Override
	public int getNumberScale()
	{
		return numberScale;
	}

	@Override
	public void setNumberScale(final int numberScale)
	{
		this.numberScale = numberScale;
	}

	@Override
	public boolean isVisible()
	{
		return visible;
	}

	@Override
	public void setVisible(final boolean visible)
	{
		this.visible = visible;
	}

	@Override
	public boolean isConsistent()
	{
		return consistent;
	}

	@Override
	public void setConsistent(final boolean consistent)
	{
		this.consistent = consistent;
	}

	@Override
	public boolean isComplete()
	{
		return complete;
	}

	@Override
	public void setComplete(final boolean complete)
	{
		this.complete = complete;
	}

	@Override
	public boolean isReadonly()
	{
		return readonly;
	}

	@Override
	public void setReadonly(final boolean readonly)
	{
		this.readonly = readonly;
	}

	@Override
	public boolean isRequired()
	{
		return required;
	}

	@Override
	public void setRequired(final boolean required)
	{
		this.required = required;
	}

	@Override
	public boolean isMultivalued()
	{
		return multivalued;
	}

	@Override
	public void setMultivalued(final boolean multivalued)
	{
		this.multivalued = multivalued;
	}

	@Override
	public boolean isChangedByFrontend()
	{
		return changedByFrontend;
	}

	@Override
	public void setChangedByFrontend(final boolean changedByFrontend)
	{
		this.changedByFrontend = changedByFrontend;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(200);
		builder.append("\nCsticModelImpl [name=");
		builder.append(name);
		builder.append(", instanceId=");
		builder.append(instanceId);
		builder.append(", instanceName=");
		builder.append(instanceName);
		builder.append(", languageDependentName=");
		builder.append(languageDependentName);
		builder.append(", longText=[");
		builder.append(longText);
		builder.append("]");
		builder.append(", author=");
		builder.append(author);
		builder.append(", valueType=");
		builder.append(valueType);
		builder.append(", typeLength=");
		builder.append(typeLength);
		builder.append(", numberScale=");
		builder.append(numberScale);
		builder.append(", staticDomainLength=");
		builder.append(staticDomainLength);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", consistent=");
		builder.append(consistent);
		builder.append(", constrained=");
		builder.append(constrained);
		builder.append(", multivalued=");
		builder.append(multivalued);
		builder.append(", readonly=");
		builder.append(readonly);
		builder.append(", required=");
		builder.append(required);
		builder.append(", visible=");
		builder.append(visible);
		builder.append(", allowsAdditionalValues=");
		builder.append(allowsAdditionalValues);
		builder.append(", entryFieldMask=");
		builder.append(entryFieldMask);
		builder.append(", intervalInDomain=");
		builder.append(intervalInDomain);
		builder.append(", placeholder=");
		builder.append(placeholder);
		builder.append(", retractTriggered=");
		builder.append(retractTriggered);
		builder.append(",\nassignedValues=");
		if (!assignedValues.isEmpty())
		{
			builder.append(assignedValues);
		}
		else
		{
			builder.append("[empty]");
		}
		builder.append(",\nassignableValues=");
		if (!assignableValues.isEmpty())
		{
			builder.append(assignableValues);
		}
		else
		{
			builder.append("[empty]");
		}
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(assignableValues, assignedValues, complete, consistent, constrained, languageDependentName, //
				multivalued, name, longText, numberScale, readonly, required, typeLength, valueType, visible, staticDomainLength);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final CsticModelImpl other = (CsticModelImpl) obj;

		//assignableValues null cannot happen, no specific check needed
		if (!assignableValues.equals(other.assignableValues))
		{
			return false;
		}

		//assigedValues null cannot happen, no specific check needed
		if (!assignedValues.equals(other.assignedValues))
		{
			return false;
		}

		if (complete != other.complete)
		{
			return false;
		}
		if (consistent != other.consistent)
		{
			return false;
		}
		if (constrained != other.constrained)
		{
			return false;
		}
		if (languageDependentName == null)
		{
			if (other.languageDependentName != null)
			{
				return false;
			}
		}
		else if (!languageDependentName.equals(other.languageDependentName))
		{
			return false;
		}
		if (multivalued != other.multivalued)
		{
			return false;
		}
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		if (longText == null)
		{
			if (other.longText != null)
			{
				return false;
			}
		}
		else if (!longText.equals(other.longText))
		{
			return false;
		}
		if (numberScale != other.numberScale)
		{
			return false;
		}
		if (readonly != other.readonly)
		{
			return false;
		}
		if (required != other.required)
		{
			return false;
		}
		if (typeLength != other.typeLength)
		{
			return false;
		}
		if (valueType != other.valueType)
		{
			return false;
		}
		if (visible != other.visible)
		{
			return false;
		}
		return staticDomainLength == other.staticDomainLength;
	}

	@Override
	public void setSingleValue(final String value)
	{
		final List<CsticValueModel> newList;
		if (value == null || value.isEmpty())
		{
			newList = Collections.emptyList();
		}
		else
		{
			final CsticValueModel newValueModel = getValueModelForValue(value);
			newList = Collections.singletonList(newValueModel);
		}
		this.setAssignedValues(newList);
	}

	protected CsticValueModel getValueModelForValue(final String valueName)
	{
		CsticValueModel newValueModel = null;
		for (final CsticValueModel assignableValue : assignableValues)
		{
			if (isValueNameMatching(valueName, assignableValue))
			{
				newValueModel = assignableValue;
			}
		}
		if (newValueModel == null)
		{
			newValueModel = new CsticValueModelImpl();
			if (valueType == CsticModel.TYPE_FLOAT || valueType == CsticModel.TYPE_INTEGER)
			{
				newValueModel.setNumeric(true);
			}
			newValueModel.setName(valueName);
		}
		return newValueModel;
	}

	protected boolean isValueNameMatching(final String value, final CsticValueModel valueModel)
	{
		return (value == null && valueModel.getName() == null) || valueModel.getName().equals(value);
	}

	@Override
	public void addValue(final String valueName)
	{
		final CsticValueModel newValueModel = getValueModelForValue(valueName);
		if (!assignedValues.contains(newValueModel))
		{
			if (assignedValues.equals(Collections.emptyList()))
			{
				assignedValues = new ArrayList<>(4);
			}
			else if (assignedValues.size() == 1)
			{
				// ensure no singleton list
				assignedValues = new ArrayList<>(assignedValues);
			}
			newValueModel.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
			assignedValues.add(newValueModel);
			changedByFrontend = true;
		}

	}

	@Override
	public void removeValue(final String valueName)
	{
		if (!assignedValues.isEmpty())
		{
			final CsticValueModel newValueModel = getValueModelForValue(valueName);
			if (assignedValues.contains(newValueModel))
			{
				if (assignedValues.size() == 1)
				{
					assignedValues = Collections.emptyList();
				}
				else
				{
					assignedValues.remove(newValueModel);
				}
				changedByFrontend = true;

			}

		}
	}

	@Override
	public String getSingleValue()
	{
		String value = null;
		if (!assignedValues.isEmpty())
		{
			value = assignedValues.get(0).getName();
		}
		return value;
	}

	@Override
	public void clearValues()
	{
		setAssignedValues(Collections.emptyList());
	}

	@Override
	public boolean isAllowsAdditionalValues()
	{
		return allowsAdditionalValues;
	}

	@Override
	public String getEntryFieldMask()
	{
		return entryFieldMask;
	}

	@Override
	public boolean isIntervalInDomain()
	{
		return this.intervalInDomain;
	}

	@Override
	public void setAllowsAdditionalValues(final boolean allowsAdditionalValues)
	{
		this.allowsAdditionalValues = allowsAdditionalValues;

	}

	@Override
	public void setEntryFieldMask(final String entryFieldMask)
	{
		this.entryFieldMask = entryFieldMask;
	}

	@Override
	public void setIntervalInDomain(final boolean intervalInDomain)
	{
		this.intervalInDomain = intervalInDomain;
	}





	@Override
	public String getAuthor()
	{
		return author;
	}

	@Override
	public void setAuthor(final String author)
	{
		this.author = author;
	}

	@Override
	public boolean isConstrained()
	{
		return constrained;
	}

	@Override
	public void setConstrained(final boolean constrained)
	{
		this.constrained = constrained;
	}

	@Override
	public int getStaticDomainLength()
	{
		return staticDomainLength;
	}

	@Override
	public void setStaticDomainLength(final int staticDomainLength)
	{
		this.staticDomainLength = staticDomainLength;
	}

	@Override
	public String getPlaceholder()
	{
		return placeholder;
	}

	@Override
	public void setPlaceholder(final String placeHolder)
	{
		this.placeholder = placeHolder;
	}


	@Override
	public void setInstanceId(final String instanceId)
	{
		this.instanceId = instanceId;

	}

	@Override
	public String getInstanceId()
	{
		return instanceId;
	}


	@Override
	public void setRetractTriggered(final boolean b)
	{
		if (b)
		{
			changedByFrontend = true;
		}
		retractTriggered = b;

	}


	@Override
	public boolean isRetractTriggered()
	{
		return retractTriggered;
	}

	@Override
	public boolean removeAssignableValue(final String valueName)
	{
		if (CollectionUtils.isEmpty(assignableValues))
		{
			return false;
		}

		boolean removed = false;
		for (final CsticValueModel assignableValue : assignableValues)
		{
			if (assignableValue.getName().equals(valueName))
			{
				assignableValues.remove(assignableValue);
				removed = true;
				break;
			}
		}
		return removed;
	}

	@Override
	public Set<ProductConfigMessage> getMessages()
	{
		if (this.messages == null)
		{
			this.messages = new HashSet<>(4);
		}
		return this.messages;
	}

	@Override
	public void setMessages(final Set<ProductConfigMessage> messages)
	{
		this.messages = Optional.ofNullable(messages).map(Set::stream).orElseGet(Stream::empty).collect(Collectors.toSet());
	}

	@Override
	public void setInstanceName(final String instanceName)
	{
		this.instanceName = instanceName;
	}

	@Override
	public String getInstanceName()
	{
		return this.instanceName;
	}


}
