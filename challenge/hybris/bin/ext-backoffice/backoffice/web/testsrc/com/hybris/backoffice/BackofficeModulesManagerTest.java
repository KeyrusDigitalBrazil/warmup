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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hybris.cockpitng.core.CockpitApplicationException;
import com.hybris.cockpitng.core.modules.LibraryFetcher;
import com.hybris.cockpitng.core.modules.ModuleInfo;
import com.hybris.cockpitng.modules.ModulesEnumeration;
import com.hybris.cockpitng.modules.server.ws.jaxb.CockpitModuleInfo;


@RunWith(MockitoJUnitRunner.class)
public class BackofficeModulesManagerTest
{

	private BackofficeModulesManager manager;

	@Mock
	private LibraryFetcher fetcher;

	@Mock
	private ModulesEnumeration enumeration;

	private final File root = new File("/").getAbsoluteFile();

	private final Properties libsURLS = new Properties();

	@Before
	public void setUp()
	{
		manager = spy(new BackofficeModulesManager(fetcher, enumeration));
		doReturn(root).when(manager).getDataRootDir();
		doNothing().when(manager).storeModuleLibsURLS(any(), any());
		doReturn(libsURLS).when(manager).loadModuleLibsURLS(any());
	}

	@Test
	public void shouldRegisterAndFetchManualModulesAndRemoveOtherModulesOnRefresh() throws CockpitApplicationException, IOException
	{
		// given
		final String MODULE_1 = "ext1";
		final File PATH_1 = buildModuleLibPath(MODULE_1);
		final String URL_1 = "extension://" + MODULE_1;
		doReturn(Optional.of(MODULE_1)).when(manager).getModuleName(eq(PATH_1));
		libsURLS.setProperty(PATH_1.getAbsolutePath(), URL_1);
		final String MODULE_2 = "ext2";
		final File PATH_2 = buildModuleLibPath(MODULE_2);
		final String URL_2 = "manual://" + MODULE_2;
		doReturn(Optional.of(MODULE_2)).when(manager).getModuleName(eq(PATH_2));
		doReturn(Boolean.TRUE).when(manager).isModuleFetched(eq(PATH_2));
		libsURLS.setProperty(PATH_2.getAbsolutePath(), URL_2);
		final String MODULE_3 = "ext3";
		final File PATH_3 = buildModuleLibPath(MODULE_3);
		final String URL_3 = "remote://" + MODULE_3;
		doReturn(Optional.of(MODULE_3)).when(manager).getModuleName(eq(PATH_3));
		libsURLS.setProperty(PATH_3.getAbsolutePath(), URL_3);

		// when
		manager.refreshAndFetch();

		// then
		verify(manager).registerExistingModules(eq(libsURLS));
		final ArgumentCaptor<String> modules = ArgumentCaptor.forClass(String.class);
		verify(manager, times(2)).removeModuleJarImmediately(modules.capture(), any(), same(libsURLS));
		assertThat(modules.getAllValues()).containsOnly(MODULE_1, MODULE_3);
		assertManualModuleRegistered(MODULE_2, PATH_2);
		verify(manager).fetchModules(any(), same(libsURLS));
	}

	@Test
	public void shouldRegisterNewManualModuleWithoutModuleInfoWhenRequired() throws IOException, CockpitApplicationException
	{
		// given
		final String MODULE_1 = "ext1";

		// when
		final File file = manager.registerNewModuleJar(MODULE_1);

		// then
		assertManualModuleRegistered(MODULE_1, file);
	}

	protected void assertManualModuleRegistered(final String moduleName, final File moduleLibFile)
	{
		assertThat(manager.getModules()).containsOnly(moduleName);

		final Optional<URI> moduleSource = manager.getModuleSource(moduleLibFile);
		assertOptional(moduleSource, source -> {
			assertThat(source.getScheme()).isEqualTo("manual");
			assertThat(source.getHost()).isEqualTo(moduleName);
		});
		final Optional<URI> moduleSourceByName = manager.getModuleSource(moduleName);
		assertOptional(moduleSourceByName, source -> {
			assertThat(source.getScheme()).isEqualTo("manual");
			assertThat(source.getHost()).isEqualTo(moduleName);
		});

		final Optional<File> moduleLib = manager.getModuleLib(moduleName);
		assertOptional(moduleLib, lib -> assertThat(lib.getAbsolutePath()).isEqualTo(moduleLibFile.getAbsolutePath()));

		final Optional<ModuleInfo> moduleInfo = manager.getModuleInfo(moduleLibFile);
		assertThat(moduleInfo.isPresent()).isFalse();
		final Optional<ModuleInfo> moduleInfoByName = manager.getModuleInfo(moduleName);
		assertThat(moduleInfoByName.isPresent()).isFalse();

		final Optional<String> moduleNameByURI = manager.getModuleName(moduleSource.get());
		assertOptional(moduleNameByURI, name -> assertThat(name).isEqualTo(moduleName));
		final Optional<String> moduleNameByLib = manager.getModuleName(moduleLibFile);
		assertOptional(moduleNameByLib, name -> assertThat(name).isEqualTo(moduleName));
	}

