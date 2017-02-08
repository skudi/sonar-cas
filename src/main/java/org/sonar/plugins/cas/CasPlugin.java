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

package org.sonar.plugins.cas;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.ExtensionProvider;
import org.sonar.api.ServerExtension;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.Settings;
import org.sonar.plugins.cas.cas1.Cas1AuthenticationFilter;
import org.sonar.plugins.cas.cas1.Cas1ValidationFilter;
import org.sonar.plugins.cas.cas2.Cas2AuthenticationFilter;
import org.sonar.plugins.cas.cas2.Cas2ValidationFilter;
import org.sonar.plugins.cas.logout.CasLogoutRequestFilter;
import org.sonar.plugins.cas.logout.SonarLogoutRequestFilter;
import org.sonar.plugins.cas.util.CasPluginConstants;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class CasPlugin extends SonarPlugin {

  public List getExtensions() {
    return ImmutableList.of(CasExtensions.class);
  }

  public static final class CasExtensions extends ExtensionProvider implements ServerExtension, CasPluginConstants {
    private Settings settings;

    public CasExtensions(Settings settings) {
      this.settings = settings;
    }

    @Override
    public Object provide() {
      List<Class> extensions = Lists.newArrayList();
      if (isRealmEnabled()) {
        Preconditions.checkState(settings.getBoolean(PROPERTY_CREATE_USERS), "Property sonar.authenticator.createUsers must be set to true.");
        String protocol = settings.getString(PROPERTY_PROTOCOL);
        Preconditions.checkState(!Strings.isNullOrEmpty(protocol), "Missing CAS protocol. Values are: cas1, cas2 or saml11.");

        extensions.add(CasSecurityRealm.class);

        if (StringUtils.isNotBlank(settings.getString(PROPERTY_CAS_LOGOUT_URL))) {
          extensions.add(CasLogoutRequestFilter.class);
          extensions.add(SonarLogoutRequestFilter.class);
        }

        if (PROTOCOL_CAS1.equals(protocol)) {
          extensions.add(Cas1AuthenticationFilter.class);
          extensions.add(Cas1ValidationFilter.class);
        } else if (PROTOCOL_CAS2.equals(protocol)) {
          extensions.add(Cas2AuthenticationFilter.class);
          extensions.add(Cas2ValidationFilter.class);
        } else if (PROTOCOL_SAML11.equals(protocol)) {
          throw new IllegalStateException("Usupported CAS protocol: " + protocol + ". Valid values are: cas1, cas2.");
        } else {
          throw new IllegalStateException("Unknown CAS protocol: " + protocol + ". Valid values are: cas1, cas2.");
        }

      }
      return extensions;
    }

    private boolean isRealmEnabled() {
      return CasSecurityRealm.KEY.equalsIgnoreCase(settings.getString(PROPERTY_SECURITY_REALM));
    }
  }

}
