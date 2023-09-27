package com.adambritt.workshift.db;

import java.util.List;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import com.adambritt.workshift.api.User;

@RegisterBeanMapper(User.class)
public interface UserDao {

	@SqlUpdate("create table if not exists users("
			+ "  id identity primary key"
			+ ", name varchar(255) not null);")
	public void createUserTable();
	
	@SqlUpdate("INSERT INTO users (NAME) VALUES(:name)")
	@GetGeneratedKeys("id")
	public int addUser(@BindBean User user);
	
	@SqlQuery("SELECT id, name FROM users WHERE id = :id")
	public User getUserById(@Bind("id") long id);
	
	@SqlQuery("SELECT id, name FROM users")
	public List<User> getAllUsers();
}
