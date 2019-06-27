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

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Default implementation of the {@link ConfigModel}
 */
public class ConfigModelImpl extends BaseModelImpl implements ConfigModel
{
	private String id;
	private String version;
	private String name;
	private InstanceModel rootInstance;
	private boolean complete;
	private boolean consistent;
	private boolean singleLevel;
	private boolean pricingError;
	private PriceModel basePrice;
	private PriceModel selectedOptionsPrice;
	private PriceModel currentTotalSavings;
	private PriceModel currentTotalPrice;
	private List<SolvableConflictModel> solvableConflicts;
	private Set<ProductConfigMessage> messages; // initialized lazy
	private List<CsticValueDelta> csticValueDeltas;
	private String kbId;
	private KBKey kbKey;
	private String variantProduct;

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public void setId(final String id)
	{
		this.id = id;
	}

	@Override
	public String getVersion()
	{
		return version;
	}

	@Override
	public void setVersion(final String version)
	{
		this.version = version;
	}

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
	public InstanceModel getRootInstance()
	{
		return rootInstance;
	}

	@Override
	public void setRootInstance(final InstanceModel rootInstance)
	{
		this.rootInstance = rootInstance;
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
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("\nConfigModelImpl [id=");
		builder.append(id);
		builder.append(", kbId=");
		builder.append(kbId);
		builder.append(", kbKey=");
		builder.append(((kbKey == null) ? "null" : kbKey.toString()));
		builder.append(", name=");
		builder.append(name);
		builder.append(", variantProduct=");
		builder.append(variantProduct);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", consistent=");
		builder.append(consistent);
		builder.append(", singeLevel=");
		builder.append(singleLevel);
		builder.append(", pricingError=");
		builder.append(pricingError);
		builder.append(", solvableConflicts=");
		builder.append(solvableConflicts);
		builder.append("\nbasePrice=");
		builder.append(basePrice);
		builder.append(",\nselectedOptionsPrice=");
		builder.append(selectedOptionsPrice);
		builder.append(",\ncurrentTotalPrice=");
		builder.append(currentTotalPrice);
		builder.append(",\nrootInstance=");
		builder.append(rootInstance);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id, version, name, rootInstance, complete, consistent, singleLevel, //
				pricingError, basePrice, selectedOptionsPrice, currentTotalSavings, //
				currentTotalPrice, kbId, kbKey, variantProduct);
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

		final ConfigModelImpl other = (ConfigModelImpl) obj;
		if (complete != other.complete)
		{
			return false;
		}
		if (consistent != other.consistent)
		{
			return false;
		}
		if (pricingError != other.pricingError)
		{
			return false;
		}
		if (!objectsEqual(id, other.id))
		{
			return false;
		}
		if (!objectsEqual(version, other.version))
		{
			return false;
		}
		if (!objectsEqual(kbId, other.kbId))
		{
			return false;
		}
		if (!objectsEqual(name, other.name))
		{
			return false;
		}
		if (!objectsEqual(rootInstance, other.rootInstance))
		{
			return false;
		}
		if (!objectsEqual(basePrice, other.basePrice))
		{
			return false;
		}
		if (!objectsEqual(selectedOptionsPrice, other.selectedOptionsPrice))
		{
			return false;
		}
		if (!objectsEqual(currentTotalPrice, other.currentTotalPrice))
		{
			return false;
		}
		if (!objectsEqual(kbKey, other.kbKey))
		{
			return false;
		}
		if (!objectsEqual(variantProduct, other.variantProduct))
		{
			return false;
		}
		return singleLevel == other.singleLevel;
	}

	protected boolean objectsEqual(final Object obj1, final Object obj2)
	{
		if (obj1 == null)
		{
			if (obj2 != null)
			{
				return false;
			}
		}
		else if (!obj1.equals(obj2))
		{
			return false;
		}

		return true;
	}

	@Override
	public PriceModel getBasePrice()
	{
		return basePrice;
	}

	@Override
	public void setBasePrice(final PriceModel basePrice)
	{
		this.basePrice = basePrice;
	}

	@Override
	public PriceModel getSelectedOptionsPrice()
	{
		return selectedOptionsPrice;
	}

	@Override
	public void setSelectedOptionsPrice(final PriceModel selectedOptionsPrice)
	{
		this.selectedOptionsPrice = selectedOptionsPrice;
	}

	@Override
	public PriceModel getCurrentTotalPrice()
	{
		return currentTotalPrice;
	}

	@Override
	public void setCurrentTotalPrice(final PriceModel currentTotalPrice)
	{
		this.currentTotalPrice = currentTotalPrice;
	}

	@Override
	public boolean isSingleLevel()
	{
		return singleLevel;
	}

	@Override
	public void setSingleLevel(final boolean singleLevel)
	{
		this.singleLevel = singleLevel;
	}

	@Override
	public void setSolvableConflicts(final List<SolvableConflictModel> solvableConflicts)
	{
		this.solvableConflicts = Optional.ofNullable(solvableConflicts).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());

	}

	@Override
	public List<SolvableConflictModel> getSolvableConflicts()
	{
		return Optional.ofNullable(solvableConflicts).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public Set<ProductConfigMessage> getMessages()
	{
		return Optional.ofNullable(messages).map(Set::stream).orElseGet(Stream::empty).collect(Collectors.toSet());
	}

	@Override
	public void setMessages(final Set<ProductConfigMessage> messages)
	{
		this.messages = Optional.ofNullable(messages).map(Set::stream).orElseGet(Stream::empty).collect(Collectors.toSet());
	}

	@Override
	public void setCsticValueDeltas(final List<CsticValueDelta> csticValueDeltas)
	{
		this.csticValueDeltas = Optional.ofNullable(csticValueDeltas).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	@Override
	public List<CsticValueDelta> getCsticValueDeltas()
	{
		return Optional.ofNullable(csticValueDeltas).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public String getKbId()
	{
		return kbId;
	}

	@Override
	public void setKbId(final String kbId)
	{
		this.kbId = kbId;
	}

	@Override
	public void setPricingError(final boolean pricingError)
	{
		this.pricingError = pricingError;
	}

	@Override
	public boolean hasPricingError()
	{
		return pricingError;
	}

	@Override
	public KBKey getKbKey()
	{
		return kbKey;
	}

	@Override
	public void setKbKey(final KBKey kbKey)
	{
		this.kbKey = kbKey;
	}

	@Override
	public PriceModel getCurrentTotalSavings()
	{
		return currentTotalSavings;
	}

	@Override
	public void setCurrentTotalSavings(final PriceModel currentTotalSavings)
	{
		this.currentTotalSavings = currentTotalSavings;
	}

}
