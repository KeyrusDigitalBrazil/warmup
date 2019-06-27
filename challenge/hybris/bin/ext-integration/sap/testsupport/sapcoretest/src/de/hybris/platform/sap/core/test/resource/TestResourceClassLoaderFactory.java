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
package de.hybris.platform.sap.core.test.resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Factory which creates a class loader that also loads from all existing resource/test directories.
 */
public class TestResourceClassLoaderFactory
{

	private TestResourceClassLoaderFactory() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 *
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger(TestResourceClassLoaderFactory.class.getName());

	/**
	 * Creates the test resource class loader.
	 *
	 * @return test resource class loader
	 */
	public static ClassLoader createClassLoader()
	{
		return createClassLoaderInternal(null);
	}

	/**
	 * Creates the test resource class loader passing a parent class loader.
	 *
	 * @param parentClassLoader
	 *           parent class loader
	 * @return test resource class loader
	 */
	public static ClassLoader createClassLoader(final ClassLoader parentClassLoader)
	{
		return createClassLoaderInternal(parentClassLoader);
	}

	/**
	 * Creates an URL class loader with all resource/test directories.
	 *
	 * @param parentClassLoader
	 *           parent class loader
	 * @return URL class loader
	 */
	private static URLClassLoader createClassLoaderInternal(final ClassLoader parentClassLoader)
	{
		final List<URL> resourceURLs = new ArrayList<URL>();
		try
		{
			final Enumeration<URL> resources = TestResourceClassLoaderFactory.class.getClassLoader().getResources("test");

			if (resources != null)
			{

				iterateOverResourceElements(resourceURLs, resources);

				if (parentClassLoader == null)
				{
					return new URLClassLoader(resourceURLs.toArray(new URL[resourceURLs.size()]));
				}
				else
				{
					return new URLClassLoader(resourceURLs.toArray(new URL[resourceURLs.size()]), parentClassLoader);
				}
			}
		}
		catch (final IOException e1)
		{
			log.warn("No test resource directories found.");
			log.warn(e1);
		}

		return null;
	}

	private static void iterateOverResourceElements(List<URL> resourceURLs, Enumeration<URL> resources) throws MalformedURLException {
		while (resources.hasMoreElements())
        {
            final URL url = resources.nextElement();
            try
            {
                if ("file".equals(url.toURI().getScheme()))
                {
					addResourceURL(resourceURLs, new File(url.toURI()));
				}

            }
            catch (final URISyntaxException e)
            {
                log.error(e);
            }

        }
	}

	private static void addResourceURL(List<URL> resourceURLs, File file) throws MalformedURLException {
		if (file.isDirectory())
        {
            resourceURLs.add(file.toURI().toURL());
            log.trace("Add URL " + file.toURI().toURL() + " to test resource class loader.");
        }
	}
}
