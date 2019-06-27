/**
 *
 */
package de.hybris.platform.gigya.gigyaloginaddon.handlers;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.gigya.gigyaservices.enums.GigyaUserManagementMode;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaBeforeViewHandlerTest
{

	private static final String SAMPLE_VIEW = "sample-view";

	@InjectMocks
	private final GigyaBeforeViewHandler gigyaBeforeViewHandler = new GigyaBeforeViewHandler();

	@Mock
	private CMSSiteService cmsSiteService;

	@Mock
	private GenericDao<GigyaConfigModel> gigyaConfigGenericDao;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private ModelMap model;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private CMSSiteModel site;

	@Mock
	private GigyaConfigModel gigyaConfig;

	@Mock
	private LanguageModel language;

	@Test
	public void testBeforeViewWhenNoAddonJSPathsExist() throws Exception
	{
		Mockito.when(model.containsKey("addOnJavaScriptPaths")).thenReturn(Boolean.FALSE);

		Assert.assertEquals(SAMPLE_VIEW, gigyaBeforeViewHandler.beforeView(request, response, model, SAMPLE_VIEW));

		Mockito.verifyZeroInteractions(cmsSiteService);
	}

	@Test
	public void testBeforeViewWhenAddonJSPathsExistsButNoGigyaConfig() throws Exception
	{
		Mockito.when(model.containsKey("addOnJavaScriptPaths")).thenReturn(Boolean.TRUE);
		Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(site);
		Mockito.when(site.getGigyaConfig()).thenReturn(null);

		Assert.assertEquals(SAMPLE_VIEW, gigyaBeforeViewHandler.beforeView(request, response, model, SAMPLE_VIEW));

		Mockito.verify(model, Mockito.times(0)).get("addOnJavaScriptPaths");
	}

	@Test
	public void testBeforeViewWhenAddonJSPathsExistsWithGigyaConfig() throws Exception
	{
		Mockito.when(request.getScheme()).thenReturn("https");
		Mockito.when(model.containsKey("addOnJavaScriptPaths")).thenReturn(Boolean.TRUE);
		Mockito.when(cmsSiteService.getCurrentSite()).thenReturn(site);
		Mockito.when(site.getGigyaConfig()).thenReturn(gigyaConfig);
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(language);
		Mockito.when(language.getIsocode()).thenReturn("en");
		Mockito.when(model.get("addOnJavaScriptPaths")).thenReturn(new ArrayList<>());
		Mockito.when(model.get("jsVariables")).thenReturn(new ArrayList<>());
		Mockito.when(gigyaConfig.getMode()).thenReturn(GigyaUserManagementMode.RAAS);

		Assert.assertEquals(SAMPLE_VIEW, gigyaBeforeViewHandler.beforeView(request, response, model, SAMPLE_VIEW));

		Mockito.verify(model, Mockito.times(1)).get("addOnJavaScriptPaths");
	}

	@Test
	public void testGetJsVariablesWhenVariablesAreNull()
	{
		Mockito.when(model.get("jsVariables")).thenReturn(null);

		Assert.assertNotNull(gigyaBeforeViewHandler.getJsVariables(model));

		Mockito.verify(model, Mockito.times(1)).put(Mockito.eq("jsVariables"), Mockito.any());
	}

	@Test
	public void testGetJsVariablesWhenVariablesExists()
	{
		final List<JavaScriptVariableData> data = new ArrayList<>();
		Mockito.when(model.get("jsVariables")).thenReturn(data);

		Assert.assertEquals(data, gigyaBeforeViewHandler.getJsVariables(model));

		Mockito.verify(model, Mockito.times(0)).put(Mockito.eq("jsVariables"), Mockito.any());
	}

}
