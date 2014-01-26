package vswe.stevesfactory.multipart;

import codechicken.multipart.MultiPartRegistry;
import codechicken.multipart.TMultiPart;

/**
 * Created with IntelliJ IDEA.
 * User: Vswe
 * Date: 26/01/14
 * Time: 02:00
 * To change this template use File | Settings | File Templates.
 */
public class MultiPartFactory implements MultiPartRegistry.IPartFactory {

    public static final String CABLE_ID = "sfm.cable";

    public MultiPartFactory() {
        MultiPartRegistry.registerParts(this, new String[] {CABLE_ID});
        new ItemMultiTest(3333);
    }

    /**
     * Create a new instance of the part with the specified type name identifier
     * @param client If the part instance is for the client or the server
     */
    @Override
    public TMultiPart createPart(String name, boolean client) {
        return name.equals(CABLE_ID) ? new BlockMultiTest() : null;
    }
}
