package org.xllyll.magicapidemo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.ssssssss.magicapi.datasource.annotation.TargetDataSource;
import org.xllyll.magicapidemo.model.SysConfig;

import java.util.List;

@Mapper
public interface SysConfigMapper {

    @TargetDataSource("test")
    @Select("select * from sys_config")
    List<SysConfig> getAll();

}
