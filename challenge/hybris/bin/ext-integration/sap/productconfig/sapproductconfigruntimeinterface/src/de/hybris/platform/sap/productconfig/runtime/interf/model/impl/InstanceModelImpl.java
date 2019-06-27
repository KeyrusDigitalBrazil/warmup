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

import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticGroupImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Default implementation of the {@link InstanceModel}
 */
public class InstanceModelImpl extends BaseModelImpl implements InstanceModel
{

	private String id;
	private String name;
	private String languageDependentName;
	private String position;

	private List<InstanceModel> subInstances = Collections.emptyList();
	private List<CsticModel> cstics = Collections.emptyList();

	private Map<String, CsticModel> cachedCstics = null;

	private List<CsticGroupModel> csticGroups = Collections.emptyList();

	private boolean complete;
	private boolean consistent;
	private boolean rootInstance;

	private List<VariantConditionModel> variantConditions;

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
	public List<InstanceModel> getSubInstances()
	{
		return Optional.ofNullable(subInstances).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public void setSubInstances(final List<InstanceModel> subInstances)
	{
		this.subInstances = Optional.ofNullable(subInstances).map(List::stream).orElseGet(Stream::empty)
				.collect(Collectors.toList());
	}

	@Override
	public List<CsticModel> getCstics()
	{
		return Collections.unmodifiableList(cstics);
	}

	@Override
	public void setCstics(final List<CsticModel> cstics)
	{
		this.cstics = Optional.ofNullable(cstics).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
		this.cachedCstics = null;
	}

	@Override
	public boolean isRootInstance()
	{
		return rootInstance;
	}

	@Override
	public void setRootInstance(final boolean rootInstance)
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
	public CsticModel getCstic(final String csticName)
	{
		if (cachedCstics == null)
		{
			initCache();
		}
		return cachedCstics.get(csticName);
	}

	protected void initCache()
	{
		final int capa = (int) (cstics.size() / 0.75) + 1;
		this.cachedCstics = new HashMap<>(capa);
		for (final CsticModel cstic : cstics)
		{
			cachedCstics.put(cstic.getName(), cstic);
		}

	}

	@Override
	public void addCstic(final CsticModel cstic)
	{
		if (this.cstics.equals(Collections.emptyList()))
		{
			this.cstics = new ArrayList<>(4);
		}

		cstics.add(cstic);
		if (cachedCstics != null)
		{
			cachedCstics.put(cstic.getName(), cstic);
		}
	}

	@Override
	public boolean removeCstic(final CsticModel cstic)
	{
		final boolean existed = cstics.remove(cstic);
		if (cachedCstics != null)
		{
			cachedCstics.remove(cstic.getName());
		}
		return existed;

	}

	@Override
	public InstanceModel getSubInstance(final String subInstanceId)
	{
		return subInstances.stream().filter(subInstance -> subInstance.getId().equalsIgnoreCase(subInstanceId)).findFirst()
				.orElse(null);
	}

	@Override
	public InstanceModel removeSubInstance(final String subInstanceId)
	{
		final List<InstanceModel> subInstancesForRemoval = subInstances;
		for (final InstanceModel subInstance : subInstancesForRemoval)
		{
			if (subInstance.getId().equalsIgnoreCase(subInstanceId))
			{
				subInstancesForRemoval.remove(subInstance);
				return subInstance;
			}
		}
		return null;
	}

	@Override
	public void setSubInstance(final InstanceModel subInstance)
	{
		throw new IllegalStateException("Method not implemented");
	}

	@Override
	public void setCsticGroups(final List<CsticGroupModel> csticGroups)
	{
		this.csticGroups = Optional.ofNullable(csticGroups).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public List<CsticGroupModel> getCsticGroups()
	{
		return Optional.ofNullable(csticGroups).map(List::stream).orElseGet(Stream::empty).collect(Collectors.toList());
	}

	@Override
	public List<CsticGroup> retrieveCsticGroupsWithCstics()
	{
		final List<CsticGroup> csticGroupsWithCstics = new ArrayList<>();

		for (final CsticGroupModel csticModelGroup : getCsticGroups())
		{
			final CsticGroup csticGroup = new CsticGroupImpl();

			String langDepName = csticModelGroup.getDescription();
			final String groupName = csticModelGroup.getName();
			langDepName = getDisplayName(langDepName, groupName);
			csticGroup.setName(groupName);
			csticGroup.setDescription(langDepName);

			final List<CsticModel> csticsForGroup = getCsticsForGroup(csticModelGroup);
			csticGroup.setCstics(csticsForGroup);

			if (csticsForGroup.isEmpty())
			{
				continue;
			}

			csticGroupsWithCstics.add(csticGroup);
		}

		if (csticGroupsWithCstics.isEmpty())
		{
			final CsticGroup group = new CsticGroupImpl();
			group.setName(GENERAL_GROUP_NAME);
			group.setCstics(getCstics());
			csticGroupsWithCstics.add(group);
		}

		return csticGroupsWithCstics;
	}

	protected String getDisplayName(final String langDepName, final String name)
	{
		String displayName = langDepName;
		if (displayName == null || displayName.isEmpty())
		{
			displayName = "[" + name + "]";
		}
		return displayName;
	}

	protected List<CsticModel> getCsticsForGroup(final CsticGroupModel csticModelGroup)
	{
		final List<CsticModel> csticsForGroup = new ArrayList<>();
		final List<String> csticNames = csticModelGroup.getCsticNames();
		if (csticNames == null)
		{
			return csticsForGroup;
		}

		for (final String csticName : csticNames)
		{
			final CsticModel cstic = getCstic(csticName);
			if (cstic == null)
			{
				continue;
			}

			csticsForGroup.add(cstic);
		}
		return csticsForGroup;
	}

	@Override
	public String getPosition()
	{
		return position;
	}

	@Override
	public void setPosition(final String position)
	{
		this.position = position;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(complete, consistent, cstics, csticGroups, id, languageDependentName, name, position, rootInstance, subInstances);
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
		final InstanceModelImpl other = (InstanceModelImpl) obj;
		if (complete != other.complete)
		{
			return false;
		}
		if (consistent != other.consistent)
		{
			return false;
		}
		if (cstics == null)
		{
			if (other.cstics != null)
			{
				return false;
			}
		}
		else if (!cstics.equals(other.cstics))
		{
			return false;
		}
		if (csticGroups == null)
		{
			if (other.csticGroups != null)
			{
				return false;
			}
		}
		else if (!csticGroups.equals(other.csticGroups))
		{
			return false;
		}
		if (id == null)
		{
			if (other.id != null)
			{
				return false;
			}
		}
		else if (!id.equals(other.id))
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
		if (position == null)
		{
			if (other.position != null)
			{
				return false;
			}
		}
		else if (!position.equals(other.position))
		{
			return false;
		}
		if (rootInstance != other.rootInstance)
		{
			return false;
		}
		if (subInstances == null)
		{
			if (other.subInstances != null)
			{
				return false;
			}
		}
		else if (!subInstances.equals(other.subInstances))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		final StringBuilder builder = new StringBuilder(70);
		builder.append("\nInstanceModelImpl [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", languageDependentName=");
		builder.append(languageDependentName);
		builder.append(", position=");
		builder.append(position);
		builder.append(", complete=");
		builder.append(complete);
		builder.append(", consistent=");
		builder.append(consistent);
		builder.append(", rootInstance=");
		builder.append(rootInstance);

		if (!csticGroups.isEmpty())
		{
			builder.append("\ncsticGroups=");
			builder.append(csticGroups);
			builder.append(',');
		}
		if (!cstics.isEmpty())
		{
			builder.append("\ncstics=");
			builder.append(cstics);
		}
		if (!subInstances.isEmpty())
		{
			builder.append(",\nsubInstances=");
			builder.append(subInstances);
			builder.append(',');
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * @return the variantConditions
	 */
	@Override
	public List<VariantConditionModel> getVariantConditions()
	{
		return variantConditions;
	}

	/**
	 * @param variantConditions
	 *           the variantConditions to set
	 */
	@Override
	public void setVariantConditions(final List<VariantConditionModel> variantConditions)
	{
		this.variantConditions = variantConditions;
	}

}
