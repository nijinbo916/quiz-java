package com.tfzhang.quiz.mapper;

import com.tfzhang.quiz.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户表的 MyBatis 映射接口。
 *
 * 只需要写 interface，不用写实现类：@Mapper 让 MyBatis 在运行时生成实现对象并交给 Spring 容器管理，
 * SQL 通过注解绑定到方法上（也可以用 UserMapper.xml）。
 *
 * 注意：所有查询都带 isDelete = 0，被逻辑删除的用户不再出现在结果里。
 */
@Mapper
public interface UserMapper {

    /** 查询全部未删除的用户 */
    @Select("select * from user where isDelete = 0 order by id")
    List<User> list();

    /** 按 id 查询单个用户 */
    @Select("select * from user where id = #{id} and isDelete = 0")
    User getById(Long id);

    /** 按用户名模糊查询（like '%关键字%'） */
    @Select("select * from user where isDelete = 0 and userName like concat('%', #{keyword}, '%') order by id")
    List<User> findByName(@Param("keyword") String keyword);

    /** 新增用户，并把数据库自增主键回填到 user.id */
    @Insert("insert into user(userName, userPassword, isDelete, userRole, createTime, updateTime) " +
            "values(#{userName}, #{userPassword}, #{isDelete}, #{userRole}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int saveUser(User user);

    /** 统计同名且未删除的用户数量，用于注册时判断用户名是否已被占用 */
    @Select("select count(*) from user where userName = #{username} and isDelete = 0")
    int existsByName(@Param("username") String username);

    /** 统计未删除的用户总数（分页用） */
    @Select("select count(*) from user where isDelete = 0")
    int count();

    /**
     * 分页查询：limit 后面两个参数，第一个是起始行下标（从 0 开始），第二个是每页条数。
     * 起始行 = (当前页码 - 1) * 每页条数，由 Service 层算好再传进来。
     */
    @Select("select * from user where isDelete = 0 order by id limit #{start}, #{pageSize}")
    List<User> page(@Param("start") Integer start, @Param("pageSize") Integer pageSize);

    /** 按 id 逻辑删除：只把 isDelete 置 1，数据仍然留在表里 */
    @Update("update user set isDelete = 1, updateTime = now() where id = #{id} and isDelete = 0")
    int deleteUserById(@Param("id") Long id);

    /** 按用户名逻辑删除 */
    @Update("update user set isDelete = 1, updateTime = now() where userName = #{username} and isDelete = 0")
    int deleteUserByUsername(@Param("username") String username);

    /** 物理删除：真把数据从表里删掉（老师留的作业），接口已做保护，只允许删已逻辑删除的数据 */
    @Update("delete from user where id = #{id} and isDelete = 1")
    int hardDeleteById(@Param("id") Long id);

    /** 更新用户名与角色 */
    @Update("update user set userName = #{userName}, userRole = #{userRole}, updateTime = now() where id = #{id}")
    int update(User user);
}
