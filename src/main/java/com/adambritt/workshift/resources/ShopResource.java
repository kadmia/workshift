package com.adambritt.workshift.resources;

import java.util.List;

import com.adambritt.workshift.api.Shift;
import com.adambritt.workshift.api.Shop;
import com.adambritt.workshift.api.User;
import com.adambritt.workshift.db.ShopDao;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/shop")
@Produces(MediaType.APPLICATION_JSON)
public class ShopResource {
	
	private ShopDao shopDao;
	
	public ShopResource(ShopDao shopDao) {
		this.shopDao = shopDao;
	}
	
	@POST
	public Shop addShop(@Valid Shop shop) {
		long id = shopDao.addUser(shop);
		return shopDao.getShopById(id);
	}

	@GET
	@Path("/{id}")
	public Shop getShop(@PathParam("id") long id) {
		return shopDao.getShopById(id);
	}
	
	@GET
	@Path("/{id}/users")
	public List<User> getUsersForShop(@PathParam("id") long id) {
		return shopDao.getUsersforShop(id);
	}
	
	@GET
	@Path("/{id}/shifts")
	public List<Shift> getShiftsForShop(@PathParam("id") long id) {
		return shopDao.getShiftsforShop(id);
	}
	
	@GET
	@Path("all")
	public List<Shop> getAll() {
		return shopDao.getAllShops();
	}
	

}
