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
package com.hybris.yprofile.consent.filters;

import com.hybris.yprofile.common.Utils;
import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;

import static com.hybris.yprofile.constants.ProfileservicesConstants.PROFILE_TRACKING_PAUSE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Generates the consent reference for the user and stores it in a cookie and in session
 */
public class ConsentLayerFilter extends OncePerRequestFilter
{

    private static final Logger LOG = Logger.getLogger(ConsentLayerFilter.class);
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private ConsentService consentService;
    private SessionService sessionService;
    private boolean enabled;
    private String excludeUrlPatterns;
    private UserService userService;

    private ProfileConfigurationService profileConfigurationService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        if (isProfileTrackingPaused(httpServletRequest)) {
            LOG.debug("Profile tracking disabled");
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (isProfileTrackingConsentGiven(httpServletRequest, httpServletResponse) && isActiveAccount()){
            LOG.debug("Profile tracking consent given");
            getConsentService().saveConsentReferenceInSessionAndCurrentUserModel(httpServletRequest);
        } else {
            LOG.debug("Profile tracking consent withdrawn | account is deactivated");
            getConsentService().removeConsentReferenceCookieAndSession(httpServletResponse);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    protected boolean isProfileTrackingPaused(HttpServletRequest httpServletRequest) {

        final Optional<Cookie> pauseProfileTrackingCookie = Utils.getCookie(httpServletRequest, PROFILE_TRACKING_PAUSE);
        getProfileConfigurationService().setProfileTrackingPauseValue(pauseProfileTrackingCookie.isPresent());

        return  pauseProfileTrackingCookie.isPresent();
    }

    protected boolean isProfileTrackingConsentGiven(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        final boolean profileTrackingConsentGiven = getConsentService().isProfileTrackingConsentGiven(httpServletRequest);
        getConsentService().setProfileConsentCookieAndSession(httpServletRequest, httpServletResponse, profileTrackingConsentGiven);

        return profileTrackingConsentGiven;
    }

    protected boolean isActiveAccount(){
        final UserModel currentUser = getUserService().getCurrentUser();

        return currentUser.getDeactivationDate() == null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        if (isBlank(excludeUrlPatterns)){
            return false;
        }

        final List<String> patterns = Arrays.asList(excludeUrlPatterns.split("\\s*,\\s*"));

        return patterns.stream()
                .anyMatch(p -> (request.getRequestURI() !=null && pathMatcher.match(p, request.getRequestURI()))
                        || (request.getQueryString() !=null && pathMatcher.match(p, request.getQueryString())));
    }

    public ConsentService getConsentService() {
        return consentService;
    }

    @Required
    public void setConsentService(ConsentService consentService) {
        this.consentService = consentService;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Required
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ProfileConfigurationService getProfileConfigurationService() {
        return profileConfigurationService;
    }

    @Required
    public void setProfileConfigurationService(ProfileConfigurationService profileConfigurationService) {
        this.profileConfigurationService = profileConfigurationService;
    }

    @Required
    public void setExcludeUrlPatterns(String excludeUrlPatterns) {
        this.excludeUrlPatterns = excludeUrlPatterns;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
