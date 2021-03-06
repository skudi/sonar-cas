/*
 * Sonar CAS Plugin
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.cas.logout;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.web.ServletFilter;
import org.sonar.plugins.cas.util.CasPluginConstants;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * This filter will handle logout request coming from sonar UI, in order to redirect to CAS logout page.
 *
 * @author Guillaume Lamirand
 */
public class SonarLogoutRequestFilter extends ServletFilter implements CasPluginConstants {

  private final Settings settings;
  private String logoutUrl;

  public SonarLogoutRequestFilter(final Settings settings) {
    this.settings = settings;
  }

  @Override
  public UrlPattern doGetPattern() {
    return UrlPattern.create("/sessions/logout");
  }

  public final void init(final FilterConfig initialConfig) throws ServletException {
    logoutUrl = settings.getString(PROPERTY_CAS_LOGOUT_URL);
    Preconditions.checkState(!Strings.isNullOrEmpty(logoutUrl), String.format("Missing property: %s", PROPERTY_CAS_LOGOUT_URL));
    if (Boolean.parseBoolean(StringUtils.defaultIfBlank(settings.getString(PROPERTY_LOGOUT_REDIRECT), "true"))) {
      try {
        logoutUrl += "?service=" + URLEncoder.encode(settings.getString(PROPERTY_SONAR_SERVER_URL), "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
    HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
    httpResponse.sendRedirect(logoutUrl);
  }

  public final void destroy() {
    logoutUrl = null;
  }

}
