package vswe.stevesfactory.components;

import java.util.List;


public interface IItemBufferElement {
    void prepareSubElements();
    IItemBufferSubElement getSubElement();
    void removeSubElement();
    int retrieveItemCount(int moveCount);
    void decreaseStackSize(int moveCount);
}
