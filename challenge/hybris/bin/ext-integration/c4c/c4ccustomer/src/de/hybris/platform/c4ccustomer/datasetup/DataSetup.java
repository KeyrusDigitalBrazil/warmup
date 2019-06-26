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
package de.hybris.platform.c4ccustomer.datasetup;

import de.hybris.platform.c4ccustomer.constants.C4ccustomerConstants;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.scripting.enums.ScriptType;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Essential data creator.
 */
@SystemSetup(extension = C4ccustomerConstants.EXTENSIONNAME)
public class DataSetup extends AbstractSystemSetup
{
	private static final Logger LOG = Logger.getLogger(DataSetup.class);
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;

	/**
	 * Create essential data.
	 */
	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.ALL)
	public void createEssentialData()
	{
		createScript("c4cSync", "c4ccustomer/essentialdata/Synchronize.groovy");
	}

	/**
	 * This method will be called during the system initialization.
	 *
	 * @param context the context providing selected parameters and values
	 */
	@SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
	public void createProjectData(final SystemSetupContext context)
	{
		importImpexFile(context, "/c4ccustomer/import/projectdata-c4cdata-streams.impex");
	}

	protected void createScript(final String modelId, final String scriptResource)
	{
		ScriptModel script = null;
		// Let's try to find first if the script already exist in the DB.
		try
		{
			final ScriptModel example = new ScriptModel();
			example.setCode(modelId);
			example.setScriptType(ScriptType.GROOVY);

			script = flexibleSearchService.getModelByExample(example);
		}
		catch (final ModelNotFoundException e)
		{
			// If the script does not exist
			script = modelService.create(ScriptModel.class);
			script.setScriptType(ScriptType.GROOVY);
			script.setCode(modelId);
		}
		script.setContent(readFromResource(scriptResource));
		modelService.save(script);
	}

	protected String readFromResource(final String resource)
	{
	    if(null!= getClass().getClassLoader()) {
		try (final InputStream stream = getClass().getClassLoader().getResourceAsStream(resource))
		{
			if (stream != null)
			{
				return IOUtils.toString(stream, "UTF-8");
			}
			// if resource does not exist, then throw an exception.
			throw new IllegalStateException("Resource " + resource + " does not exist");
		}
		catch (final IOException e)
		{
			LOG.error("Failed to create essential data: error reading resource " + resource, e);
			throw new IllegalStateException("Failed to create essential data: error reading resource " + resource, e);
		}
	    }else {
	        // if not able to get ClassLoader, then throw an exception.
                throw new IllegalStateException("Not able to get ClassLoader for  " + resource);
	    }
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		return Collections.emptyList();
	}
}
