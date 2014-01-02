package io.snw.entityapi.api.mind;

import io.snw.entityapi.api.ControllableEntity;
import io.snw.entityapi.api.mind.attribute.Attribute;
import io.snw.entityapi.api.mind.behaviour.BehaviourSelector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Mind {

    protected ControllableEntity controllableEntity;
    protected HashMap<String, Attribute> attributes = new HashMap<>();

    protected BehaviourSelector behaviourSelector;

    public Mind(ControllableEntity controllableEntity) {
        this.controllableEntity = controllableEntity;
        this.behaviourSelector = new BehaviourSelector(this.controllableEntity);
    }

    public ControllableEntity getControllableEntity() {
        return controllableEntity;
    }

    public HashMap<String, Attribute> getAttributes() {
        return attributes;
    }

    public BehaviourSelector getBehaviourSelector() {
        return behaviourSelector;
    }

    public void addAttribute(Attribute attribute) {
        this.clearAttribute(attribute);
        this.attributes.put(attribute.getKey(), attribute);
    }

    public void clearAttribute(Attribute attribute) {
        Iterator<Map.Entry<String, Attribute>> i = this.attributes.entrySet().iterator();
        while (i.hasNext()) {
            if (i.next().getKey().equals(attribute.getKey())) {
                i.remove();
            }
        }
    }

    public Attribute getAttribute(String key) {
        for (String k : this.attributes.keySet()) {
            if (k.equals(key)) {
                return this.attributes.get(k);
            }
        }
        return null;
    }

    public boolean hasAttribute(String key) {
        for (String k : this.attributes.keySet()) {
            if (k.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void tick() {
        this.behaviourSelector.updateBehaviours();
        for (Attribute b : this.attributes.values()) {
            b.tick();
        }
    }
}