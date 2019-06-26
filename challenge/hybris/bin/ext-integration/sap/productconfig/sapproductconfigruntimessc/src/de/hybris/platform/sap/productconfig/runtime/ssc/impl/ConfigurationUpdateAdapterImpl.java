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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueDelta;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ValueChangeType;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationUpdateAdapter;
import de.hybris.platform.sap.productconfig.runtime.ssc.SolvableConflictAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.custdev.projects.fbs.slc.cfg.client.IConfigSessionClient;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDeltaBean;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;


/**
 * Default implementation of {@link ConfigurationUpdateAdapter}
 */
public class ConfigurationUpdateAdapterImpl implements ConfigurationUpdateAdapter
{
	private static final Logger LOG = Logger.getLogger(ConfigurationUpdateAdapterImpl.class);
	private final SSCTimer timer = new SSCTimer();
	private SolvableConflictAdapter conflictAdapter;
	private boolean trackingEnabled = true;
	private ObjectMapper mapper;
	private static final boolean WITHOUT_DESCRIPTION = true;

	@Override
	public boolean updateConfiguration(final ConfigModel configModel, final String plainId, final IConfigSessionClient session)
	{

		final String qualifiedId = configModel.getId();
		final InstanceModel rootInstanceModel = configModel.getRootInstance();
		initCsticValueDeltaList(configModel);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Updating config with data: " + configModel.toString());
		}

		final boolean sscUpdated;
		try
		{
			sscUpdated = updateInstance(qualifiedId, plainId, rootInstanceModel, configModel, session);
		}
		catch (final IpcCommandException e)
		{
			throw new IllegalStateException("Could not update instance", e);
		}

