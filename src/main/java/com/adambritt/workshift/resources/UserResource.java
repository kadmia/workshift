package com.adambritt.workshift.resources;


import java.util.List;

import com.adambritt.workshift.api.User;
import com.adambritt.workshift.db.UserDao;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
	
	private UserDao userDao;
	
	public UserResource(UserDao userDao) {
		this.userDao = userDao;
	}

	@POST
	public User addUser(@Valid User user) {
		long id = userDao.addUser(user);
		return userDao.getUserById(id);
	}
	
	@GET
	@Path("/{id}")
	public User getUser(@PathParam("id") long id) {
		return userDao.getUserById(id);
	}
	
	@GET
	@Path("all")
	public List<User> getAll() {
		return userDao.getAllUsers();
	}
}
