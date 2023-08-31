package org.tom.vertx.mapper;

import org.apache.ibatis.annotations.*;
import org.tom.vertx.entity.FruitEntity;

@Mapper
public interface FruitMapper {
    @Select("SELECT * FROM fruits WHERE id = #{id}")
    FruitEntity getById(Long id);

    @Insert("INSERT INTO fruits (id, name) VALUES (#{id}, #{name})")
    Integer create(@Param("id") Long id, @Param("name") String name);

    @Update("update fruits set name= #{name} where id = #{id}")
    Integer update(@Param("id") Long id, @Param("name") String name);

    @Delete("DELETE FROM fruits WHERE id = #{id}")
    Integer remove(Long id);
}
