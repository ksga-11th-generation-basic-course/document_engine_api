package kh.com.kshrd.docengine.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@MappedTypes({Map.class})
public class JsonTypeHandler extends BaseTypeHandler<Map<String, Object>> {

    ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Map<String, Object> config, JdbcType jdbcType) throws SQLException {
        try {
            preparedStatement.setObject(i, objectMapper.writeValueAsString(config));

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        if (resultSet.getString(s) != null) {
            try {
                return objectMapper.readValue(resultSet.getString(s), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        if (resultSet.getString(i) != null) {
            try {
                return objectMapper.readValue(resultSet.getString(i), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        if (callableStatement.getString(i) != null) {
            try {
                return objectMapper.readValue(callableStatement.getString(i), Map.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