		if (LOG.isDebugEnabled())
		{
			if (sscUpdated)
			{
				LOG.debug("Update for config with id: " + configModel.getId() + " executed");
			}
			else
			{
				LOG.debug("There was nothing to update for config with id: " + configModel.getId());
			}
		}
		return sscUpdated;
	}

	protected void initCsticValueDeltaList(final ConfigModel configModel)
	{
		List<CsticValueDelta> csticValueDeltas;
		if (trackingEnabled)
		{
			csticValueDeltas = new ArrayList<>();
		}
		else
		{
			csticValueDeltas = Collections.emptyList();
		}
		configModel.setCsticValueDeltas(csticValueDeltas);
	}



	protected boolean updateInstance(final String qualifiedId, final String configId, final InstanceModel instanceModel,
			final ConfigModel configModel, final IConfigSessionClient session) throws IpcCommandException
	{

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Update instance : InstanceID = " + instanceModel.getId());
		}

		final String instanceId = instanceModel.getId();
		final List<CsticModel> csticModels = instanceModel.getCstics();

		final List<ICsticData> csticDataList = new ArrayList<>();
		final List<ICsticData> csticDataListToClear = new ArrayList<>();


		boolean instanceUpdated = handleCstics(configModel, session, instanceId, csticModels, csticDataList, csticDataListToClear,
				configId);

		final boolean doDeleteCstics = !csticDataListToClear.isEmpty();
		if (doDeleteCstics)
		{
			deleteCsticValues(configId, session, instanceId, csticDataListToClear);
			instanceUpdated = true;
		}

		final boolean doSetCstics = !csticDataList.isEmpty();
		if (doSetCstics)
		{
			setCsticValues(configId, session, instanceId, csticDataList);
			instanceUpdated = true;
		}

		if (trackingEnabled && (doDeleteCstics || doSetCstics))
		{
			fillCsticValueDeltas(session, configId, configModel, instanceId, csticDataListToClear, csticDataList);
		}

		final boolean subInstanceUpdated = updateSubInstances(qualifiedId, configId, instanceModel, configModel, session);
		return instanceUpdated || subInstanceUpdated;
	}


	protected void fillCsticValueDeltas(final IConfigSessionClient session, final String configId, final ConfigModel configModel,
			final String instanceId, final List<ICsticData> csticDataListToClear, final List<ICsticData> csticDataList)
			throws IpcCommandException
	{
		final List<CsticValueDelta> csticValueDeltas = configModel.getCsticValueDeltas();
		final IInstanceData instance = session.getInstance(configId, instanceId);
		final String instanceName = instance.getInstName();

		for (final ICsticData icd : csticDataListToClear)
		{
			mapICsticDataToCsticValueDelta(csticValueDeltas, instanceName, instanceId, ValueChangeType.DELETE, icd);
		}
		for (final ICsticData icd : csticDataList)
		{
			mapICsticDataToCsticValueDelta(csticValueDeltas, instanceName, instanceId, ValueChangeType.SET, icd);
		}

		configModel.setCsticValueDeltas(csticValueDeltas);
	}


	protected void mapICsticDataToCsticValueDelta(final List<CsticValueDelta> csticValueDeltas, final String instanceName,
			final String instanceId, final ValueChangeType changeType, final ICsticData icd)
	{
		final List<ICsticValueData> icvdList = CollectionUtils.arrayToList(icd.getCsticValues());
		if ((icvdList != null) && !icvdList.isEmpty())
		{
			final CsticValueDelta delta = new CsticValueDelta();
			delta.setInstanceName(instanceName);
			delta.setInstanceId(instanceId);
			delta.setCsticName(icd.getCsticHeader().getCsticName());

			final List<String> valueNames = new ArrayList(icvdList.size());
			for (final ICsticValueData iDelta : icvdList)
			{
				valueNames.add(iDelta.getValueName());
			}

			delta.setValueNames(valueNames);
			delta.setChangeType(changeType);
			csticValueDeltas.add(delta);
		}
	}

	protected boolean handleCstics(final ConfigModel configModel, final IConfigSessionClient session, final String rootInstanceId,
			final List<CsticModel> csticModels, final List<ICsticData> csticDataList, final List<ICsticData> csticDataListToClear,
			final String configId) throws IpcCommandException
	{
		boolean instanceUpdated = false;
		for (final CsticModel csticModel : csticModels)
		{
			instanceUpdated = instanceUpdated
					|| handleCstic(configModel, session, rootInstanceId, csticDataList, csticDataListToClear, configId, csticModel);
		}
		return instanceUpdated;
	}

	protected boolean handleCstic(final ConfigModel configModel, final IConfigSessionClient session, final String rootInstanceId,
			final List<ICsticData> csticDataList, final List<ICsticData> csticDataListToClear, final String configId,
			final CsticModel csticModel) throws IpcCommandException
	{
		boolean instanceUpdated = false;
		if (csticModel.isChangedByFrontend())
		{

			if (LOG.isDebugEnabled())
			{
				LOG.debug("Cstic changed by Frontend: " + csticModel.toString());
			}

			if (hasBeenRetracted(csticModel, configModel, session, configId))
			{
				instanceUpdated = true;
				return instanceUpdated;
			}
			final String csticName = csticModel.getName();

			timer.start("getCstic");
			final ICsticData csticData = session.getCstic(rootInstanceId, csticName, WITHOUT_DESCRIPTION, configId);
			timer.stop();

			final List<String> newValues = getValuesToBeAssigned(csticModel);
			final List<String> oldValues = getValuesPreviouslyAssigned(csticData);

			final ICsticValueData[] valuesToSet = determineValuesToSet(newValues, oldValues);
			if (valuesToSet.length > 0)
			{
				csticData.setCsticValues(valuesToSet);
				csticDataList.add(csticData);
			}

			// explicitly delete old value for single-value cstics only if there is no new value
			final boolean isMultiValue = csticData.getCsticHeader().getCsticMulti().booleanValue();
			if (valuesToSet.length == 0 || isMultiValue)
			{
				final ICsticValueData[] valuesToDelete = determineValuesToDelete(newValues, oldValues);
				if (valuesToDelete.length > 0)
				{
					timer.start("getCstic");
					final ICsticData csticDataToClear = session.getCstic(rootInstanceId, csticName, WITHOUT_DESCRIPTION, configId);
					timer.stop();

					csticDataToClear.setCsticValues(valuesToDelete);
					csticDataListToClear.add(csticDataToClear);
				}
			}
		}
		return instanceUpdated;
	}


	protected boolean updateSubInstances(final String qualifiedId, final String plainId, final InstanceModel instanceModel,
			final ConfigModel configModel, final IConfigSessionClient session) throws IpcCommandException
	{

		boolean subInstanceUpdated = false;
		final List<InstanceModel> subInstances = instanceModel.getSubInstances();
		for (final InstanceModel subInstanceModel : subInstances)
		{
			final boolean instanceUpdated = updateInstance(qualifiedId, plainId, subInstanceModel, configModel, session);
			subInstanceUpdated = subInstanceUpdated || instanceUpdated;
		}
		return subInstanceUpdated;
	}

	/**
	 * Handles the retraction of a cstic.
	 */
	protected boolean hasBeenRetracted(final CsticModel csticModel, final ConfigModel configModel,
			final IConfigSessionClient session, final String configId) throws IpcCommandException
	{
		if (csticModel.isRetractTriggered())
		{
			final String assumptionId = conflictAdapter.getAssumptionId(csticModel.getName(), configModel);
			if (assumptionId == null)
			{
				throw new IllegalStateException("In case a cstic is to be retracted, an assumption ID is needed");
			}
			timer.start("retractConflict");
			session.deleteConflictingAssumption(configId, assumptionId);
			timer.stop();
			return true;
		}
		return false;
	}

	protected List<String> getValuesToBeAssigned(final CsticModel csticModel)
	{
		final List<String> valueNames = new ArrayList(csticModel.getAssignedValues().size());
		for (final CsticValueModel value : csticModel.getAssignedValues())
		{
			valueNames.add(value.getName());
		}
		return valueNames;
	}

	protected List<String> getValuesPreviouslyAssigned(final ICsticData csticData)
	{
		final List<String> valueNames = new ArrayList(4);
		for (final ICsticValueData value : csticData.getCsticValues())
		{
			if (value.getValueAssigned().booleanValue())
			{
				valueNames.add(value.getValueName());
			}
		}

		return valueNames;
	}

	protected ICsticValueData[] determineValuesToDelete(final List<String> newValues, final List<String> oldValues)
	{
		final List<ICsticValueData> csticValueDataList = new ArrayList<>();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Cstic values to delete newValues/oldValues: " + newValues + " / " + oldValues);
		}

		for (final String value : oldValues)
		{
			if (!newValues.contains(value))
			{
				final ICsticValueData valueData = new CsticValueData();
				valueData.setValueName(value);
				csticValueDataList.add(valueData);
			}
		}

		return csticValueDataList.toArray(new ICsticValueData[csticValueDataList.size()]);

	}

	protected ICsticValueData[] determineValuesToSet(final List<String> newValues, final List<String> oldValues)
	{
		final List<ICsticValueData> csticValueDataList = new ArrayList<>();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Cstic values to set newValues/oldValues: " + newValues + " / " + oldValues);
		}

		for (final String value : newValues)
		{
			if (!oldValues.contains(value))
			{
				final ICsticValueData valueData = new CsticValueData();
				valueData.setValueName(value);
				csticValueDataList.add(valueData);
			}
		}

		return csticValueDataList.toArray(new ICsticValueData[csticValueDataList.size()]);

	}



	protected void deleteCsticValues(final String plainId, final IConfigSessionClient session, final String rootInstanceId,
			final List<ICsticData> csticDataListToClear) throws IpcCommandException
	{
		final ICsticData[] csticDataArray = csticDataListToClear.toArray(new ICsticData[csticDataListToClear.size()]);

		final String configId = plainId;
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Delete Cstic values : InstanceID = " + rootInstanceId + " / csticDataList = " + csticDataListToClear);
		}

		timer.start("deleteCsticValues");
		final IDeltaBean delta = session.deleteCsticValues(rootInstanceId, "false", csticDataArray, configId);
		timer.stop();
		logDelta(delta);
	}


	protected void setCsticValues(final String plainId, final IConfigSessionClient session, final String rootInstanceId,
			final List<ICsticData> csticDataList) throws IpcCommandException
	{
		final ICsticData[] csticDataArray = csticDataList.toArray(new ICsticData[csticDataList.size()]);
		final String configId = plainId;

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Set Cstic values : InstanceID = " + rootInstanceId + " / csticDataList = " + csticDataList);
		}

		timer.start("setCsticsValues");
		final IDeltaBean delta = session.setCsticsValues(rootInstanceId, configId, false, csticDataArray);
		timer.stop();
		logDelta(delta);
	}

	protected void logDelta(final IDeltaBean delta)
	{
		if (LOG.isDebugEnabled())
		{
			try
			{
				LOG.debug("DELTA_BEAN:" + delta.toString());
				LOG.debug("DELTA_BEAN_AS_JSON" + getObjectMapper().writeValueAsString(Collections.singletonMap("deltaBean", delta)));
			}
			catch (final IOException e)
			{
				LOG.debug("Error while stringify delta bean: " + e.getMessage(), e);
			}
		}
	}

	protected ObjectMapper getObjectMapper()
	{
		if (null == this.mapper)
		{
			this.mapper = new ObjectMapper();
		}
		return mapper;
	}


	/**
	 * @param solvableConflictAdapterImpl
	 */
	public void setConflictAdapter(final SolvableConflictAdapter solvableConflictAdapterImpl)
	{
		conflictAdapter = solvableConflictAdapterImpl;
	}


	protected boolean isTrackingEnabled()
	{
		return trackingEnabled;
	}

	/**
	 * @param trackingEnabled
	 */
	public void setTrackingEnabled(final boolean trackingEnabled)
	{
		this.trackingEnabled = trackingEnabled;
	}
}
