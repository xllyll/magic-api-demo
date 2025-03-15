package org.xllyll.magicapidemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;
import org.xllyll.magicapidemo.annotation.TargetDataSource;
import org.xllyll.magicapidemo.mapper.SysConfigMapper;
import org.xllyll.magicapidemo.model.SysConfig;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/openserver/text")
public class TextController {

    @Autowired
    private MagicDynamicDataSource magicDynamicDataSource;

    @Resource
    private SysConfigMapper sysConfigMapper;

    @RequestMapping("/test")
    public String test(){
//        JdbcTemplate jdbcTemplate = magicDynamicDataSource.getDataSource("ai").getJdbcTemplate();
//        return jdbcTemplate.execute("select * from sys_config", new CallableStatementCallback<String>() {
//            @Override
//            public String doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
//                return "";
//            }
//        });
        return "";
    }

    @TargetDataSource("ai")
    @RequestMapping("/db")
    public Object db(){
        SysConfig cs = sysConfigMapper.selectById(1);
        return cs;
    }

    @RequestMapping("/db2")
    public Object db2(){
        List<SysConfig> cs = sysConfigMapper.getAll();
        return cs;
    }
}
