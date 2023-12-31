package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;
    //참고로 생성자가 한개있으면 스프링빈으로 등록되있으면 Autowird 생략가능
    public JdbcTemplateMemberRepository(DataSource dataSource) {
        jdbcTemplate=new JdbcTemplate(dataSource);
    }


    @Override
    public Member Save(Member member) {
        SimpleJdbcInsert jdbcInsert= new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName("member").usingGeneratedKeyColumns("id");

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("name",member.getName());

        Number key=jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());
        return member;


    }

    @Override
    public Optional<Member> findbyid(Long id) {
        List<Member> result = jdbcTemplate.query("select * from member where id =?", memberRowMapper(),id);
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findbyname(String name) {
        List<Member> result= jdbcTemplate.query("select * from member where name=?",memberRowMapper(),name);
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return jdbcTemplate.query("selct * from member",memberRowMapper());
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member=new Member();
            member.setId(rs.getLong("id"));
            member.setName(rs.getString("name"));
            return member;
        };
    }
}
