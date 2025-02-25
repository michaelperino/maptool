/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.clientserver.hessian;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class HessianSecurity {
  private final Set<String> allowed;
  private final Set<String> denied;

  public HessianSecurity() {

    Set<String> allow = new HashSet<>();
    // Safe java.lang stuff
    allow.add("java.lang.Boolean");
    allow.add("java.lang.Byte");
    allow.add("java.lang.Character");
    allow.add("java.lang.Double");
    allow.add("java.lang.Float");
    allow.add("java.lang.Long");
    allow.add("java.lang.Short");
    allow.add("java.lang.String");
    allow.add("java.lang.Integer");
    allow.add("java.lang.BigDecimal");

    allow.add("java.util.ArrayList");
    allow.add("java.util.Arrays");
    allow.add("java.util.Base64");
    allow.add("java.util.BitSet");
    allow.add("java.util.EnumMap");
    allow.add("java.util.EnumSet");
    allow.add("java.util.HashMap");
    allow.add("java.util.HashSet");
    allow.add("java.util.LinkedHashMap");
    allow.add("java.util.LinkedHashSet");
    allow.add("java.util.LinkedList");
    allow.add("java.util.TreeMap");
    allow.add("java.util.TreeSet");
    allow.add("java.util.UUID");

    allow.add("java.awt.*");
    allow.add("java.awt.geom.*");
    allow.add("java.awt.Point");
    allow.add("sun.awt.geom.*");
    allow.add("java.awt.BasicStroke");

    allow.add("net.rptools.maptool.client.walker.*");
    allow.add("net.rptools.maptool.common.*");
    allow.add("net.rptools.maptool.model.*");
    allow.add("net.rptools.maptool.client.ui.*");
    allow.add("net.rptools.CaseInsensitiveHashMap");
    allow.add("net.rptools.*");

    allow.add("net.rptools.lib.MD5Key");

    Set<String> deny = new HashSet<>();
    deny.add("*");

    allowed = Collections.unmodifiableSet(allow);

    denied = Collections.unmodifiableSet(deny);
  }

  public Collection<String> getAllowed() {
    return allowed;
  }

  public Collection<String> getDenied() {
    return denied;
  }
}
