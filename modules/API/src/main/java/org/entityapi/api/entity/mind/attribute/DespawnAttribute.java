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

package org.entityapi.api.entity.mind.attribute;

import org.entityapi.api.entity.DespawnReason;
import org.entityapi.api.entity.mind.Attribute;
import org.entityapi.api.events.ControllableEntityDeathEvent;
import org.entityapi.api.events.ControllableEntityDespawnEvent;

public abstract class DespawnAttribute extends Attribute<ControllableEntityDespawnEvent> {

    @Override
    protected ControllableEntityDespawnEvent call(ControllableEntityDespawnEvent event) {
        onDespawn(event.getReason());
        return event;
    }

    @Override
    protected ControllableEntityDespawnEvent getNewEvent(Object... args) {
        DespawnReason reason = (DespawnReason) args[1];
        return reason == DespawnReason.DEATH ? new ControllableEntityDeathEvent(getControllableEntity()) : new ControllableEntityDespawnEvent(getControllableEntity(), reason);
    }

    public abstract void onDespawn(DespawnReason reason);

    @Override
    public String getKey() {
        return "Death";
    }
}