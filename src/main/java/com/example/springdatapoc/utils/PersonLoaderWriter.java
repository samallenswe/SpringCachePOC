package com.example.springdatapoc.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonLoaderWriter<K, V> implements CacheLoaderWriter<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonLoaderWriter.class);

    private final Map<K, V> data = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PersonLoaderWriter() {
      this(Collections.<K, V>emptyMap());
    }

    public PersonLoaderWriter(Map<K, V> initialData) {
      data.putAll(initialData);
    }

    public void clear() {
      data.clear();
    }

    @Override
    public V load(K key) {
      lock.readLock().lock();
      try {
        V value = data.get(key);
        LOGGER.info("Key - '{}', Value - '{}' successfully loaded", key, value);
        return value;
      } finally {
        lock.readLock().unlock();
      }
    }

    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) {
      throw new UnsupportedOperationException("Implement me!");
    }

    @Override
    public void write(K key, V value) {
      lock.writeLock().lock();
      try {
        data.put(key, value);
        LOGGER.info("Key - '{}', Value - '{}' successfully written", key, value);
      } finally {
        lock.writeLock().unlock();
      }
    }

    @Override
    public void writeAll(Iterable<? extends Map.Entry<? extends K, ? extends V>> entries) {
      lock.writeLock().lock();
      try {
        for (Map.Entry<? extends K, ? extends V> entry : entries) {
          data.put(entry.getKey(), entry.getValue());
          LOGGER.info("Key - '{}', Value - '{}' successfully written in batch", entry.getKey(), entry.getValue());
        }
      } finally {
        lock.writeLock().unlock();
      }
    }

    @Override
    public void delete(K key) {
      lock.writeLock().lock();
      try {
        data.remove(key);
        LOGGER.info("Key - '{}' successfully deleted", key);
      } finally {
        lock.writeLock().unlock();
      }
    }

    @Override
    public void deleteAll(Iterable<? extends K> keys) {
      lock.writeLock().lock();
      try {
        for (K key : keys) {
          data.remove(key);
          LOGGER.info("Key - '{}' successfully deleted in batch", key);
        }
      } finally {
        lock.writeLock().unlock();
      }
    }
}
