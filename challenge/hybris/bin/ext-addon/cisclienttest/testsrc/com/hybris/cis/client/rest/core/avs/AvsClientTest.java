/**
 *
 */
package com.hybris.cis.client.rest.core.avs;

import com.hybris.cis.client.shared.models.CisAddress;
import com.hybris.cis.client.shared.models.CisDecision;
import com.hybris.cis.client.avs.AvsClient;
import com.hybris.cis.client.avs.models.AvsResult;
import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import org.junit.Test;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@ManualTest
public class AvsClientTest extends ServicelayerTest
{
	private static final String CLIENT_ID = "TEST-ID";
	private static final String TENANT_ID = "single";

	@Resource
	private AvsClient avsClient;

	@Test
	public void shouldAcceptAddress()
	{
		assertNotNull(avsClient);
		final CisAddress address = new CisAddress("1700 Broadway  Fl 26", "10019", "New York", "NY", "US");
		final AvsResult response = avsClient.verifyAddress(CLIENT_ID, TENANT_ID, address);
		assertEquals(CisDecision.ACCEPT, response.getDecision());
	}

}
