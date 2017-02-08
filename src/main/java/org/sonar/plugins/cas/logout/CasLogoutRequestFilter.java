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

import java.util.Map;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.sonar.api.config.Settings;
import org.sonar.plugins.cas.util.AbstractCasFilter;

/**
 * This filter will handle logout request coming from CAS
 *
 * @author Guillaume Lamirand
 */
public class CasLogoutRequestFilter extends AbstractCasFilter {

  public CasLogoutRequestFilter(final Settings pSettings) {
    super(pSettings, new SingleSignOutFilter());
  }

  @Override
  public UrlPattern doGetPattern() {
    return UrlPattern.create("/cas/validate");
  }

  @Override
  protected void doCompleteProperties(final Settings settings, final Map<String, String> properties) {
    if (PROTOCOL_SAML11.equals(settings.getString(PROPERTY_PROTOCOL))) {
      properties.put("artifactParameterName", "SAMLart");
    }
  }

}