	protected <V> void assertOptional(final Optional<V> optional, final Consumer<V> assertion)
	{
		assertThat(optional.isPresent()).isTrue();
		assertion.accept(optional.get());
	}

	@Test
	public void shouldUnregisterManuallyRegisteredModuleIfRequired() throws IOException, CockpitApplicationException
	{
		// given
		final String MODULE_1 = "ext1";
		final File file = manager.registerNewModuleJar(MODULE_1);

		// when
		manager.unregisterModuleJar(MODULE_1);

		// then
		assertThat(manager.getModules()).isEmpty();

		final Optional<URI> moduleSource = manager.getModuleSource(file);
		assertThat(moduleSource.isPresent()).isFalse();
		final Optional<URI> moduleSourceByName = manager.getModuleSource(MODULE_1);
		assertThat(moduleSourceByName.isPresent()).isFalse();

		final Optional<File> moduleLib = manager.getModuleLib(MODULE_1);
		assertThat(moduleLib.isPresent()).isFalse();

		final Optional<ModuleInfo> moduleInfo = manager.getModuleInfo(file);
		assertThat(moduleInfo.isPresent()).isFalse();
		final Optional<ModuleInfo> moduleInfoByName = manager.getModuleInfo(MODULE_1);
		assertThat(moduleInfoByName.isPresent()).isFalse();

		final Optional<String> moduleNameByLib = manager.getModuleName(file);
		assertThat(moduleNameByLib.isPresent()).isFalse();
	}

	@Test
	public void shouldNotFailWhenUnregisteringUnknownModule() throws IOException, CockpitApplicationException
	{
		// given
		final String MODULE_1 = "ext1";

		// when
		manager.unregisterModuleJar(MODULE_1);
	}

	@Test
	public void shouldRespectUserHomePlaceholder()
	{
		// given
		final String PLACEHOLDER = "${user.home}";

		// when
		final File rootDir = manager.getRootDir(PLACEHOLDER, manager.getDirProcessors());

		// then
		assertThat(rootDir).isEqualTo(FileUtils.getUserDirectory());
	}

	@Test
	public void shouldRespectTempDirPlaceholder()
	{
		// given
		final String PLACEHOLDER = "${java.io.tmpdir}";

		// when
		final File rootDir = manager.getRootDir(PLACEHOLDER, manager.getDirProcessors());

		// then
		assertThat(rootDir).isEqualTo(FileUtils.getTempDirectory());
	}

	@Test
	public void shouldRespectDataHomePlaceholder()
	{
		// given
		final String PLACEHOLDER = "${data.home}";

		// when
		final File rootDir = manager.getRootDir(PLACEHOLDER, manager.getDirProcessors());

		// then
		assertThat(rootDir.getAbsolutePath())
				.isEqualTo(Utilities.getPlatformConfig().getSystemConfig().getDataDir() + File.separator + "backoffice");
	}

	@Test
	public void shouldRegisterModuleFromEnumerationWithoutSourceCodeOnRefresh() throws CockpitApplicationException
	{
		// given
		final String MODULE_1 = "ext1";
		final ModuleInfo MODULE_INFO_1 = createModuleInfo(MODULE_1);
		final String MODULE_2 = "ext2";
		final ModuleInfo MODULE_INFO_2 = createModuleInfo(MODULE_2);
		final Iterator<ModuleInfo> moduleIterator = Arrays.asList(MODULE_INFO_1, MODULE_INFO_2).iterator();
		when(enumeration.hasMoreElements()).then(inv -> moduleIterator.hasNext());
		when(enumeration.nextElement()).then(inv -> moduleIterator.next());

		// when
		manager.refreshAndFetch();

		// then
		assertExtensionModuleRegistered(MODULE_1, null);
		assertExtensionModuleRegistered(MODULE_2, null);
		assertThat(manager.getModules()).containsOnly(MODULE_1, MODULE_2);
		final ArgumentCaptor<ModuleInfo> infos = ArgumentCaptor.forClass(ModuleInfo.class);
		verify(manager, times(2)).registerNewModuleImmediately(infos.capture(), any());
		assertThat(infos.getAllValues()).containsExactly(MODULE_INFO_1, MODULE_INFO_2);
		verify(fetcher, never()).fetchLibrary(any(), any());
		verify(manager, never()).registerNewModuleJarImmediately(any(), any(), any(), any());
	}

