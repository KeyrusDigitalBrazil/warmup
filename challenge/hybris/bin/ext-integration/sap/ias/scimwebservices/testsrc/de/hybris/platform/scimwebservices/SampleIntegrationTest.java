/**
 *
 */
package de.hybris.platform.scimwebservices;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.junit.Test;


@IntegrationTest
public class SampleIntegrationTest
{

	private static String SAMPLE_TEST_CLASS = "sample-test-class";

	@Test
	public void testOnValidateWhenScimUserIdIsEmpty() throws InterceptorException
	{
		// do nothing
	}

}
