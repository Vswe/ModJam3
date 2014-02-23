package vswe.stevesfactory.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import vswe.stevesfactory.Localization;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.CheckBox;
import vswe.stevesfactory.components.CheckBoxList;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.interfaces.IInterfaceRenderer;

@SideOnly(Side.CLIENT)
public class SettingsScreen implements IInterfaceRenderer {

    private TileEntityManager manager;

    public SettingsScreen(TileEntityManager manager) {
        this.manager = manager;

        checkBoxes = new CheckBoxList();
        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.CLOSE_GROUP_LABEL, 10, 20) {
            @Override
            public void setValue(boolean val) {
                Settings.setAutoCloseGroup(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isAutoCloseGroup();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.OPEN_MENU_LARGE_HIT_BOX, 10, 50) {
            @Override
            public void setValue(boolean val) {
                Settings.setLargeOpenHitBox(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isLargeOpenHitBox();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.OPEN_GROUP_QUICK, 10, 80) {
            @Override
            public void setValue(boolean val) {
                Settings.setQuickGroupOpen(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isQuickGroupOpen();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.SHOW_COMMAND_TYPE, 10, 110) {
            @Override
            public void setValue(boolean val) {
                Settings.setCommandTypes(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isCommandTypes();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.AUTO_SIDE, 10, 160) {
            @Override
            public void setValue(boolean val) {
                Settings.setAutoSide(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isAutoSide();
            }
        });

        checkBoxes.addCheckBox(new CheckBoxSetting(Localization.AUTO_BLACK_LIST, 10, 200) {
            @Override
            public void setValue(boolean val) {
                Settings.setAutoBlacklist(val);
            }

            @Override
            public boolean getValue() {
                return Settings.isAutoBlacklist();
            }
        });
    }

    private abstract class CheckBoxSetting extends CheckBox {
        private CheckBoxSetting(Localization name, int x, int y) {
            super(name, x, y);

            setTextWidth(100);
        }

        @Override
        public void onUpdate() {}
    }

    private CheckBoxList checkBoxes;



    @Override
    public void draw(GuiManager gui, int mX, int mY) {
        gui.drawString(Localization.SETTINGS.toString(), 8, 6, 0x404040);
        checkBoxes.draw(gui, mX, mY);
    }

    @Override
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onClick(GuiManager gui, int mX, int mY, int button) {
        checkBoxes.onClick(mX, mY);
        if (button == 1) {
            manager.specialRenderer = null;
        }
    }

    @Override
    public void onDrag(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onRelease(GuiManager gui, int mX, int mY) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onKeyTyped(GuiManager gui, char c, int k) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onScroll(int scroll) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
