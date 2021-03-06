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
package cz.afri.smg;

import java.util.ArrayList;
import java.util.List;

import cz.afri.smg.graphs.ReadableSMG;
import cz.afri.smg.objects.sll.SMGSingleLinkedListFinder;


public class SMGAbstractionManager {
  private ReadableSMG smg;
  private List<SMGAbstractionCandidate> abstractionCandidates = new ArrayList<>();

  public SMGAbstractionManager(final ReadableSMG pSMG) {
    smg = pSMG;
  }

  private boolean hasCandidates() {
    SMGSingleLinkedListFinder sllCandidateFinder = new SMGSingleLinkedListFinder();
    abstractionCandidates.addAll(sllCandidateFinder.traverse(smg));

    return (!abstractionCandidates.isEmpty());
  }

  private SMGAbstractionCandidate getBestCandidate() {
    return abstractionCandidates.get(0);
  }

  public final ReadableSMG execute() {
    while (hasCandidates()) {
      SMGAbstractionCandidate best = getBestCandidate();
      smg = best.execute(smg);
      invalidateCandidates();
    }
    return smg;
  }

  private void invalidateCandidates() {
    abstractionCandidates.clear();
  }
}
