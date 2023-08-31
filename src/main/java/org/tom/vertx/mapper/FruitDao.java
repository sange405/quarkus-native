package org.tom.vertx.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.tom.vertx.entity.FruitEntity;

import java.util.List;

@Mapper
public interface FruitDao {
    List<FruitEntity> listAll();
}
