package vswe.stevesfactory.multipart;

import codechicken.multipart.TMultiPart;

/**
 * Created with IntelliJ IDEA.
 * User: Vswe
 * Date: 26/01/14
 * Time: 01:18
 * To change this template use File | Settings | File Templates.
 */
public class BlockMultiTest extends TMultiPart {
    /**
     * The unique string identifier for this class of multipart.
     */
    @Override
    public String getType() {
        return MultiPartFactory.CABLE_ID;
    }


}
