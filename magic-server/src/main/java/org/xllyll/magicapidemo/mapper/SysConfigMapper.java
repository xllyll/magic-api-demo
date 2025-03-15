package org.xllyll.magicapidemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.xllyll.magicapidemo.annotation.TargetDataSource;
import org.xllyll.magicapidemo.model.SysConfig;

import java.util.List;

@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    @TargetDataSource("test")
    @Select("select * from sys_config")
    List<SysConfig> getAll();

}
