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
package de.hybris.y2ysync.demo.electronics.datasetup;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.scripting.enums.ScriptType;
import de.hybris.platform.scripting.model.ScriptModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.y2ysync.demo.electronics.constants.Y2ysyncdemoelectronicsConstants;

import org.springframework.beans.factory.annotation.Required;


@SystemSetup(extension = Y2ysyncdemoelectronicsConstants.EXTENSIONNAME)
public class DataSetup
{
	private static final String SYNC_SCRIPT = "import de.hybris.platform.servicelayer.search.FlexibleSearchQuery\n" +
            "import de.hybris.y2ysync.services.SyncExecutionService.ExecutionMode\n" +
            "\n" +
            "def syncJob = findJob '%s'\n" +
            "syncExecutionService.startSync(syncJob, ExecutionMode.SYNC)\n" +
            "\n" +
            "def findJob(code) {\n" +
            "\tdef fQuery = new FlexibleSearchQuery('SELECT {PK} FROM {Y2YSyncJob} WHERE {code}=?code')\n" +
            "\tfQuery.addQueryParameter('code', code)\n" +
            "\n" +
            "\tflexibleSearchService.searchUnique(fQuery)\n" +
            "}";
	private static final String REMOVE_VERSION_MARKERS_SCRIPT = "import de.hybris.platform.servicelayer.search.FlexibleSearchQuery\n"
			+ "\n"
			+ "def fQuery = new FlexibleSearchQuery('SELECT {PK} FROM {ItemVersionMarker}')\n"
			+ "def result = flexibleSearchService.search(fQuery)\n"
			+ "\n"
			+ "result.getResult().forEach {\n"
			+ "modelService.remove(it) \n" + "}";
    private static final String UPLOAD_DH_EXTENSION_SCRIPT = "import com.hybris.datahub.client.extension.ExtensionInputStreamSource\n" +
            "\n" +
            "def inp = getClass().getResourceAsStream('/y2ysync-demo-dh-model.xml')\n" +
            "def source = new ExtensionInputStreamSource(inp)\n" +
            "\n" +
            "dataHubExtensionUploadService.uploadExtension(source)";


	private ModelService modelService;

	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.INIT)
	public void setup()
	{
		createScript(
				"syncToDataHub",
				getSyncScriptContent("y2ySyncDemoElectronicsToDataHub"));
		createScript(
				"syncToZip",
				getSyncScriptContent("y2ySyncDemoElectronicsToDataZip"));
		createScript("removeVersionMarkers", REMOVE_VERSION_MARKERS_SCRIPT);
        createScript("uploadDhExtension", UPLOAD_DH_EXTENSION_SCRIPT);
	}

	private String getSyncScriptContent(final String cronJobCode)
	{
		return String.format(SYNC_SCRIPT, cronJobCode);
	}

	private void createScript(final String code, final String content)
	{
		final ScriptModel script = modelService.create(ScriptModel.class);
		script.setScriptType(ScriptType.GROOVY);
		script.setContent(content);
		script.setCode(code);
		modelService.save(script);
	}


	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
