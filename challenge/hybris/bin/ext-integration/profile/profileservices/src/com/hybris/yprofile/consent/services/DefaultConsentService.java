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
package com.hybris.yprofile.consent.services;


import com.hybris.charon.RawResponse;
import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.consent.cookie.EnhancedCookieGenerator;
import com.hybris.yprofile.constants.ProfileservicesConstants;
import com.hybris.yprofile.dto.cookie.ProfileConsentCookie;
import com.hybris.yprofile.rest.clients.ConsentServiceClient;
import com.hybris.yprofile.services.ProfileConfigurationService;
import com.hybris.yprofile.services.RetrieveRestClientStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.*;

import static com.hybris.yprofile.constants.ProfileservicesConstants.*;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Implementation for {@link ConsentService}. Service is responsible to generate and provide the consent reference.
 */
public class DefaultConsentService implements ConsentService {
    private static final Logger LOG = Logger.getLogger(DefaultConsentService.class);

    private static final String CONSENT_REFERENCE_SESSION_ATTR_KEY = "consent-reference";
    private static final String CONSENT_REFERENCE_COOKIE_NAME_SUFFIX = "-consentReference";
    private static final String NULL = "null";

    private EnhancedCookieGenerator cookieGenerator;

    private SessionService sessionService;

    private UserService userService;

    private BaseSiteService baseSiteService;

    private ModelService modelService;

    private RetrieveRestClientStrategy retrieveRestClientStrategy;

    private ProfileConfigurationService profileConfigurationService;

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String UTF_8 = "UTF-8";


    @Override
    public boolean isProfileTrackingConsentGiven(final HttpServletRequest request){
        if (isAnonymousUser()) {
            return isProfileTrackingConsentGivenForAnonymousUser(Utils.getCookie(request, ANONYMOUS_CONSENTS));
        } else {
            return isProfileTrackingConsentGivenForLoggedInUser();
        }
    }

    protected boolean isProfileTrackingConsentGivenForLoggedInUser() {
        return profileTrackingConsentForLoggedInUser(ProfileservicesConstants.CONSENT_GIVEN);
    }

    protected boolean profileTrackingConsentForLoggedInUser(final String consent) {
        boolean track = false;

        try {
            final Map<String, String> userConsents = getSessionService().getAttribute(USER_CONSENTS);
            if(userConsents != null
                    && userConsents.containsKey(PROFILE_CONSENT)
                    && consent.equals(userConsents.get(PROFILE_CONSENT))) {
                track = true;
            }
        }catch(Exception ex)
        {
            LOG.warn("Error while processing user consents", ex);
        }
        return track;
    }

    protected boolean isAnonymousUser() {
        return (getUserService().isAnonymousUser(getUserService().getCurrentUser())
                || isUserSoftLoggedIn()
                || getSessionService().getAttribute(USER_CONSENTS) == null);
    }

