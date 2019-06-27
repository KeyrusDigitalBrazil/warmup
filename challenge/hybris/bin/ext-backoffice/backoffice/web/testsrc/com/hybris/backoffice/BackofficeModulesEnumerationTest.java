/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.backoffice;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.util.Utilities;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.EnumerationUtils;
import org.junit.Test;
import org.mockito.Spy;

import com.hybris.cockpitng.core.modules.ModuleInfo;


@IntegrationTest
public class BackofficeModulesEnumerationTest extends ServicelayerTest
{

	private static final String META_BACKOFFICE_NATURE = "backoffice-module";
	@Spy
	private final BackofficeModulesEnumeration enumeration = new BackofficeModulesEnumeration();

	@Test
	public void shouldIterateOverAllBackofficeModulesAndProperlyInterpretThem()
	{
		// given
		final Map<String, ExtensionInfo> BACKOFFICE_MODULES = getBackofficeModules().stream()
				.collect(Collectors.toMap(ExtensionInfo::getName, Function.identity()));

		// when
		final List<ModuleInfo> enumerationList = EnumerationUtils.toList(enumeration);

		// then
		assertThat(enumerationList).hasSize(BACKOFFICE_MODULES.size());
		enumerationList.forEach(moduleInfo -> {
			final ExtensionInfo extensionInfo = BACKOFFICE_MODULES.get(moduleInfo.getId());
			assertThat(extensionInfo).isNotNull();
			assertThat(moduleInfo.getWidgetsPackage()).isEqualTo(moduleInfo.getId() + "_bof.jar");
			assertThat(moduleInfo.getLocationUrl()).isEqualTo(getModuleUrl(moduleInfo.getId()));
			assertThat(moduleInfo.getParentModulesLocationUrls()).containsOnly(resolveParentModuleUrls(extensionInfo).toArray());
		});
	}

	protected String getModuleUrl(final String moduleName)
	{
		return "extension://" + moduleName;
	}

	protected Collection<String> resolveParentModuleUrls(final ExtensionInfo info)
	{
		return info.getAllRequiredExtensionInfos().stream()
				.filter(dependency -> Boolean.parseBoolean(dependency.getMeta(META_BACKOFFICE_NATURE)))
				.map(dependency -> getModuleUrl(dependency.getName())).collect(Collectors.toList());
	}

	@Test
	public void shouldReiterateAfterReset()
	{
		// given
		final List<String> enumerationList = ((List<ModuleInfo>) EnumerationUtils.toList(enumeration)).stream()
				.map(ModuleInfo::getId).collect(Collectors.toList());

		// when
		enumeration.reset();

		// then
		final List<String> newEnumerationList = ((List<ModuleInfo>) EnumerationUtils.toList(enumeration)).stream()
				.map(ModuleInfo::getId).collect(Collectors.toList());
		assertThat(newEnumerationList).isEqualTo(enumerationList);
	}

	protected static List<ExtensionInfo> getBackofficeModules()
	{
		final PlatformConfig PLATFORM_CONFIG = ConfigUtil.getPlatformConfig(Utilities.class);
		return PLATFORM_CONFIG.getExtensionInfosInBuildOrder().stream()
				.filter(ext -> Boolean.parseBoolean(ext.getMeta(META_BACKOFFICE_NATURE))).collect(Collectors.toList());
	}

}