	@Test
	public void shouldRegisterModuleAndFetchFromEnumerationWithSourceCodeOnRefresh() throws CockpitApplicationException
	{
		// given
		final String MODULE_1 = "ext1";
		final ModuleInfo MODULE_INFO_1 = createModuleInfo(MODULE_1);
		final File PATH_1 = buildModuleLibPath(MODULE_1);
		final String MODULE_2 = "ext2";
		final ModuleInfo MODULE_INFO_2 = createModuleInfo(MODULE_2);
		final File PATH_2 = buildModuleLibPath(MODULE_2);
		final Iterator<ModuleInfo> moduleIterator = Arrays.asList(MODULE_INFO_1, MODULE_INFO_2).iterator();
		when(enumeration.hasMoreElements()).then(inv -> moduleIterator.hasNext());
		when(enumeration.nextElement()).then(inv -> moduleIterator.next());
		when(fetcher.canFetchLibrary(same(MODULE_INFO_1))).thenReturn(Boolean.TRUE);
		when(fetcher.canFetchLibrary(same(MODULE_INFO_2))).thenReturn(Boolean.TRUE);

		// when
		manager.refreshAndFetch();

		// then
		assertExtensionModuleRegistered(MODULE_1, PATH_1);
		assertExtensionModuleRegistered(MODULE_2, PATH_2);
		assertThat(manager.getModules()).containsOnly(MODULE_1, MODULE_2);
		final ArgumentCaptor<ModuleInfo> registeredInfos = ArgumentCaptor.forClass(ModuleInfo.class);
		verify(manager, times(2)).registerNewModuleImmediately(registeredInfos.capture(), any());
		assertThat(registeredInfos.getAllValues()).containsExactly(MODULE_INFO_1, MODULE_INFO_2);

		final ArgumentCaptor<ModuleInfo> fetchedInfos = ArgumentCaptor.forClass(ModuleInfo.class);
		verify(fetcher, times(2)).fetchLibrary(fetchedInfos.capture(), any());
		assertThat(fetchedInfos.getAllValues()).containsExactly(MODULE_INFO_1, MODULE_INFO_2);

		final ArgumentCaptor<String> registeredNames = ArgumentCaptor.forClass(String.class);
		verify(manager, times(2)).registerNewModuleJarImmediately(registeredNames.capture(), any(), any(), any());
		assertThat(registeredNames.getAllValues()).containsExactly(MODULE_1, MODULE_2);
	}

	protected ModuleInfo createModuleInfo(final String moduleName)
	{
		final CockpitModuleInfo ret = new CockpitModuleInfo();
		ret.setLocationUrl("extension://" + moduleName);
		ret.setId(moduleName);
		ret.setIconUrl("/cng/img/MMC.png");
		ret.setWidgetsPackage(moduleName + "_bof.jar");
		ret.setParentModulesLocationUrls(Collections.emptyList());
		return ret;
	}

	protected void assertExtensionModuleRegistered(final String moduleName, final File moduleLibFile)
	{
		assertThat(manager.getModules()).contains(moduleName);

		final Optional<URI> moduleSource = manager.getModuleSource(moduleName);
		assertOptional(moduleSource, source -> {
			assertThat(source.getScheme()).isEqualTo("extension");
			assertThat(source.getHost()).isEqualTo(moduleName);
		});

		final Optional<File> moduleLib = manager.getModuleLib(moduleName);
		if (moduleLibFile != null)
		{
			assertOptional(moduleLib, lib -> assertThat(lib.getAbsoluteFile()).isEqualTo(moduleLibFile.getAbsoluteFile()));

			final Optional<ModuleInfo> moduleInfo = manager.getModuleInfo(moduleLib.get());
			assertOptional(moduleInfo, info -> assertThat(info.getId()).isEqualTo(moduleName));

			final Optional<String> moduleNameByLib = manager.getModuleName(moduleLib.get());
			assertOptional(moduleNameByLib, name -> assertThat(name).isEqualTo(moduleName));
		}
		else
		{
			assertThat(moduleLib.isPresent()).isFalse();
		}

		final Optional<ModuleInfo> moduleInfoByName = manager.getModuleInfo(moduleName);
		assertOptional(moduleInfoByName, info -> assertThat(info.getId()).isEqualTo(moduleName));

		final Optional<String> moduleNameByURI = manager.getModuleName(moduleSource.get());
		assertOptional(moduleNameByURI, name -> assertThat(name).isEqualTo(moduleName));
	}

	protected File buildModuleLibPath(final String moduleName)
	{
		return new File(root, "widgetlib" + File.separator + "deployed" + File.separator + moduleName + ".jar").getAbsoluteFile();
	}

}
