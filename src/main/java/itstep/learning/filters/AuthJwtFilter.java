package itstep.learning.filters;

import java.io.IOException;
import java.util.Base64;

import itstep.learning.dal.dao.DataContext;
import itstep.learning.dal.dto.UserAccess;
import itstep.learning.services.hash.HashService;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Singleton
public class AuthJwtFilter implements Filter {
    private FilterConfig filterConfig;
    private final HashService hashService;

    @Inject
    public AuthJwtFilter(HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain next)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sreq;
        checkJwt(req);
        next.doFilter(req, sresp);
    }

    @Override
    public void destroy() {
        this.filterConfig = null;
    }

    private void checkJwt(HttpServletRequest req) {
        String secret = "secret";
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null) {
            req.setAttribute("AuthStatus", "Authorization header required");
            return;

        }

        String authScheme = "Bearer ";

        if (!authHeader.startsWith(authScheme)) {
            req.setAttribute("AuthStatus", "Authorization cheme error ");
            return;

        }

        String credentials = authHeader.substring(authScheme.length());

        String[] parts = credentials.split("\\.");

        if (parts.length != 3) {
            req.setAttribute("AuthStatus", "Token format invalid");
            return;
        }

        String header = parts[0];
        String payload = parts[1];
        String signature = new String(Base64.getUrlDecoder().decode(parts[2]));
        if (!signature.equals(hashService.digest(secret + header + "." + payload))) {

            req.setAttribute("AuthStatus", "Token signuture error");
            return;
        }
        payload = new String(Base64.getUrlDecoder().decode(payload));
        UserAccess userAccess = new Gson().fromJson(payload, UserAccess.class);
        req.setAttribute("AuthStatus", "OK");
        req.setAttribute("AuthUserAccess", userAccess);

    }
}
