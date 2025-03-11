package org.xllyll.magicapidemo.controller;

import ch.qos.logback.core.db.dialect.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ssssssss.magicapi.datasource.model.MagicDynamicDataSource;
import org.ssssssss.magicapi.modules.db.mybatis.MybatisParser;
import org.xllyll.magicapidemo.mapper.SysConfigMapper;
import org.xllyll.magicapidemo.model.SysConfig;

import javax.annotation.Resource;
import java.sql.CallableStatement;
import java.sql.SQLException;
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
        JdbcTemplate jdbcTemplate = magicDynamicDataSource.getDataSource("ai").getJdbcTemplate();
        return jdbcTemplate.execute("select * from sys_config", new CallableStatementCallback<String>() {
            @Override
            public String doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
                return "";
            }
        });
    }
    @RequestMapping("/db")
    public Object db(){
        List<SysConfig> cs = sysConfigMapper.getAll();
        return cs;
    }
}
