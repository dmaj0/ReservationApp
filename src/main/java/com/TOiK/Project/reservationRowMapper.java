package com.TOiK.Project;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class reservationRowMapper implements RowMapper<ModelRes> {
    @Override
    public ModelRes mapRow(ResultSet rs, int rowNum) throws SQLException {
        ModelRes modelRes = new ModelRes();
        modelRes.setName(rs.getString("imie"));
        modelRes.setSurname(rs.getString("nazwisko"));
        modelRes.setEmail(rs.getString("email"));
        modelRes.setTable(rs.getString("stol"));
        modelRes.setTime(rs.getString("timepicker"));
        modelRes.setDay(rs.getString("daypicker"));
        modelRes.setMonth(rs.getString("monthpicker"));
        modelRes.setYear(rs.getString("yearpicker"));
        return modelRes;
    }
}