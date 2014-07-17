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

package org.entityapi.api.events;

import org.bukkit.event.HandlerList;
import org.entityapi.api.entity.ControllableEntity;
import org.entityapi.api.entity.DespawnReason;

/**
 * Called when a ControllableEntity dies
 */
public class ControllableEntityDeathEvent extends ControllableEntityEvent {

    private static final HandlerList handlers = new HandlerList();

    private DespawnReason reason;

    public ControllableEntityDeathEvent(final ControllableEntity controllableEntity, DespawnReason reason) {
        super(controllableEntity);
    }

    public DespawnReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}