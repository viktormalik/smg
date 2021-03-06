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

import com.google.common.collect.Sets;
import cz.afri.smg.SMGConcretisation;
import cz.afri.smg.graphs.ReadableSMG;

import java.util.HashSet;

public abstract class SMGAbstractObject extends SMGObject {

  protected SMGAbstractObject(final int pSize, final String pLabel) {
    super(pSize, pLabel);
  }

  protected SMGAbstractObject(final SMGObject pPrototype) {
    super(pPrototype);
  }

  @Override
  public final boolean isAbstract() { return true; }
  public abstract boolean matchGenericShape(SMGAbstractObject pOther);
  public abstract boolean matchSpecificShape(SMGAbstractObject pOther);

  public HashSet<ReadableSMG> concretise(ReadableSMG pSmg){
    SMGConcretisation concretisation = createConcretisation();
    if (concretisation != null)
      return concretisation.execute(pSmg);
    else
      return Sets.newHashSet(pSmg);
  }

  protected abstract SMGConcretisation createConcretisation();
}
