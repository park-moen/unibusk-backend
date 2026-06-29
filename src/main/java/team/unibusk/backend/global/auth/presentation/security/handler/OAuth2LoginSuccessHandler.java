package team.unibusk.backend.global.auth.presentation.security.handler;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import team.unibusk.backend.global.auth.application.auth.AuthService;
import team.unibusk.backend.global.auth.domain.user.CustomOAuth2User;
import static team.unibusk.backend.global.auth.presentation.security.RedirectUrlFilter.STATE_COOKIE_NAME;
import team.unibusk.backend.global.jwt.config.SecurityProperties;
import team.unibusk.backend.global.jwt.injector.TokenInjector;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final TokenInjector tokenInjector;
    private final SecurityProperties securityProperties;

    private static final List<String> ALLOWED_REDIRECT_HOSTS = List.of(
            "localhost",
            "unibusk.site",
            "www.unibusk.site",
            "dev.unibusk.site",
            "unibusk.xyz",
            "www.unibusk.xyz"
    );

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();

        String code = authService.generateAuthCode(user.getAuthAttributes());

        redirectWithCode(request, response, code);
    }

    private void redirectWithCode(
            HttpServletRequest request,
            HttpServletResponse response,
            String code
    ) throws IOException {

        String stateCookieValue = getStateCookie(request);

        String target = determineTargetUrl(stateCookieValue);

        tokenInjector.invalidateCookie(STATE_COOKIE_NAME, response);

        String redirectUrl = UriComponentsBuilder.fromUriString(target)
                .queryParam("code", code)
                .build()
                .encode()
                .toUriString();
        response.sendRedirect(redirectUrl);
    }

    private String getStateCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        return Arrays.stream(cookies)
                .filter(cookie -> Objects.equals(cookie.getName(), STATE_COOKIE_NAME))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private String determineTargetUrl(String cookieValue) {
        if (StringUtils.hasText(cookieValue)) {
            String decoded = URLDecoder.decode(cookieValue, StandardCharsets.UTF_8);
            log.info("[OAuth2Success] decoded state={}", decoded);

            if (isValidRedirectUrl(decoded)) {
                return decoded;
            }
        }
        log.warn("[OAuth2Success] fallback redirect used");
        return securityProperties.oAuthUrl().redirectUrl();
    }

    private boolean isValidRedirectUrl(String url) {
        try {
            URI uri = URI.create(url);

            if (uri.getHost() == null) {
                return true;
            }

            return ALLOWED_REDIRECT_HOSTS.contains(uri.getHost());
        } catch (Exception e) {
            log.warn("[OAuth2Success] invalid redirect url={}", url);
            return false;
        }
    }

}
