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
package net.rptools.maptool.client.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.Zone;
import net.rptools.maptool.util.FunctionUtil;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.VariableResolver;
import net.rptools.parser.function.AbstractFunction;

public class MapFunctions extends AbstractFunction {
  private static final MapFunctions instance = new MapFunctions();

  private MapFunctions() {
    super(
        0,
        2,
        "getAllMapNames",
        "getAllMapDisplayNames",
        "getCurrentMapName",
        "getMapDisplayName",
        "getVisibleMapNames",
        "getVisibleMapDisplayNames",
        "setCurrentMap",
        "getMapVisible",
        "setMapVisible",
        "setMapName",
        "setMapDisplayName",
        "copyMap",
        "getMapName",
        "setMapSelectButton");
  }

  public static MapFunctions getInstance() {
    return instance;
  }

  @Override
  public Object childEvaluate(
      Parser parser, VariableResolver resolver, String functionName, List<Object> parameters)
      throws ParserException {
    if (functionName.equalsIgnoreCase("getCurrentMapName")) {
      FunctionUtil.checkNumberParam(functionName, parameters, 0, 0);
      ZoneRenderer currentZR = MapTool.getFrame().getCurrentZoneRenderer();
      if (currentZR == null) {
        throw new ParserException(I18N.getText("macro.function.map.none", functionName));
      } else {
        return currentZR.getZone().getName();
      }
    } else if (functionName.equalsIgnoreCase("getMapDisplayName")) {
      FunctionUtil.checkNumberParam(functionName, parameters, 0, 1);
      if (parameters.size() == 0) {
        ZoneRenderer currentZR = MapTool.getFrame().getCurrentZoneRenderer();
        if (currentZR == null) {
          throw new ParserException(I18N.getText("macro.function.map.none", functionName));
        } else {
          return currentZR.getZone().getPlayerAlias();
        }
      } else {
        List<ZoneRenderer> rendererList =
            new LinkedList<ZoneRenderer>(
                MapTool.getFrame().getZoneRenderers()); // copied from ZoneSelectionPopup
        if (!MapTool.getPlayer().isGM()) {
          rendererList.removeIf(renderer -> !renderer.getZone().isVisible());
        }
        String searchMap = parameters.get(0).toString();
        String foundMap = null;
        for (int i = 0; i < rendererList.size(); i++) {
          if (rendererList.get(i).getZone().getName().equals(searchMap)) {
            foundMap = rendererList.get(i).getZone().getPlayerAlias();
            break;
          }
        }
        if (foundMap == null) {
          throw new ParserException(I18N.getText("macro.function.map.notFound", functionName));
        } else {
          return foundMap;
        }
      }
    } else if (functionName.equalsIgnoreCase("setCurrentMap")) {
      checkTrusted(functionName);
      FunctionUtil.checkNumberParam(functionName, parameters, 1, 1);
      String mapName = parameters.get(0).toString();
      ZoneRenderer zr = getNamedMap(functionName, mapName);
      MapTool.getFrame().setCurrentZoneRenderer(zr);
      return mapName;
    } else if ("getMapVisible".equalsIgnoreCase(functionName)) {
      FunctionUtil.checkNumberParam(functionName, parameters, 0, 1);
      if (parameters.size() > 0) {
        String mapName = parameters.get(0).toString();
        return getNamedMap(functionName, mapName).getZone().isVisible()
            ? BigDecimal.ONE
            : BigDecimal.ZERO;
      } else {
        // Return the visibility of the current map/zone
        ZoneRenderer currentZR = MapTool.getFrame().getCurrentZoneRenderer();
        if (currentZR == null) {
          throw new ParserException(I18N.getText("macro.function.map.none", functionName));
        } else {
          return currentZR.getZone().isVisible() ? BigDecimal.ONE : BigDecimal.ZERO;
        }
      }
    } else if ("setMapVisible".equalsIgnoreCase(functionName)) {
      checkTrusted(functionName);
      FunctionUtil.checkNumberParam(functionName, parameters, 1, 2);
      boolean visible = FunctionUtil.getBooleanValue(parameters.get(0).toString());
      Zone zone;
      if (parameters.size() > 1) {
        String mapName = parameters.get(1).toString();
        zone = getNamedMap(functionName, mapName).getZone();
      } else {
        ZoneRenderer currentZR = MapTool.getFrame().getCurrentZoneRenderer();
        if (currentZR == null) {
          throw new ParserException(I18N.getText("macro.function.map.none", functionName));
        } else {
          zone = currentZR.getZone();
        }
      }
      // Set the zone and return the visibility of the current map/zone
      zone.setVisible(visible);
      MapTool.serverCommand().setZoneVisibility(zone.getId(), zone.isVisible());
      MapTool.getFrame().getZoneMiniMapPanel().flush();
      MapTool.getFrame().repaint();
      return zone.isVisible() ? BigDecimal.ONE : BigDecimal.ZERO;

    } else if ("setMapName".equalsIgnoreCase(functionName)) {
      checkTrusted(functionName);
      FunctionUtil.checkNumberParam(functionName, parameters, 2, 2);
      String oldMapName = parameters.get(0).toString();
      String newMapName = parameters.get(1).toString();
      Zone zone = getNamedMap(functionName, oldMapName).getZone();
      zone.setName(newMapName);
      MapTool.serverCommand().renameZone(zone.getId(), newMapName);
      if (zone == MapTool.getFrame().getCurrentZoneRenderer().getZone())
        MapTool.getFrame().setCurrentZoneRenderer(MapTool.getFrame().getCurrentZoneRenderer());
      return zone.getName();

    } else if ("setMapDisplayName".equalsIgnoreCase(functionName)) {
      checkTrusted(functionName);
      FunctionUtil.checkNumberParam(functionName, parameters, 2, 2);
      String mapName = parameters.get(0).toString();
      String newMapDisplayName = parameters.get(1).toString();
      Zone zone = getNamedMap(functionName, mapName).getZone();
      String oldName;
      oldName = zone.getPlayerAlias();
      zone.setPlayerAlias(newMapDisplayName);
      if (oldName.equals(newMapDisplayName)) return zone.getPlayerAlias();
      MapTool.serverCommand().changeZoneDispName(zone.getId(), newMapDisplayName);
      if (zone == MapTool.getFrame().getCurrentZoneRenderer().getZone())
        MapTool.getFrame().setCurrentZoneRenderer(MapTool.getFrame().getCurrentZoneRenderer());
      if (oldName.equals(zone.getPlayerAlias()))
        throw new ParserException(
            I18N.getText("macro.function.map.duplicateDisplay", functionName));
      return zone.getPlayerAlias();

    } else if ("copyMap".equalsIgnoreCase(functionName)) {
      checkTrusted(functionName);
      FunctionUtil.checkNumberParam(functionName, parameters, 2, 2);
      String oldName = parameters.get(0).toString();
      String newName = parameters.get(1).toString();
      Zone oldMap = getNamedMap(functionName, oldName).getZone();
      Zone newMap = new Zone(oldMap);
      newMap.setName(newName);
      MapTool.addZone(newMap, false);
      MapTool.serverCommand().putZone(newMap);
      return newMap.getName();

    } else if ("getVisibleMapNames".equalsIgnoreCase(functionName)
        || "getAllMapNames".equalsIgnoreCase(functionName)) {
      FunctionUtil.checkNumberParam(functionName, parameters, 0, 1);
      boolean allMaps = functionName.equalsIgnoreCase("getAllMapNames");

      if (allMaps) checkTrusted(functionName);

      List<String> mapNames = new LinkedList<String>();
      for (ZoneRenderer zr : MapTool.getFrame().getZoneRenderers()) {
        if (allMaps || zr.getZone().isVisible()) {
          mapNames.add(zr.getZone().getName());
        }
      }
      String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
      if ("json".equals(delim)) {
        JsonArray jarr = new JsonArray();
        mapNames.forEach(m -> jarr.add(new JsonPrimitive(m)));
        return jarr;
      } else {
        return StringFunctions.getInstance().join(mapNames, delim);
      }

    } else if ("getVisibleMapDisplayNames".equalsIgnoreCase(functionName)
        || "getAllMapDisplayNames".equalsIgnoreCase(functionName)) {
      FunctionUtil.checkNumberParam(functionName, parameters, 0, 1);
      boolean allMaps = functionName.equalsIgnoreCase("getAllMapDisplayNames");

      if (allMaps) checkTrusted(functionName);

      List<String> mapNames = new LinkedList<String>();
      for (ZoneRenderer zr : MapTool.getFrame().getZoneRenderers()) {
        if (allMaps || zr.getZone().isVisible()) {
          mapNames.add(zr.getZone().getPlayerAlias());
        }
      }
      String delim = parameters.size() > 0 ? parameters.get(0).toString() : ",";
      if ("json".equals(delim)) {
        JsonArray jarr = new JsonArray();
        mapNames.forEach(m -> jarr.add(new JsonPrimitive(m)));
        return jarr;
      } else {
        return StringFunctions.getInstance().join(mapNames, delim);
      }
    } else if ("getMapName".equalsIgnoreCase(functionName)) {
      FunctionUtil.checkNumberParam(functionName, parameters, 1, 1);
      String dispName = parameters.get(0).toString();
      checkTrusted(functionName);

      for (ZoneRenderer zr : MapTool.getFrame().getZoneRenderers()) {
        if (zr.getZone().getPlayerAlias().equals(dispName)) {
          return zr.getZone().getName();
        }
      }
      throw new ParserException(I18N.getText("macro.function.map.notFound", functionName));
    } else if ("setMapSelectButton".equalsIgnoreCase(functionName)) {
      // this is kind of a map function? :)
      checkTrusted(functionName);
      FunctionUtil.checkNumberParam(functionName, parameters, 1, 1);
      boolean vis = !parameters.get(0).toString().equals("0");
      if (MapTool.getFrame().getFullsZoneButton() != null)
        MapTool.getFrame().getFullsZoneButton().setVisible(vis);
      MapTool.getFrame().getToolbarPanel().getMapselect().setVisible(vis);
      return (MapTool.getFrame().getToolbarPanel().getMapselect().isVisible()
          ? BigDecimal.ONE
          : BigDecimal.ZERO);
    }
    throw new ParserException(I18N.getText("macro.function.general.unknownFunction", functionName));
  }

  /**
   * Find the map/zone for a given map name
   *
   * @param functionName String Name of the calling function.
   * @param mapName String Name of the searched for map.
   * @return ZoneRenderer The map/zone.
   * @throws ParserException if the map is not found
   */
  private ZoneRenderer getNamedMap(final String functionName, final String mapName)
      throws ParserException {
    ZoneRenderer zr = MapTool.getFrame().getZoneRenderer(mapName);

    if (zr != null) return zr;

    throw new ParserException(
        I18N.getText("macro.function.moveTokenMap.unknownMap", functionName, mapName));
  }

  /**
   * Checks whether or not the function is trusted
   *
   * @param functionName Name of the macro function
   * @throws ParserException Returns trust error message and function name
   */
  private void checkTrusted(String functionName) throws ParserException {
    if (!MapTool.getParser().isMacroTrusted()) {
      throw new ParserException(I18N.getText("macro.function.general.noPerm", functionName));
    }
  }
}
