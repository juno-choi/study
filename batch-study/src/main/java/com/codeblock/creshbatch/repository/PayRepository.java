package com.codeblock.creshbatch.repository;

import com.codeblock.creshbatch.domain.dao.Pay;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PayRepository {
    @Qualifier("serviceDb")
    private JdbcTemplate jdbc;

    public List<Pay> findAll(){
        String sql = "select * from pay";
        return jdbc.query(sql, rowMapper);
    }
    public List<Pay> findAllByState(){
        String sql = "select * from pay where success_state = 'true'";
        return jdbc.query(sql, rowMapper);
    }

    public int save(Pay pay){
        return jdbc.update("insert into pay (amount, tx_name, tx_date_time, success_state) values (?,?,?,?)", pay.getAmount(), pay.getTxName(), pay.getTxDateTime(), pay.getSuccessState());
    }

    static RowMapper<Pay> rowMapper = (rs, rowNum) -> new Pay(
            rs.getLong("id"),
            rs.getLong("amount"),
            rs.getString("tx_name"),
            rs.getString("tx_date_time"),
            rs.getString("success_state")
    );

}
