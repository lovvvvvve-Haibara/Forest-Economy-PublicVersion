package edu.scau.forestproject.mapper;

import edu.scau.forestproject.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT id, username, phoneNumber, password FROM `User` WHERE (username = #{identifier} OR phoneNumber = #{identifier}) LIMIT 1")
    User selectByUsernameOrPhone(@Param("identifier") String identifier);

    @Delete("DELETE FROM `User` WHERE id = #{id}")
    void deleteByID(Integer id);

    @Insert("INSERT INTO `User` (username, phoneNumber, password) VALUES (#{username}, #{phoneNumber}, #{password})")
    void addUser(User user);

    @Select("SELECT COUNT(1) FROM `User` WHERE username = #{username} OR phoneNumber = #{phoneNumber}")
    int countByUsernameOrPhone(@Param("username") String username, @Param("phoneNumber") String phoneNumber);

    @Select("SELECT id, username, phoneNumber FROM `User` WHERE id = #{id} LIMIT 1")
    User selectById(Integer id);
}
