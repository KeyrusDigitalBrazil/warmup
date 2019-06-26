/**
 *
 */
package de.hybris.platform.gigya.gigyaloginaddon.renderers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.gigya.gigyaloginaddon.model.GigyaRaasComponentModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.jsp.PageContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaRaasComponentRendererTest
{

	@InjectMocks
	private final GigyaRaasComponentRenderer renderer = new GigyaRaasComponentRenderer();

	@Mock
	private UserService userService;

	@Mock
	private CMSComponentService cmsComponentService;

	@Mock
	private GigyaRaasComponentModel gigyaRaasComponent;

	@Mock
	private UserModel user;

	@Mock
	private PageContext pageContext;

	@Test
	public void testGetVariablesToExpose()
	{
		renderer.setCmsComponentService(cmsComponentService);
		System.out.println("##########cmsComponentService" + renderer.getCmsComponentService());
		Mockito.when(cmsComponentService.getReadableEditorProperties(Mockito.any())).thenReturn(new ArrayList<String>());
		Mockito.when(gigyaRaasComponent.getUid()).thenReturn("sample-uid");
		Mockito.when(gigyaRaasComponent.getScreenSet()).thenReturn("screen-set");
		Mockito.when(gigyaRaasComponent.getStartScreen()).thenReturn("start-screen");
		Mockito.when(gigyaRaasComponent.getProfileEdit()).thenReturn(Boolean.TRUE);

		Mockito.when(gigyaRaasComponent.getEmbed()).thenReturn(Boolean.FALSE);
		Mockito.when(userService.getCurrentUser()).thenReturn(user);
		Mockito.when(userService.isAnonymousUser(user)).thenReturn(Boolean.TRUE);
		Mockito.when(gigyaRaasComponent.getShowAnonymous()).thenReturn(Boolean.TRUE);

		final Map<String, Object> variables = renderer.getVariablesToExpose(pageContext, gigyaRaasComponent);

		Assert.assertNotNull(variables);

		Assert.assertNotNull(variables.get("gigyaRaas"));
		Assert.assertEquals("sampleuid", variables.get("id"));
		Assert.assertEquals(Boolean.TRUE, variables.get("show"));
		Assert.assertEquals(Boolean.TRUE, variables.get("profileEdit"));
	}
}
