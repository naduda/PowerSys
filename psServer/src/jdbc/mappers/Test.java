package jdbc.mappers;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

public class Test
{
    public static String getQuery(String query)
    {
        return query;
    }

    public interface TestMapper
    {
        @SelectProvider(type = Test.class, method = "getQuery")
        void update(@Param("query") String query);
    }
}