    protected boolean isUserSoftLoggedIn(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication instanceof RememberMeAuthenticationToken);
    }

    protected boolean isProfileTrackingConsentGivenForAnonymousUser(final Optional<Cookie> anonymousConsentCookie) {

        boolean track = false;

        try {
            if (anonymousConsentCookie.isPresent()) {
                final List<ProfileConsentCookie> profileConsentCookieList;

                profileConsentCookieList = new ArrayList(Arrays
                        .asList(mapper.readValue(URLDecoder.decode(anonymousConsentCookie.get().getValue(), UTF_8), ProfileConsentCookie[].class)));

                track = profileConsentCookieList.stream().filter(x-> PROFILE_CONSENT.equals(x.getTemplateCode()))
                        .map(k-> ProfileservicesConstants.CONSENT_GIVEN.equals(k.getConsentState())).reduce(true, (x, y)-> x && y);
            }

        }catch (Exception ex){
            LOG.warn("Error while processing anonymous consents", ex);
        }
        return track;
    }

    @Override
    public void saveConsentReferenceInSessionAndCurrentUserModel(final HttpServletRequest request) {
        final String consentReferenceId = getConsentReferenceFromCookie(getSiteId(), request);

        setAttributeInSession(CONSENT_REFERENCE_SESSION_ATTR_KEY, consentReferenceId);

        setConsentReferenceForCurrentUser(consentReferenceId);
    }



    @Override
    public String getConsentReferenceFromCookie(final String siteId, final HttpServletRequest request) {

        final String consentReferenceCookieName = siteId + CONSENT_REFERENCE_COOKIE_NAME_SUFFIX;
        final Optional<Cookie> cookie = Utils.getCookie(request, consentReferenceCookieName);
        if (cookie.isPresent()) {
            return cookie.get().getValue();
        }
        return null;
    }

    @Override
    public String getConsentReferenceFromSession(){
        if (getSessionService().getAttribute(PROFILE_CONSENT_GIVEN) != null &&
                Boolean.TRUE.equals(getSessionService().getAttribute(PROFILE_CONSENT_GIVEN)) ){
            return getSessionService().getAttribute(CONSENT_REFERENCE_SESSION_ATTR_KEY);
        }

        return null;
    }

    @Override
    public void setProfileConsentCookieAndSession(final HttpServletRequest request, final HttpServletResponse response, final boolean consent){

        final Optional<Cookie> cookie = Utils.getCookie(request, PROFILE_CONSENT_GIVEN);

        setAttributeInSession(PROFILE_CONSENT_GIVEN, consent);

        if (!cookie.isPresent()) {
            Utils.setCookie(cookieGenerator, response, PROFILE_CONSENT_GIVEN, Boolean.toString(consent));
            return;
        }

        if (!cookie.get().getValue().equals(Boolean.toString(consent))){
            Utils.setCookie(cookieGenerator, response, PROFILE_CONSENT_GIVEN, Boolean.toString(consent));
            return;
        }
    }

    @Override
    public void removeConsentReferenceCookieAndSession(final HttpServletResponse response) {

        try {
            final String consentReferenceCookieName = getSiteId() + CONSENT_REFERENCE_COOKIE_NAME_SUFFIX;
            Utils.removeCookie(getCookieGenerator(), response, consentReferenceCookieName);
            setAttributeInSession(CONSENT_REFERENCE_SESSION_ATTR_KEY, null);
        } catch (Exception e) {
            LOG.warn("Error removing consent reference cookie", e);
        }
    }

    @Override
    public void deleteConsentReferenceInConsentServiceAndInUserModel(final UserModel userModel, final String baseSiteId) {

        final String consentReference = userModel.getConsentReference();

        if(!shouldSendEvent(consentReference, baseSiteId)) {
            LOG.warn("YaaS Configuration not found");
            return;
        }

        resetConsentReferenceForUser(userModel);
        final String debugEnabled = getDebugFlagValue(userModel);

        getClient().deleteConsentReference(consentReference, debugEnabled)
                .subscribe(response -> logSuccess(Optional.ofNullable(response), "Delete Consent Reference Request sent to yprofile"),
                        error -> logError(error),
                        () -> logSuccess(Optional.empty(), "Delete Consent Reference Request sent to yprofile"));
    }

    protected static String getDebugFlagValue(final UserModel userModel){
        if(Boolean.TRUE.equals(userModel.getProfileTagDebug())){
            return "1";
        }
        return "0";
    }

    protected boolean shouldSendEvent(final String consentReference, final String baseSiteId) {
        return !getProfileConfigurationService().isProfileTrackingPaused()
                && getProfileConfigurationService().isConfigurationPresent()
                && isValidConsentReference(consentReference);
    }

    protected static boolean isValidConsentReference(String consentReferenceId) {
        return !isBlank(consentReferenceId) && !NULL.equals(consentReferenceId);
    }

    protected void setAttributeInSession(final String key, final Object value){

        if (value == null){
            return;
        }

        try {
            getSessionService().setAttribute(key, value);

        } catch (Exception e) {
            LOG.warn("Error setting " + key + " in session", e);
        }

    }

    protected String getSiteId(){
        return getCurrentBaseSiteModel().isPresent() ? getCurrentBaseSiteModel().get().getUid() : StringUtils.EMPTY;
    }

    protected Optional<BaseSiteModel> getCurrentBaseSiteModel() {
        return ofNullable(getBaseSiteService().getCurrentBaseSite());
    }

    protected static void logSuccess(final Optional<RawResponse> rawResponse, final String message){
        LOG.debug(message);

        rawResponse.ifPresent(response ->  {
            final int statusCode = response.getStatusCode();
            final Optional<String> optionalContext = response.header("hybris-context-trace-id");

            optionalContext.
                    ifPresent(contextTraceId -> LOG.debug("Event sent to yprofile. " +
                            "With Status: " + statusCode + " and " +
                            "Context-Trace-ID: " + contextTraceId));
        });
    }

    protected static void logError(Throwable error){
        LOG.error("Error sending request to consent service", error);
    }

    protected ConsentServiceClient getClient() {
        return getRetrieveRestClientStrategy().getConsentServiceRestClient();
    }

    protected EnhancedCookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(EnhancedCookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    public RetrieveRestClientStrategy getRetrieveRestClientStrategy() {
        return retrieveRestClientStrategy;
    }

    @Required
    public void setRetrieveRestClientStrategy(RetrieveRestClientStrategy retrieveRestClientStrategy) {
        this.retrieveRestClientStrategy = retrieveRestClientStrategy;
    }

    public BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }


    public ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    protected void setConsentReferenceForCurrentUser(String consentReferenceId) {
        final UserModel user = getUserService().getCurrentUser();

        if (!getUserService().isAnonymousUser(user) &&
                (isBlank(user.getConsentReference()) ||
                        !user.getConsentReference().equals(consentReferenceId))){
            user.setConsentReference(consentReferenceId);
            user.setProfileTagDebug(false);
            getModelService().save(user);
            getModelService().refresh(user);
        }
    }

    protected void resetConsentReferenceForUser(final UserModel user) {
        try {

            user.setConsentReference(null);
            getModelService().save(user);
            getModelService().refresh(user);

        } catch (Exception e) {
            LOG.warn("Error resetting the consent reference", e);
        }
    }
}

