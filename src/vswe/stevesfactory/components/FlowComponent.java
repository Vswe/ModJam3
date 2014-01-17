package vswe.stevesfactory.components;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.opengl.GL11;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.interfaces.ContainerManager;
import vswe.stevesfactory.interfaces.GuiManager;
import vswe.stevesfactory.network.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowComponent implements IComponentNetworkReader {
    private static final int COMPONENT_SRC_X = 0;
    private static final int COMPONENT_SRC_Y = 0;
    private static final int COMPONENT_SIZE_W = 64;
    private static final int COMPONENT_SIZE_H = 20;
    private static final int COMPONENT_SIZE_LARGE_W = 124;
    private static final int COMPONENT_SIZE_LARGE_H = 152;
    private static final int COMPONENT_SRC_LARGE_X = 64;
    private static final int DRAGGABLE_SIZE = 6;

    private static final int ARROW_X = -10;
    private static final int ARROW_Y = 5;
    private static final int ARROW_SIZE_W = 9;
    private static final int ARROW_SIZE_H = 10;
    private static final int ARROW_SRC_X = 0;
    private static final int ARROW_SRC_Y = 20;

    private static final int MENU_ITEM_SIZE_W = 120;
    private static final int MENU_ITEM_SIZE_H = 13;
    private static final int MENU_ITEM_SRC_X = 0;
    private static final int MENU_ITEM_SRC_Y = 152;
    private static final int MENU_X = 2;
    private static final int MENU_Y = 20;
    private static final int MENU_SIZE_H = 130;
    private static final int MENU_ITEM_CAPACITY = 5;

    private static final int MENU_ARROW_X = 109;
    private static final int MENU_ARROW_Y = 2;
    private static final int MENU_ARROW_SIZE_W = 9;
    private static final int MENU_ARROW_SIZE_H = 9;
    private static final int MENU_ARROW_SRC_X = 0;
    private static final int MENU_ARROW_SRC_Y = 40;

    private static final int MENU_ITEM_TEXT_X = 5;
    private static final int MENU_ITEM_TEXT_Y = 3;

    private static final int CONNECTION_SIZE_W = 7;
    private static final int CONNECTION_SIZE_H = 6;
    private static final int CONNECTION_SRC_X = 0;
    private static final int CONNECTION_SRC_Y = 58;

    private static final int ERROR_X = 2;
    private static final int ERROR_Y = 8;
    private static final int ERROR_SIZE_W = 2;
    private static final int ERROR_SIZE_H = 10;
    private static final int ERROR_SRC_X = 62;
    private static final int ERROR_SRC_Y = 20;

    private static final int NODE_SRC_X = 120;
    private static final int NODE_SRC_Y = 152;
    private static final int NODE_SIZE = 4;
    private static final int MAX_NODES = 15;

    public FlowComponent(TileEntityManager manager, int x, int y, ComponentType type) {
        this.x = x;
        this.y = y;
        this.connectionSet = type.getSets()[0];
        this.type = type;
        this.manager = manager;
        this.id = manager.getFlowItems().size();

        menus = new ArrayList<ComponentMenu>();
        for (Class<? extends ComponentMenu> componentMenuClass : type.getClasses()) {
            try {
                Constructor<? extends ComponentMenu> constructor = componentMenuClass.getConstructor(FlowComponent.class);
                Object obj = constructor.newInstance(this);


                menus.add((ComponentMenu)obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        openMenuId = -1;
        connections = new HashMap<Integer, Connection>();
    }

    private int x;
    private int y;
    private int mouseDragX;
    private int mouseDragY;
    private boolean isDragging;
    private boolean isLarge;
    private List<ComponentMenu> menus;
    private int openMenuId;
    private ConnectionSet connectionSet;
    private ComponentType type;
    private TileEntityManager manager;
    private int id;
    private Map<Integer, Connection> connections;
    private int currentInterval;


    public int getCurrentInterval() {
        return currentInterval;
    }

    public void setCurrentInterval(int currentInterval) {
        this.currentInterval = currentInterval;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ConnectionSet getConnectionSet() {
        return connectionSet;
    }

    public void setConnectionSet(ConnectionSet connectionSet) {
        if (this.connections != null) {
            int oldLength = this.connectionSet.getConnections().length;
            int newLength = connectionSet.getConnections().length;

            for (int i = newLength; i < oldLength; i++) {
                Connection connection = connections.get(i);
                if (connection != null) {
                    removeConnection(i);
                }
            }
        }
        this.connectionSet = connectionSet;
    }

    @SideOnly(Side.CLIENT)
    public void draw(GuiManager gui, int mX, int mY, int zLevel) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, zLevel);

        gui.drawTexture(x, y, isLarge ? COMPONENT_SRC_LARGE_X : COMPONENT_SRC_X, COMPONENT_SRC_Y, getComponentWidth(), getComponentHeight());

        int internalX = mX - x;
        int internalY = mY - y;

        int srcArrowX = isLarge ? 1 : 0;
        int srcArrowY = inArrowBounds(internalX, internalY) ? 1 : 0;
        gui.drawTexture(x + getComponentWidth() + ARROW_X, y + ARROW_Y, ARROW_SRC_X + ARROW_SIZE_W * srcArrowX, ARROW_SRC_Y + ARROW_SIZE_H * srcArrowY, ARROW_SIZE_W, ARROW_SIZE_H);


        if (isLarge) {
            for (int i = 0; i < menus.size(); i++) {
                ComponentMenu menu = menus.get(i);

                if (!menu.isVisible()) {
                    if (openMenuId == i) {
                        openMenuId = -1;
                    }

                    continue;
                }

                int itemX = getMenuAreaX();
                int itemY = y + getMenuItemY(i);
                gui.drawTexture(itemX, itemY, MENU_ITEM_SRC_X, MENU_ITEM_SRC_Y, MENU_ITEM_SIZE_W, MENU_ITEM_SIZE_H);

                int srcItemArrowX = inMenuArrowBounds(i, internalX, internalY) ? 1 : 0;
                int srcItemArrowY = i == openMenuId ? 1 : 0;
                gui.drawTexture(itemX + MENU_ARROW_X, itemY + MENU_ARROW_Y, MENU_ARROW_SRC_X + MENU_ARROW_SIZE_W * srcItemArrowX, MENU_ARROW_SRC_Y + MENU_ARROW_SIZE_H * srcItemArrowY, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H);

                gui.drawString(menu.getName(), x + MENU_X + MENU_ITEM_TEXT_X, y + getMenuItemY(i) + MENU_ITEM_TEXT_Y, 0x404040);

                if (i == openMenuId) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(itemX, getMenuAreaY(i), 0);
                    menu.draw(gui, mX - itemX, mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }


        boolean hasConnection = false;
        int outputCount = 0;
        int inputCount = 0;
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            ConnectionOption connection = connectionSet.getConnections()[i];

            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }

            int srcConnectionX = (CollisionHelper.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) ? 1 : 0;

            Connection current = manager.getCurrentlyConnecting();
            if (current != null && current.getComponentId() == id && current.getConnectionId() == i) {
                gui.drawLine(location[0] + CONNECTION_SIZE_W / 2, location[1] + CONNECTION_SIZE_H / 2, mX, mY);
            }

            Connection connectedConnection = connections.get(i);
            if (connectedConnection != null) {
                hasConnection = true;
                if (id < connectedConnection.getComponentId()) {
                    int[] otherLocation = manager.getFlowItems().get(connectedConnection.getComponentId()).getConnectionLocationFromId(connectedConnection.getConnectionId());
                    int startX = location[0] + CONNECTION_SIZE_W / 2;
                    int startY = location[1] + CONNECTION_SIZE_H / 2;
                    int endX = otherLocation[0] + CONNECTION_SIZE_W / 2;
                    int endY = otherLocation[1] + CONNECTION_SIZE_H / 2;

                    GL11.glPushMatrix();
                    GL11.glTranslatef(0, 0, -zLevel);
                    List<Point> nodes = connectedConnection.getNodes();
                    for (int j = 0; j <= nodes.size(); j++) {
                        int x1, y1, x2, y2;
                        if (j == 0) {
                            x1 = startX;
                            y1 = startY;
                        }else{
                            x1 = nodes.get(j - 1).getX();
                            y1 = nodes.get(j - 1).getY();
                        }

                        if (j == nodes.size()) {
                            x2 = endX;
                            y2 = endY;
                        }else{
                            x2 = nodes.get(j).getX();
                            y2 = nodes.get(j).getY();
                        }

                        gui.drawLine(x1, y1, x2, y2);
                    }

                    for (Point node : nodes) {
                        int x = node.getX() - NODE_SIZE / 2;
                        int y = node.getY() - NODE_SIZE / 2;
                        int srcXNode = connectedConnection.getSelectedNode() == null && CollisionHelper.inBounds(x, y, NODE_SIZE, NODE_SIZE, mX, mY) ? 1 : 0;
                        gui.drawTexture(x, y, NODE_SRC_X + srcXNode * NODE_SIZE, NODE_SRC_Y, NODE_SIZE, NODE_SIZE);
                    }

                    GL11.glPopMatrix();
                }
            }

            gui.drawTexture(location[0], location[1], CONNECTION_SRC_X + srcConnectionX * CONNECTION_SIZE_W, location[2], CONNECTION_SIZE_W, CONNECTION_SIZE_H);

        }

        errors.clear();
        if (hasConnection) {
            for (ComponentMenu menu : menus) {
                menu.addErrors(errors);
            }
        }

        if (!errors.isEmpty()) {
            int srcErrorY = CollisionHelper.inBounds(x + ERROR_X, y + ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX, mY) ? 1 : 0;
            gui.drawTexture(x + ERROR_X, y + ERROR_Y, ERROR_SRC_X, ERROR_SRC_Y + srcErrorY * ERROR_SIZE_H, ERROR_SIZE_W, ERROR_SIZE_H);
        }

        gui.drawString(getType().getName(), x + 7, y + 10, 0.7F, 0x404040);

        GL11.glPopMatrix();
    }

    List<String> errors = new ArrayList<String>();

    private int[] getConnectionLocationFromId(int id) {
        int outputCount = 0;
        int inputCount = 0;
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            ConnectionOption connection = connectionSet.getConnections()[i];

            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (id == i) {
                return location;
            }
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }
        }
        return null;
    }


    @SideOnly(Side.CLIENT)
    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        if (isLarge) {
            for (int i = 0; i < menus.size(); i++) {
                ComponentMenu menu = menus.get(i);

                if (menu.isVisible() && i == openMenuId) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(getMenuAreaX(), getMenuAreaY(i), 0);
                    menu.drawMouseOver(gui, mX - getMenuAreaX(), mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }

        int outputCount = 0;
        int inputCount = 0;
        for (ConnectionOption connection : connectionSet.getConnections()) {
            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }

            if (CollisionHelper.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) {
                gui.drawMouseOver(connection.toString(), mX, mY);
            }
        }

        if (!errors.isEmpty()) {
            if (CollisionHelper.inBounds(x + ERROR_X, y + ERROR_Y, ERROR_SIZE_W, ERROR_SIZE_H, mX, mY)) {
                String str = "";
                for (int i = 0; i < errors.size(); i++) {
                    if (i != 0) {
                        str += "\n\n";
                    }
                    str += errors.get(i);
                }
                gui.drawMouseOver(str, mX, mY);
            }
        }
    }

    public boolean onClick(int mX, int mY, int button) {
        if (CollisionHelper.inBounds(x, y, getComponentWidth(), getComponentHeight(), mX, mY)) {
           int internalX = mX - x;
           int internalY = mY - y;

            if (internalX <= DRAGGABLE_SIZE && internalY <= DRAGGABLE_SIZE) {
                mouseDragX = mX;
                mouseDragY = mY;
                isDragging = true;
            }else if(inArrowBounds(internalX, internalY)) {
                isLarge = !isLarge;
            }else if (isLarge){

                for (int i = 0; i < menus.size(); i++) {
                    ComponentMenu menu = menus.get(i);

                    if (menu.isVisible()) {
                        if (inMenuArrowBounds(i, internalX, internalY)) {
                            if (openMenuId == i) {
                                openMenuId = -1;
                            }else{
                                openMenuId = i;
                            }

                            return true;
                        }

                        if (i == openMenuId) {
                            menu.onClick(mX - getMenuAreaX(), mY - getMenuAreaY(i), button);
                        }
                    }
                }

            }
            return true;
        }else{
            int outputCount = 0;
            int inputCount = 0;
            for (int i = 0; i < connectionSet.getConnections().length; i++) {
                ConnectionOption connection = connectionSet.getConnections()[i];

                int[] location = getConnectionLocation(connection, inputCount, outputCount);
                if (connection.isInput()) {
                    inputCount++;
                }else{
                    outputCount++;
                }

                if (CollisionHelper.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) {


                    Connection current = manager.getCurrentlyConnecting();
                    if (button == 1 && current == null) {
                        Connection selected = connections.get(i);

                        if (selected != null) {
                            boolean reversed = false;
                            FlowComponent component = this;
                            if (selected.getComponentId() < id) {
                                component = manager.getFlowItems().get(selected.getComponentId());
                                selected = component.getConnection(selected.getConnectionId());
                                reversed = true;
                            }
                            if (selected.getNodes().size() < MAX_NODES && selected.getSelectedNode() == null) {
                                int id = reversed ? selected.getNodes().size() : 0;
                                selected.addAndSelectNode(mX, mY, id);
                                component.sendConnectionNode(i, id, false, true, mX, mY);
                            }
                        }
                    }else{
                        if (current == null) {
                            if (connections.get(i) != null) {
                                removeConnection(i);
                            }
                            manager.setCurrentlyConnecting(new Connection(id, i));
                        }else if (current.getComponentId() == this.id && current.getConnectionId() == i) {
                            manager.setCurrentlyConnecting(null);
                        }else if (current.getComponentId() != id){
                            FlowComponent connectTo = manager.getFlowItems().get(current.getComponentId());
                            ConnectionOption connectToOption = connectTo.connectionSet.getConnections()[current.getConnectionId()];


                            if (connectToOption.isInput() != connection.isInput()) {

                                if (checkForLoops(i, current)) {
                                    return true;
                                }

                                if (connections.get(i) != null) {
                                    removeConnection(i);
                                }

                                Connection thisConnection = new Connection(id, i);
                                connectTo.addConnection(current.getConnectionId(), thisConnection);
                                addConnection(i, manager.getCurrentlyConnecting());
                                manager.setCurrentlyConnecting(null);
                            }
                        }
                    }

                    return true;
                }else{
                    Connection selected = connections.get(i);
                    if (selected != null) {
                        List<Point> nodes = selected.getNodes();
                        for (int j = 0; j < nodes.size(); j++) {
                            Point node = nodes.get(j);
                            int x = node.getX() - NODE_SIZE / 2;
                            int y = node.getY() - NODE_SIZE / 2;
                            if (CollisionHelper.inBounds(x, y, NODE_SIZE, NODE_SIZE, mX, mY)) {
                                if (button == 0) {
                                    selected.setSelectedNode(node);
                                }else if (button == 1) {
                                    if (GuiScreen.isShiftKeyDown()) {
                                        sendConnectionNode(i, j, true, false, 0, 0);
                                    }else if (selected.getNodes().size() < MAX_NODES && selected.getSelectedNode() == null) {
                                        selected.addAndSelectNode(mX, mY, j + 1);
                                        sendConnectionNode(i, j + 1, false, true, mX, mY);
                                    }
                                }
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }
    }
    private boolean checkForLoops(int connectionId, Connection connection) {
        return checkForLoops(new ArrayList<Integer>(), this, connectionId, connection);
    }
    private boolean checkForLoops(List<Integer> usedComponents, FlowComponent currentComponent, int connectionId, Connection connection) {
        if (usedComponents.contains(currentComponent.getId()))  {
            return true;
        }
        usedComponents.add(currentComponent.getId());

        for (int i = 0; i < currentComponent.connectionSet.getConnections().length; i++) {
            if (!currentComponent.connectionSet.getConnections()[i].isInput()) {
                Connection c = null;

                if (connectionId == i && currentComponent.getId() == this.id) {
                    //the new connection
                    c = connection;
                }else if(connection.getComponentId() == currentComponent.getId() && connection.getConnectionId() == i) {
                    //the new connection in the other direction
                    c = new Connection(this.getId(), connectionId);
                }else{
                    c = currentComponent.connections.get(i);
                    //old connection that will be replaced
                    if (c != null && c.getComponentId() == this.id && c.getConnectionId() == connectionId) {
                        c = null;
                    }
                }

                if (c != null) {
                    if (c.getComponentId() >= 0 && c.getComponentId() < manager.getFlowItems().size()) {
                        List<Integer> usedComponentsCopy = new ArrayList<Integer>(usedComponents);

                        if (checkForLoops(usedComponentsCopy, manager.getFlowItems().get(c.getComponentId()), connectionId, connection)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void addConnection(int id, Connection connection) {
        DataWriter dw = PacketHandler.getWriterForServerComponentPacket(this, null);
        if (connection != null) {
            writeConnectionData(dw, id, true, connection.getComponentId(), connection.getConnectionId());
        }else{
            writeConnectionData(dw, id, false, 0, 0);
        }
        connections.put(id, connection);

        PacketHandler.sendDataToServer(dw);
    }

    private void removeConnection(int id) {
        Connection connection = connections.get(id);

        addConnection(id, null);
        manager.getFlowItems().get(connection.getComponentId()).addConnection(connection.getConnectionId(), null);
    }

    public void onDrag(int mX, int mY) {
        followMouse(mX, mY);

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);
            menu.onDrag(mX - getMenuAreaX(), mY - getMenuAreaY(i));
        }

        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            Connection connection = connections.get(i);
            if (connection != null) {
                connection.update(mX, mY);
            }
        }
    }

    public void onRelease(int mX, int mY, int button) {
        followMouse(mX, mY);
        if (isDragging) {
            writeLocationData();
        }
        isDragging = false;

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);
            menu.onRelease(mX - getMenuAreaX(), mY - getMenuAreaY(i));
        }



        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            Connection connection = connections.get(i);
            if (connection != null) {
                for (int j = 0; j < connection.getNodes().size(); j++) {
                    Point node = connection.getNodes().get(j);
                    if (node.equals(connection.getSelectedNode())) {
                        connection.setSelectedNode(null);
                        sendConnectionNode(i, j, false, false, mX, mY);
                        return;
                    }
                }
            }
        }

    }

    private void followMouse(int mX, int mY) {
        if (isDragging) {
            x += mX - mouseDragX;
            y += mY - mouseDragY;

            mouseDragX = mX;
            mouseDragY = mY;


        }
    }


    private boolean inArrowBounds(int internalX, int internalY) {
        return CollisionHelper.inBounds(getComponentWidth() + ARROW_X, ARROW_Y, ARROW_SIZE_W, ARROW_SIZE_H, internalX, internalY);
    }


    private boolean inMenuArrowBounds(int i, int internalX, int internalY) {
        return CollisionHelper.inBounds(MENU_X + MENU_ARROW_X, getMenuItemY(i) + MENU_ARROW_Y, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H, internalX, internalY);
    }

    private int getMenuItemY(int id) {


        int ret = MENU_Y;
        for (int i = 0; i < id; i++) {
            if (menus.get(i).isVisible()) {
                ret += MENU_ITEM_SIZE_H - 1;
                if (openMenuId == i) {
                    ret += getMenuOpenSize() - 1;
                }
            }
        }


        return ret;
    }


    public static int getMenuOpenSize() {
        return MENU_SIZE_H - MENU_ITEM_CAPACITY * (MENU_ITEM_SIZE_H - 1);
    }


    public int getComponentWidth() {
        return isLarge ? COMPONENT_SIZE_LARGE_W : COMPONENT_SIZE_W;
    }

    public int getComponentHeight() {
        return isLarge ? COMPONENT_SIZE_LARGE_H : COMPONENT_SIZE_H;
    }

    private int[] getConnectionLocation(ConnectionOption connection, int inputCount, int outputCount) {
        int targetX;
        int targetY;

        int srcConnectionY;
        int currentCount;
        int totalCount;

        if (connection.isInput()) {
            currentCount = inputCount;
            totalCount = connectionSet.getInputCount();
            srcConnectionY = 1;
            targetY = y - CONNECTION_SIZE_H;
        }else{
            currentCount = outputCount;
            totalCount = connectionSet.getOutputCount();
            srcConnectionY = 0;
            targetY = y + getComponentHeight();
        }

        targetX = x + (int)(getComponentWidth() * ((currentCount + 0.5)  / totalCount));
        targetX -= CONNECTION_SIZE_W / 2;

        return new int[] {targetX, targetY, CONNECTION_SRC_Y + srcConnectionY * CONNECTION_SIZE_H};
    }


    private int getMenuAreaX() {
        return x + MENU_X;
    }

    private int getMenuAreaY(int i) {
        return  y + getMenuItemY(i) + MENU_ITEM_SIZE_H;
    }
    @SideOnly(Side.CLIENT)
    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        if (isLarge && openMenuId != -1) {
            return menus.get(openMenuId).onKeyStroke(gui, c, k);
        }

        return false;
    }

    public ComponentType getType() {
        return type;
    }

    public List<ComponentMenu> getMenus() {
        return menus;
    }


    public TileEntityManager getManager() {
        return manager;
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        if (dr.readBoolean()) {
            x = dr.readData(DataBitHelper.FLOW_CONTROL_X);
            y = dr.readData(DataBitHelper.FLOW_CONTROL_Y);
        }else {
            int connectionId = dr.readData(DataBitHelper.CONNECTION_ID);
            if (dr.readBoolean()) {
                Connection connection;
                if (dr.readBoolean()) {
                    int targetComponentId = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
                    int targetConnectionId = dr.readData(DataBitHelper.CONNECTION_ID);

                    connection = new Connection(targetComponentId, targetConnectionId);
                }else{
                    connection = null;
                }

                connections.put(connectionId, connection);
            }else if(connections.get(connectionId) != null){
                Connection connection = connections.get(connectionId);

                int id = dr.readData(DataBitHelper.NODE_ID);
                int length = -1;
                if (manager.worldObj.isRemote) {
                    length = dr.readData(DataBitHelper.NODE_ID);
                }
                boolean deleted = dr.readBoolean();
                boolean created = false;
                if (id >= 0 && ((!deleted && (created = dr.readBoolean()) && id == connection.getNodes().size()) || id < connection.getNodes().size())) {
                    if (deleted) {
                        connection.getNodes().remove(id);
                        System.out.println("Deleted " + id + " " + manager.worldObj.isRemote);
                    }else {
                        Point node;
                        if (created) {
                            node = new Point();
                            if (connection.getNodes().size() < MAX_NODES && (!manager.worldObj.isRemote || length > connection.getNodes().size())) {
                                connection.getNodes().add(id, node);
                                System.out.println("Added " + id + " " + manager.worldObj.isRemote);
                            }
                        }else{
                            node = connection.getNodes().get(id);
                            System.out.println("Updated " + id + " " + manager.worldObj.isRemote);
                        }

                        node.setX(dr.readData(DataBitHelper.FLOW_CONTROL_X));
                        node.setY(dr.readData(DataBitHelper.FLOW_CONTROL_Y));
                    }

                }
            }
        }
    }


    private void writeLocationData() {
        DataWriter dw = PacketHandler.getWriterForServerComponentPacket(this, null);
        writeLocationData(dw);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeLocationData(DataWriter dw) {
        dw.writeBoolean(true);
        dw.writeData(x, DataBitHelper.FLOW_CONTROL_X);
        dw.writeData(y, DataBitHelper.FLOW_CONTROL_Y);
    }

    public FlowComponent copy() {
        FlowComponent copy = new FlowComponent(manager, x, y, type);
        copy.id = id;

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);

            copy.menus.get(i).copyFrom(menu);
        }


        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            Connection connection = connections.get(i);
            if (connection != null) {
                copy.connections.put(i, connection.copy());
            }
        }

        return copy;
    }

    private void writeConnectionData(DataWriter dw, int i, boolean target, int targetComponent, int targetConnection) {
        dw.writeBoolean(false);
        dw.writeData(i, DataBitHelper.CONNECTION_ID);
        dw.writeBoolean(true); //connection
        dw.writeBoolean(target);
        if (target) {
            dw.writeData(targetComponent, DataBitHelper.FLOW_CONTROL_COUNT);
            dw.writeData(targetConnection, DataBitHelper.CONNECTION_ID);
        }
    }

    private void writeConnectionNode(DataWriter dw, int length, int connectionId, int nodeId, boolean deleted, boolean created, int x, int y) {
        dw.writeBoolean(false);
        dw.writeData(connectionId, DataBitHelper.CONNECTION_ID);
        dw.writeBoolean(false); //nodes
        dw.writeData(nodeId, DataBitHelper.NODE_ID);
        if (length != -1) {
            dw.writeData(length, DataBitHelper.NODE_ID);
        }
        dw.writeBoolean(deleted);
        if (!deleted) {
            dw.writeBoolean(created);
            dw.writeData(x, DataBitHelper.FLOW_CONTROL_X);
            dw.writeData(y, DataBitHelper.FLOW_CONTROL_Y);
        }
    }

    private void sendConnectionNode(int connectionId, int nodeId, boolean deleted, boolean created, int x, int y) {
        DataWriter dw = PacketHandler.getWriterForServerComponentPacket(this, null);
        writeConnectionNode(dw, -1, connectionId, nodeId, deleted, created, x, y);
        PacketHandler.sendDataToServer(dw);
    }

    private void sendClientConnectionNode(ContainerManager container, int length, int connectionId, int nodeId, boolean deleted, boolean created, int x, int y) {
        DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
        writeConnectionNode(dw, length, connectionId, nodeId, deleted, created, x, y);
        PacketHandler.sendDataToListeningClients(container, dw);
    }

    public void refreshData(ContainerManager container, FlowComponent newData) {
        if (x != newData.x || y != newData.y) {
            x = newData.x;
            y = newData.y;

            DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
            writeLocationData(dw);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            if (newData.connections.get(i) == null && connections.get(i) != null) {
                connections.put(i, null);
                DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
                writeConnectionData(dw, i, false, 0, 0);
                PacketHandler.sendDataToListeningClients(container, dw);
            }

            if (newData.connections.get(i) != null && (connections.get(i) == null || newData.connections.get(i).getComponentId() != connections.get(i).getComponentId() || newData.connections.get(i).getConnectionId() != connections.get(i).getConnectionId())) {
                connections.put(i, newData.connections.get(i).copy());
                DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
                writeConnectionData(dw, i, true, connections.get(i).getComponentId(), connections.get(i).getConnectionId());
                PacketHandler.sendDataToListeningClients(container, dw);
            }
            Connection connection = connections.get(i);
            Connection newConnection = newData.connections.get(i);
            if (connection != null && newConnection != null) {
                boolean deleted = connection.getNodes().size() > newConnection.getNodes().size();
                boolean created = connection.getNodes().size() < newConnection.getNodes().size();

                if (deleted) {
                    boolean hasDeleted = false;
                    for (int j = 0; j < newConnection.getNodes().size(); j++) {
                        Point node = connection.getNodes().get(j);
                        Point newNode = newConnection.getNodes().get(j);

                        if (node.getX() != newNode.getX() || node.getY() != newNode.getY()) {
                            sendClientConnectionNode(container, newConnection.getNodes().size(), i, j, true, false, 0, 0);
                            hasDeleted = true;
                            break;
                        }
                    }

                    if (!hasDeleted) {
                        sendClientConnectionNode(container, newConnection.getNodes().size(), i, newConnection.getNodes().size(), true, false, 0, 0);
                    }
                }else{
                    boolean updated = false;
                    for (int j = 0; j < connection.getNodes().size(); j++) {
                        Point node = connection.getNodes().get(j);
                        Point newNode = newConnection.getNodes().get(j);

                        if (node.getX() != newNode.getX() || node.getY() != newNode.getY()) {
                            updated = true;
                            if (created) {
                                Point nextNode = newConnection.getNodes().get(j + 1);
                                if (node.getX() == nextNode.getX() && node.getY() == nextNode.getY()) {
                                    sendClientConnectionNode(container, newConnection.getNodes().size(), i, j, false, true, newNode.getX(), newNode.getY());
                                    break;
                                }
                            }
                            sendClientConnectionNode(container, newConnection.getNodes().size(), i, j, false, false, newNode.getX(), newNode.getY());
                        }
                    }

                    if (!updated && created) {
                        sendClientConnectionNode(container, newConnection.getNodes().size(), i, newConnection.getNodes().size() - 1, false, true, newConnection.getNodes().get(newConnection.getNodes().size() - 1).getX(), newConnection.getNodes().get(newConnection.getNodes().size() - 1).getY());
                    }
                }

                connections.put(i, newConnection.copy());
            }
        }

        for (int i = 0; i < menus.size(); i++) {
            menus.get(i).refreshData(container, newData.menus.get(i));
        }
    }

    public int getId() {
        return id;
    }


    public Connection getConnection(int i) {
        return connections.get(i);
    }

    public boolean isBeingMoved() {
        return isDragging;
    }

    public void decreaseId() {
        id--;
    }

    public void updateConnectionIdsAtRemoval(int idToRemove) {
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            Connection connection   = connections.get(i);
            if (connection != null) {
                if (connection.getComponentId() == idToRemove) {
                    connections.remove(i);
                }else if (connection.getComponentId() > idToRemove) {
                    connection.setComponentId(connection.getComponentId() - 1);
                }
            }
        }
    }

    private static final String NBT_POS_X = "PosX";
    private static final String NBT_POS_Y = "PosY";
    private static final String NBT_TYPE= "Type";
    private static final String NBT_CONNECTION = "Connection";
    private static final String NBT_CONNECTION_POS = "ConnectionPos";
    private static final String NBT_CONNECTION_TARGET_COMPONENT = "ConnectionComponent";
    private static final String NBT_CONNECTION_TARGET_CONNECTION = "ConnectionConnection";
    private static final String NBT_INTERVAL = "Interval";
    private static final String NBT_MENUS = "Menus";
    private static final String NBT_NODES = "Nodes";

    public static FlowComponent readFromNBT(TileEntityManager jam, NBTTagCompound nbtTagCompound, int version) {
        int x = nbtTagCompound.getShort(NBT_POS_X);
        int y = nbtTagCompound.getShort(NBT_POS_Y);
        int typeId = nbtTagCompound.getByte(NBT_TYPE);

        FlowComponent component = new FlowComponent(jam, x, y, ComponentType.getTypeFromId(typeId));

        NBTTagList connections = nbtTagCompound.getTagList(NBT_CONNECTION);
        for (int i = 0; i < connections.tagCount(); i++) {
            NBTTagCompound connectionTag = (NBTTagCompound)connections.tagAt(i);

            Connection connection = new Connection(connectionTag.getByte(NBT_CONNECTION_TARGET_COMPONENT), connectionTag.getByte(NBT_CONNECTION_TARGET_CONNECTION));

            if (connectionTag.hasKey(NBT_NODES)) {
                connection.getNodes().clear();
                NBTTagList nodes = connectionTag.getTagList(NBT_NODES);
                for (int j = 0; j < nodes.tagCount(); j++) {
                    NBTTagCompound nodeTag = (NBTTagCompound)nodes.tagAt(j);

                    connection.getNodes().add(new Point(nodeTag.getShort(NBT_POS_X), nodeTag.getShort(NBT_POS_Y)));
                }
            }

            component.connections.put((int)connectionTag.getByte(NBT_CONNECTION_POS), connection);
        }

        if (component.type == ComponentType.TRIGGER) {
            component.currentInterval = nbtTagCompound.getShort(NBT_INTERVAL);
        }

        NBTTagList menuTagList = nbtTagCompound.getTagList(NBT_MENUS);
        int menuId = 0;
        for (int i = 0; i < menuTagList.tagCount(); i++) {
            NBTTagCompound menuTag = (NBTTagCompound)menuTagList.tagAt(i);



            //added an extra menu to the triggers
            if (component.type == ComponentType.TRIGGER && i == 1 && version < 1) {
                menuId++;
            }

            //added a second extra menu to the triggers
            if (component.type == ComponentType.TRIGGER && i == 0 && version < 6) {
                menuId++;
            }

            //added an extra menu to the flow controls
            if (component.type == ComponentType.FLOW_CONTROL && i == 0 && version < 4) {
                menuId++;
            }

            //added a second extra menu to the triggers
            if (component.type == ComponentType.TRIGGER && i == 2 && version < 5) {
                menuId++;
            }

            component.menus.get(menuId).readFromNBT(menuTag, version);
            menuId++;
        }

        return component;
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setShort(NBT_POS_X, (short)x);
        nbtTagCompound.setShort(NBT_POS_Y, (short)y);
        nbtTagCompound.setByte(NBT_TYPE, (byte)type.getId());

        NBTTagList connections = new NBTTagList();
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            Connection connection = this.connections.get(i);

            if (connection != null) {
                NBTTagCompound connectionTag = new NBTTagCompound();
                connectionTag.setByte(NBT_CONNECTION_POS, (byte)i);
                connectionTag.setByte(NBT_CONNECTION_TARGET_COMPONENT, (byte)connection.getComponentId());
                connectionTag.setByte(NBT_CONNECTION_TARGET_CONNECTION, (byte)connection.getConnectionId());


                NBTTagList nodes = new NBTTagList();
                for (Point point : connection.getNodes()) {
                    NBTTagCompound nodeTag = new NBTTagCompound();

                    nodeTag.setShort(NBT_POS_X, (short)point.getX());
                    nodeTag.setShort(NBT_POS_Y, (short)point.getY());

                    nodes.appendTag(nodeTag);
                }

                connectionTag.setTag(NBT_NODES, nodes);

                connections.appendTag(connectionTag);
            }
        }
        nbtTagCompound.setTag(NBT_CONNECTION, connections);

        if (type == ComponentType.TRIGGER) {
            nbtTagCompound.setShort(NBT_INTERVAL, (short) currentInterval);
        }

        NBTTagList menuTagList = new NBTTagList();
        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);

            NBTTagCompound menuTag = new NBTTagCompound();

            menu.writeToNBT(menuTag);

            menuTagList.appendTag(menuTag);

        }
        nbtTagCompound.setTag(NBT_MENUS, menuTagList);
    }

    public boolean isOpen() {
        return isLarge;
    }

    public void close() {
        isLarge = false;
    }

    public void setConnection(int i, Connection connection) {
        connections.put(i, connection);
    }

    public void clearConnections() {
        connections.clear();
    }
}
