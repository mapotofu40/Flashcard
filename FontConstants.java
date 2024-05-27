package memoryToast;
import java.awt.Font;
import javax.swing.UIManager;
public class FontConstants {
	public static final Font labelFont = new Font(UIManager.getDefaults().getFont("TabbedPane.font").getFamily(),
            Font.PLAIN, 14);
    public static final Font textAreaFont = new Font(UIManager.getDefaults().getFont("TabbedPane.font").getFamily(),
            Font.PLAIN, 16);
}
