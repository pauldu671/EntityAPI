/*
 * Copyright (C) EntityAPI Team
 *
 * This file is part of EntityAPI.
 *
 * EntityAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EntityAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with EntityAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.entityapi.api.entity.mind;

import org.entityapi.api.entity.ControllableEntity;
import org.entityapi.api.entity.mind.behaviour.Behaviour;
import org.entityapi.api.entity.mind.behaviour.BehaviourItem;
import org.entityapi.api.entity.mind.behaviour.IBehaviourSelector;
import org.entityapi.api.entity.mind.behaviour.OneTimeBehaviourGoal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BehaviourSelector implements IBehaviourSelector {

    private Map<String, BehaviourItem> behaviourMap = new HashMap<>();
    private ArrayList<BehaviourItem> behaviours = new ArrayList<>();
    private ArrayList<BehaviourItem> activeBehaviours = new ArrayList<>();
    private ControllableEntity controllableEntity;
    private int delay = 0;

    public BehaviourSelector(ControllableEntity entity) {
        this.controllableEntity = entity;
    }

    @Override
    public void addBehaviour(Behaviour behaviour, int priority) {
        this.addBehaviour(behaviour.getGoal().getDefaultKey(), behaviour, priority);
    }

    @Override
    public void addBehaviour(String key, Behaviour behaviour, int priority) {
        key = key.toLowerCase();
        BehaviourItem behaviourItem = new BehaviourItem(priority, behaviour);
        if (this.behaviourMap.containsKey(key)) {
            return;
        }
        this.behaviourMap.put(key, behaviourItem);
        this.behaviours.add(behaviourItem);
    }

    @Override
    public void addAndReplaceBehaviour(String key, Behaviour behaviour, int priority) {
        key = key.toLowerCase();
        if (this.behaviourMap.containsKey(key)) {
            this.removeBehaviour(key);
        }
        this.addBehaviour(key, behaviour, priority);
    }

    @Override
    public void removeBehaviour(Behaviour behaviour) {
        Iterator<BehaviourItem> iterator = this.behaviours.iterator();

        while (iterator.hasNext()) {
            BehaviourItem behaviourItem = iterator.next();
            Behaviour behaviour1 = behaviourItem.getBehaviour();

            if (behaviour1 == behaviour) {
                if (this.activeBehaviours.contains(behaviourItem)) {
                    behaviour1.getGoal().finish();
                    this.activeBehaviours.remove(behaviourItem);
                }

                iterator.remove();
            }
        }
    }

    @Override
    public void removeBehaviour(String key) {
        key = key.toLowerCase();
        Iterator<Map.Entry<String, BehaviourItem>> iterator = this.behaviourMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, BehaviourItem> entry = iterator.next();
            BehaviourItem behaviourItem = entry.getValue();
            Behaviour behaviour1 = behaviourItem.getBehaviour();

            if (key.equals(entry.getKey())) {
                if (this.activeBehaviours.contains(behaviourItem)) {
                    behaviour1.getGoal().finish();
                    this.activeBehaviours.remove(behaviourItem);
                }
                if (this.behaviours.contains(behaviourItem)) {
                    this.behaviours.remove(behaviourItem);
                }

                iterator.remove();
            }
        }
    }

    @Override
    public void clearBehaviours() {
        this.behaviourMap.clear();
        this.behaviours.clear();

        Iterator<BehaviourItem> iterator = this.activeBehaviours.iterator();

        while (iterator.hasNext()) {
            iterator.next().getBehaviour().getGoal().finish();
        }
        this.activeBehaviours.clear();
    }

    @Override
    public Behaviour getBehaviour(String key) {
        Iterator<Map.Entry<String, BehaviourItem>> iterator = this.behaviourMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, BehaviourItem> entry = iterator.next();
            BehaviourItem behaviourItem = entry.getValue();
            Behaviour behaviour = behaviourItem.getBehaviour();

            if (key.equalsIgnoreCase(entry.getKey())) {
                return behaviour;
            }
        }
        return null;
    }

    protected void updateBehaviours() {
        Iterator<BehaviourItem> iterator;
        if (this.delay++ % 3 == 0) {
            iterator = this.behaviours.iterator();

            while (iterator.hasNext()) {
                BehaviourItem behaviourItem = iterator.next();
                if (this.activeBehaviours.contains(behaviourItem)) {
                    if (this.canUse(behaviourItem) && behaviourItem.getBehaviour().getGoal().shouldContinue()) {
                        continue;
                    }
                    behaviourItem.getBehaviour().getGoal().finish();
                    this.activeBehaviours.remove(behaviourItem);
                    if (behaviourItem.getBehaviour() instanceof OneTimeBehaviourGoal && ((OneTimeBehaviourGoal) behaviourItem.getBehaviour()).isFinished()) {
                        this.behaviours.remove(behaviourItem);
                    }
                } else {
                    if (this.canUse(behaviourItem) && behaviourItem.getBehaviour().getGoal().shouldStart()) {
                        behaviourItem.getBehaviour().getGoal().start();
                        this.activeBehaviours.add(behaviourItem);
                    }
                }

            }

            this.delay = 0;
        } else {
            iterator = this.activeBehaviours.iterator();

            while (iterator.hasNext()) {
                BehaviourItem behaviourItem = iterator.next();
                if (!behaviourItem.getBehaviour().getGoal().shouldContinue()) {
                    behaviourItem.getBehaviour().getGoal().finish();
                    if (behaviourItem.getBehaviour() instanceof OneTimeBehaviourGoal && ((OneTimeBehaviourGoal) behaviourItem.getBehaviour()).isFinished()) {
                        this.behaviours.remove(behaviourItem);
                    }
                    iterator.remove();
                }
            }
        }

        iterator = this.activeBehaviours.iterator();

        while (iterator.hasNext()) {
            BehaviourItem behaviourItem = iterator.next();
            behaviourItem.getBehaviour().getGoal().tick();
            if (behaviourItem.getBehaviour() instanceof OneTimeBehaviourGoal && ((OneTimeBehaviourGoal) behaviourItem.getBehaviour()).isFinished()) {
                this.behaviours.remove(behaviourItem);
            }
        }
    }

    private boolean canUse(BehaviourItem behaviourItem) {
        Iterator<BehaviourItem> iterator = this.behaviours.iterator();

        while (iterator.hasNext()) {
            BehaviourItem behaviourItem1 = iterator.next();
            if (behaviourItem1 != behaviourItem) {
                if (behaviourItem.getPriority() > behaviourItem1.getPriority()) {
                    if (!this.areCompatible(behaviourItem, behaviourItem1) && this.activeBehaviours.contains(behaviourItem1)) {
                        return false;
                    }
                    //goal.i() -> isContinuous
                } else if (!behaviourItem1.getBehaviour().getGoal().isContinuous() && this.activeBehaviours.contains(behaviourItem1)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean areCompatible(BehaviourItem behaviourItem, BehaviourItem behaviourItem1) {
        return behaviourItem.getBehaviour().getGoal().getType().isCompatibleWith(behaviourItem1.getBehaviour().getGoal().getType());
    }
}