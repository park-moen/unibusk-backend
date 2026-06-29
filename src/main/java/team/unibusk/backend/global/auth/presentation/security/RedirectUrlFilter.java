package team.unibusk.backend.global.auth.presentation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import team.unibusk.backend.global.jwt.injector.TokenInjector;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
public class RedirectUrlFilter extends OncePerRequestFilter {

    private final TokenInjector tokenInjector;

    public static final String STATE_PARAM = "state";
    public static final String STATE_COOKIE_NAME = "oauth_state";

    private static final List<String> REDIRECT_URL_INJECTION_PATTERNS = List.of(
            "/api/auths/login"
    );

    private static final List<String> ALLOWED_REDIRECT_HOSTS = List.of(
            "localhost",
            "unibusk.site",
            "www.unibusk.site",
            "dev.unibusk.site",
            "unibusk.xyz",
            "www.unibusk.xyz"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        if (pathMatcher.match("/api/auths/login", requestUri)) {

            String state = request.getParameter(STATE_PARAM);

            tokenInjector.invalidateCookie(STATE_COOKIE_NAME, response);

            if (StringUtils.hasText(state) && isValidRedirectUrl(state)) {
                String encodedState = URLEncoder.encode(state, StandardCharsets.UTF_8);

                tokenInjector.addCookie(
                        STATE_COOKIE_NAME,
                        encodedState,
                        3600,
                        response
                );
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRedirectRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return REDIRECT_URL_INJECTION_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }

    private boolean isValidRedirectUrl(String url) {
        try {
            URI uri = URI.create(url);

            if (!uri.isAbsolute()) {
                return false;
            }

            return ALLOWED_REDIRECT_HOSTS.contains(uri.getHost());
        } catch (Exception e) {
            return false;
        }
    }

}
