/*
 * This software Copyright by the RPTools.net development team, and licensed under the Affero GPL Version 3 or, at your option, any later version.
 *
 * MapTool Source Code is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public License * along with this source Code. If not, please visit <http://www.gnu.org/licenses/> and specifically the Affero license text
 * at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.client.macro.impl;

import net.rptools.maptool.client.macro.Macro;
import net.rptools.maptool_fx.MapTool;

public abstract class AbstractMacro implements Macro {
	protected String processText(String incoming) {
		return "\002" + MapTool.getFrame().getCommandPanel().getChatProcessor().process(incoming) + "\003";
	}

	// public static void main(String[] args) {
	// new AbstractMacro(){
	// public void execute(String macro) {
	//
	// System.out.println(getWords(macro));
	// }
	// }.execute("one \"two three\" \"four five\"");
	// }
}
