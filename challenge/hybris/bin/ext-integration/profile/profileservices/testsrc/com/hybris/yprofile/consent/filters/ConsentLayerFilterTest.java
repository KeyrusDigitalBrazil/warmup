package com.hybris.yprofile.consent.filters;

import com.hybris.yprofile.consent.services.ConsentService;
import com.hybris.yprofile.constants.ProfileservicesConstants;
import com.hybris.yprofile.services.ProfileConfigurationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@UnitTest
public class ConsentLayerFilterTest {

    private static final String REQUESTEDURL = "http://mySite.profile.com";
    private static final String SERVLET_PATH = "/c/584";
    private static final String EXCLUDEDURL_PATTERN = "*asm=true*";

    private ConsentLayerFilter consentLayerFilter;

    @Mock
    private ProfileConfigurationService profileConfigurationService;

    @Mock
    private ConsentService consentService;

    @Mock
    private SessionService sessionService;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Cookie cookie;

    @Mock
    private UserModel userModel;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        consentLayerFilter = new ConsentLayerFilter();
        consentLayerFilter.setProfileConfigurationService(profileConfigurationService);
        consentLayerFilter.setConsentService(consentService);
        consentLayerFilter.setEnabled(false);
        consentLayerFilter.setExcludeUrlPatterns(EXCLUDEDURL_PATTERN);
        consentLayerFilter.setSessionService(sessionService);
        consentLayerFilter.setUserService(userService);

        final StringBuffer requestUrlSb = new StringBuffer();
        requestUrlSb.append(REQUESTEDURL);
        when(httpServletRequest.getRequestURL()).thenReturn(requestUrlSb);
        when(httpServletRequest.getRequestURI()).thenReturn(requestUrlSb.toString());
        when(httpServletRequest.getServletPath()).thenReturn(SERVLET_PATH);
    }

    @Test
    public void shouldNotTrackWhenProfileTrackingPaused() throws ServletException, IOException {

        when(cookie.getName()).thenReturn(ProfileservicesConstants.PROFILE_TRACKING_PAUSE);
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});

        consentLayerFilter.doFilterInternal(httpServletRequest,httpServletResponse, filterChain);

        verify(profileConfigurationService, times(1)).setProfileTrackingPauseValue(anyBoolean());
        verify(consentService, never()).saveConsentReferenceInSessionAndCurrentUserModel(any(HttpServletRequest.class));
        verify(consentService, never()).removeConsentReferenceCookieAndSession(any(HttpServletResponse.class));

    }


    @Test
    public void shouldTrackWhenProfileTrackingConsentIsGiven() throws ServletException, IOException {

        when(consentService.isProfileTrackingConsentGiven(httpServletRequest)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userModel.getDeactivationDate()).thenReturn(null);

        consentLayerFilter.doFilterInternal(httpServletRequest,httpServletResponse, filterChain);

        verify(profileConfigurationService, times(1)).setProfileTrackingPauseValue(anyBoolean());
        verify(consentService, times(1)).setProfileConsentCookieAndSession(any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean());
        verify(consentService, times(1)).saveConsentReferenceInSessionAndCurrentUserModel(any(HttpServletRequest.class));

    }


    @Test
    public void shouldNotTrackWhenProfileTrackingConsentIsWithdrawnAndRemoveCookie() throws ServletException, IOException {

        when(consentService.isProfileTrackingConsentGiven(httpServletRequest)).thenReturn(false);

        consentLayerFilter.doFilterInternal(httpServletRequest,httpServletResponse, filterChain);

        verify(profileConfigurationService, times(1)).setProfileTrackingPauseValue(anyBoolean());
        verify(consentService, times(1)).setProfileConsentCookieAndSession(any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean());
        verify(consentService, never()).saveConsentReferenceInSessionAndCurrentUserModel(any(HttpServletRequest.class));
        verify(consentService, times(1)).removeConsentReferenceCookieAndSession(any(HttpServletResponse.class));

    }

    @Test
    public void shouldNotTrackWhenAccountIsDeactivated() throws ServletException, IOException {

        when(consentService.isProfileTrackingConsentGiven(httpServletRequest)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(userModel);
        when(userModel.getDeactivationDate()).thenReturn(mock(Date.class));

        consentLayerFilter.doFilterInternal(httpServletRequest,httpServletResponse, filterChain);

        verify(profileConfigurationService, times(1)).setProfileTrackingPauseValue(anyBoolean());
        verify(consentService, times(1)).setProfileConsentCookieAndSession(any(HttpServletRequest.class), any(HttpServletResponse.class), anyBoolean());
        verify(consentService, never()).saveConsentReferenceInSessionAndCurrentUserModel(any(HttpServletRequest.class));
        verify(consentService, times(1)).removeConsentReferenceCookieAndSession(any(HttpServletResponse.class));

    }
}