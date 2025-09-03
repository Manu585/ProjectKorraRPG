package com.projectkorra.rpg.modules.leveling.storage.registries;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LoadingCache<K, V> {
    private final HashMap<K, V> cache = new HashMap<>();
    private final ConcurrentHashMap<K, CompletableFuture<V>> inflight = new ConcurrentHashMap<>();

    protected abstract CompletableFuture<V> load(K key);

    public CompletableFuture<V> getOrLoad(K key) {
        V hit = cache.get(key);
        if (hit != null) return CompletableFuture.completedFuture(hit);

        return inflight.computeIfAbsent(key, k ->
            load(k).whenComplete((value, exception) -> {
                inflight.remove(k);
                if (exception == null && value != null) cache.put(k, value);
            })
        );
    }

    public V getIfPresent(K key) {
        return cache.get(key);
    }

    public void put(K key, V value) {
        cache.put(key, value);
    }

    public void invalidate(K key) {
        cache.remove(key);
    }

    public Map<K, V> view() {
        return Collections.unmodifiableMap(cache);
    }
}
