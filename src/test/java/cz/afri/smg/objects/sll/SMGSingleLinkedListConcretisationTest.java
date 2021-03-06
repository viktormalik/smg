/**
 * Created by Viktor Malik on 3.9.2015.
 */

package cz.afri.smg.objects.sll;

import cz.afri.smg.graphs.*;
import cz.afri.smg.objects.SMGObject;
import cz.afri.smg.objects.SMGRegion;
import cz.afri.smg.types.CPointerType;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SMGSingleLinkedListConcretisationTest {

    private static final int SIZE8 = 8;
    private static final int SIZE16 = 16;

    @Test
    public final void basicTest() {
        SMGRegion region = new SMGRegion(SIZE8, "prototype");
        final int offset4 = 4;
        SMGSingleLinkedList sll = new SMGSingleLinkedList(region, offset4, 3);
        SMGSingleLinkedListConcretisation concretisation = new SMGSingleLinkedListConcretisation(sll);

        Assert.assertSame(sll, concretisation.getSll());
    }

    @Test
    public final void executeOnSimpleList() {
        WritableSMG smg = SMGFactory.createWritableSMG();

        final int listLength = 6;
        final int offset = 8;

        SMGEdgeHasValue pointer = TestHelpers.createGlobalSll(smg, listLength, SIZE16, offset, "pointer");

        Integer value = pointer.getValue();
        SMGSingleLinkedList sll = (SMGSingleLinkedList) smg.getPointer(value).getObject();

        HashSet<ReadableSMG> concretisedSmgSet = sll.concretise(smg);
        Assert.assertEquals(concretisedSmgSet.size(), 1);
        ReadableSMG concretisedSmg = concretisedSmgSet.iterator().next();
        // Test heap size
        Set<SMGObject> heap = concretisedSmg.getHeapObjects();
        final int expectedHeapSize = 3;
        Assert.assertEquals(expectedHeapSize, heap.size());
        // Test creation of concrete region
        Assert.assertTrue(concretisedSmg.isPointer(value));
        SMGObject pointedObject = concretisedSmg.getPointer(value).getObject();
        Assert.assertTrue(pointedObject instanceof SMGRegion);
        Assert.assertFalse(pointedObject.isAbstract());
        // Test existence of new value with correct edges
        SMGEdgeHasValue newHv = concretisedSmg.getUniqueHV(
                SMGEdgeHasValueFilter.objectFilter(pointedObject), true);
        Assert.assertEquals(offset, newHv.getOffset());
        Integer newValue = newHv.getValue();
        Assert.assertNotNull(newValue);
        SMGEdgePointsTo newPt = concretisedSmg.getPointer(newValue);
        Assert.assertEquals(offset, newPt.getOffset());

        // Test edited SLL
        SMGObject newValueObj = newPt.getObject();
        Assert.assertTrue(newValueObj instanceof SMGSingleLinkedList);
        SMGSingleLinkedList editedSll = (SMGSingleLinkedList) newValueObj;
        Assert.assertEquals(sll, editedSll);
        Assert.assertEquals(editedSll.getLength(), listLength - 1);
        Assert.assertEquals(offset, editedSll.getOffset());
        Assert.assertEquals(sll.getSize(), editedSll.getSize());
    }

    @Test
    public final void executeOnNullLengthList() {
        WritableSMG smg = SMGFactory.createWritableSMG();

        final int listLength = 0;
        final int offset = 8;

        SMGEdgeHasValue pointer = TestHelpers.createGlobalSll(smg, listLength, SIZE16, offset, "pointer");

        Integer value = pointer.getValue();
        SMGObject region = pointer.getObject();
        SMGSingleLinkedList sll = (SMGSingleLinkedList) smg.getPointer(value).getObject();

        Integer nullValue = smg.getNullValue();
        SMGObject nullObject = smg.getNullObject();
        SMGEdgeHasValue hv = new SMGEdgeHasValue(CPointerType.getVoidPointer(), sll.getOffset(), sll, nullValue);
        SMGEdgePointsTo pt = new SMGEdgePointsTo(nullValue, nullObject, 0);
        smg.addHasValueEdge(hv);
        smg.addPointsToEdge(pt);

        HashSet<ReadableSMG> concretisedSmgSet = sll.concretise(smg);
        Assert.assertEquals(concretisedSmgSet.size(), 2);
        boolean noSll = false;
        for (ReadableSMG concretisedSmg : concretisedSmgSet) {
            pointer = concretisedSmg.getUniqueHV(SMGEdgeHasValueFilter.objectFilter(region), true);
            SMGObject pointedObject = concretisedSmg.getPointer(pointer.getValue()).getObject();
            if (pointedObject.equals(concretisedSmg.getNullObject())) {
                noSll = true;
                Assert.assertFalse(concretisedSmg.containsValue(value));
                Set<SMGObject> heap = concretisedSmg.getHeapObjects();
                final int expectedHeapSize = 1;
                Assert.assertEquals(heap.size(), expectedHeapSize);
                Assert.assertEquals(pointedObject, concretisedSmg.getNullObject());
            }
        }
        Assert.assertTrue(noSll);
    }

    @Test
    public final void executeOnNullLengthUnterminatedList() {
        WritableSMG smg = SMGFactory.createWritableSMG();

        final int listLength = 0;
        final int offset = 8;

        SMGEdgeHasValue pointer = TestHelpers.createGlobalSll(smg, listLength, SIZE16, offset, "pointer");

        Integer value = pointer.getValue();
        SMGObject region = pointer.getObject();
        SMGSingleLinkedList sll = (SMGSingleLinkedList) smg.getPointer(value).getObject();
        Set<Integer> values = smg.getValues();
        Assert.assertEquals(values.size(), 2);

        HashSet<ReadableSMG> concretisedSmgSet = sll.concretise(smg);
        Assert.assertEquals(concretisedSmgSet.size(), 2);

        boolean noPointer = false;
        for (ReadableSMG concretisedSmg : concretisedSmgSet) {
            pointer = concretisedSmg.getUniqueHV(SMGEdgeHasValueFilter.objectFilter(region), true);
            if (!concretisedSmg.isPointer(pointer.getValue())){
                noPointer = true;
                Set<Integer> newValues = concretisedSmg.getValues();
                Assert.assertEquals(newValues.size(), 2);
                Assert.assertFalse(values.equals(newValues));
                Assert.assertTrue(newValues.contains(pointer.getValue()));
            }
        }
        Assert.assertTrue(noPointer);
    }
}
