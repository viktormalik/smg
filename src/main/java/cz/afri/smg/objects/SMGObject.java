/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package cz.afri.smg.objects;

public abstract class SMGObject {
  private final int size;
  private final String label;

  private static final SMGObject NULL_OBJECT = new SMGObject(0, "NULL") {
    @Override
    public boolean isAbstract() {
      return false;
    }

    @Override
    public void accept(final SMGObjectVisitor pVisitor) {
      pVisitor.visit(this);
    }

    @Override
    public boolean isMoreGeneral(final SMGObject pOther) {
      return false;
    }

    @Override
    public SMGObject join(final SMGObject pOther) {
      return pOther;
    }
  };

  public static final SMGObject getNullObject() {
    return NULL_OBJECT;
  }

  protected SMGObject(final int pSize, final String pLabel) {
    size = pSize;
    label = pLabel;
  }

  protected SMGObject(final SMGObject pOther) {
    size = pOther.size;
    label = pOther.label;
  }

  public final String getLabel() {
    return label;
  }

  public final int getSize() {
    return size;
  }

  public final boolean notNull() {
    return (!equals(NULL_OBJECT));
  }

  public abstract boolean isAbstract();

  public abstract void accept(SMGObjectVisitor visitor);

  public abstract boolean isMoreGeneral(SMGObject pOther);

  public abstract SMGObject join(SMGObject pOther);
}
