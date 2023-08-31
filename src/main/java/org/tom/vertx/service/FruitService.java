package org.tom.vertx.service;

import org.tom.vertx.entity.FruitEntity;
import org.tom.vertx.mapper.FruitDao;
import org.tom.vertx.mapper.FruitMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class FruitService {
    @Inject
    private FruitMapper fruitMapper;
    @Inject
    private FruitDao fruitDao;

    public FruitEntity getById(Long id) {
        return fruitMapper.getById(id);
    }

    public Integer create(Long id, String name) {
        return fruitMapper.create(id, name);
    }

    public Integer update(Long id, String name) {
        return fruitMapper.update(id, name);
    }

    public Integer remove(Long id) {
        return fruitMapper.remove(id);
    }

    public List<FruitEntity> listAll() {
        return fruitDao.listAll();
    }
}
