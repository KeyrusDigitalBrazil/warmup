/**
 *
 */
package de.hybris.platform.gigya.gigyaloginaddon.controllers.cms;

import de.hybris.platform.acceleratorstorefrontcommons.security.AutoLoginStrategy;
import de.hybris.platform.addonsupport.controllers.AbstractAddOnController;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.gigya.gigyafacades.login.GigyaLoginFacade;
import de.hybris.platform.gigya.gigyaservices.data.GigyaAjaxResponse;
import de.hybris.platform.gigya.gigyaservices.data.GigyaJsOnLoginInfo;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;



@Controller
@RequestMapping("/gigyaraas")
public class GigyaRaasLoginController extends AbstractAddOnController
{

	private static final Logger LOG = Logger.getLogger(GigyaRaasLoginController.class);
	private static final String ERROR = "error";

	@Resource(name = "cmsSiteService")
	private CMSSiteService cmsSiteService;

	@Resource
	private GigyaLoginFacade gigyaLoginFacade;

	@Resource
	private AutoLoginStrategy gigyaAutoLoginStrategy;

	@Resource
	private SessionService sessionService;

	@Resource
	private CustomerFacade customerFacade;

	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public GigyaAjaxResponse raasLogin(@RequestBody final MultiValueMap<String, String> bodyParameterMap,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final GigyaAjaxResponse gigyaResponse = new GigyaAjaxResponse();

		GigyaJsOnLoginInfo jsInfo = null;
		try
		{
			jsInfo = getGigyaJsInfoFromBody(bodyParameterMap);
		}
		catch (final IOException e)
		{
			LOG.error("Error parsing Response from Gigya." + e);
			gigyaResponse.setCode(500);
			gigyaResponse.setResult(ERROR);
			gigyaResponse.setMessage("Error logging in user. Please contact support.");
			return gigyaResponse;
		}
		final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
		final boolean userProcessed = gigyaLoginFacade.processGigyaLogin(jsInfo, cmsSite.getGigyaConfig());

		if (userProcessed)
		{
			gigyaAutoLoginStrategy.login(gigyaLoginFacade.getHybrisUidForGigyaUser(jsInfo.getUID()), "", request, response);
			gigyaResponse.setCode(0);
			gigyaResponse.setResult("success");
			gigyaResponse.setMessage("User successfully logged in.");
			return gigyaResponse;
		}
		else
		{
			gigyaResponse.setCode(500);
			gigyaResponse.setResult(ERROR);
			gigyaResponse.setMessage("Error logging in user. Please contact support.");
			return gigyaResponse;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/profile", method = RequestMethod.POST)
	public GigyaAjaxResponse updateRaasProfile(@RequestBody final MultiValueMap<String, String> bodyParameterMap,
			final HttpServletRequest request, final HttpServletResponse response)
	{
		final GigyaAjaxResponse gigyaResponse = new GigyaAjaxResponse();
		GigyaJsOnLoginInfo jsInfo = null;
		try
		{
			jsInfo = getGigyaJsInfoFromBody(bodyParameterMap);
		}
		catch (final IOException e)
		{
			LOG.error("Error parsing Response from Gigya." + e);
			gigyaResponse.setCode(500);
			gigyaResponse.setResult(ERROR);
			gigyaResponse.setMessage("Error updating profile information.");
			return gigyaResponse;
		}
		final CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
		final boolean profileProcessed = gigyaLoginFacade.processGigyaProfileUpdate(jsInfo, cmsSite.getGigyaConfig());
		if (profileProcessed)
		{
			if (BooleanUtils.isTrue(sessionService.getAttribute("emailUpdated")))
			{
				final String newUid = customerFacade.getCurrentCustomer().getUid().toLowerCase();
				final Authentication oldAuthentication = SecurityContextHolder.getContext().getAuthentication();
				final UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newUid, null,
						oldAuthentication.getAuthorities());
				newAuthentication.setDetails(oldAuthentication.getDetails());
				SecurityContextHolder.getContext().setAuthentication(newAuthentication);
			}
			gigyaResponse.setCode(0);
			gigyaResponse.setResult("success");
			gigyaResponse.setMessage("User profile updated successfully.");
			return gigyaResponse;
		}
		else
		{
			gigyaResponse.setCode(500);
			gigyaResponse.setResult(ERROR);
			gigyaResponse.setMessage("Error updating user profile. Please contact support.");
			return gigyaResponse;
		}



	}

	/**
	 * Get GigyaJsInfo from body
	 *
	 * @param bodyParameterMap
	 * @return GigyaJsOnLoginInfo
	 * @throws IOException
	 */
	private GigyaJsOnLoginInfo getGigyaJsInfoFromBody(final MultiValueMap<String, String> bodyParameterMap) throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		return mapper.readValue(bodyParameterMap.getFirst("gigyaData"), GigyaJsOnLoginInfo.class);
	}
}
