/**
 *
 */
package de.hybris.platform.gigya.gigyaloginaddon.handlers;

import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.interceptors.BeforeViewHandlerAdaptee;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.ui.ModelMap;


/**
 * Before view handler to set gigya related information
 */
public class GigyaBeforeViewHandler implements BeforeViewHandlerAdaptee
{

	private CMSSiteService cmsSiteService;

	private CommonI18NService commonI18NService;

	@Override
	public String beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
			final String viewName) throws Exception
	{
		if (model.containsKey("addOnJavaScriptPaths"))
		{
			final String protocol = request.getScheme();
			final CMSSiteModel site = cmsSiteService.getCurrentSite();
			final GigyaConfigModel gigyaConfig = site.getGigyaConfig();
			if (gigyaConfig != null)
			{
				final String gigyaApiKey = gigyaConfig.getGigyaApiKey();

				final List<String> jsFiles = (List<String>) model.get("addOnJavaScriptPaths");
				final StringBuilder sb = new StringBuilder();
				String gigyaJsUrl = "https://cdns." + gigyaConfig.getGigyaDataCenter() + "/JS/gigya.js?apikey=";
				if ("http".equalsIgnoreCase(protocol))
				{
					gigyaJsUrl = "http://cdn." + gigyaConfig.getGigyaDataCenter() + "/JS/gigya.js?apikey=";
				}
				final String language = getLanguage();

				sb.append(gigyaJsUrl).append(gigyaApiKey).append("&lang=").append(language);
				jsFiles.add(sb.toString());
				final List<JavaScriptVariableData> variables = getJsVariables(model);
				final JavaScriptVariableData loginMode = new JavaScriptVariableData();
				loginMode.setQualifier("gigyaUserMode");
				loginMode.setValue(gigyaConfig.getMode().toString().toLowerCase());
				variables.add(loginMode);
			}
		}
		return viewName;
	}

	protected List<JavaScriptVariableData> getJsVariables(final ModelMap model)
	{
		List<JavaScriptVariableData> variables = (List<JavaScriptVariableData>) model.get("jsVariables");
		if (variables == null)
		{
			variables = new LinkedList<>();
			model.put("jsVariables", variables);
		}
		return variables;
	}

	private String getLanguage()
	{
		return commonI18NService.getCurrentLanguage().getIsocode();
	}

	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	@Required
	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}
}
