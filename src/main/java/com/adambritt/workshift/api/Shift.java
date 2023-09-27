package com.adambritt.workshift.api;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jdbi.v3.core.mapper.Nested;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Shift {

	private long id;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private ZonedDateTime startTS;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private ZonedDateTime endTS;
	@Nested
	private Shop shop;

	public Shift() { }

	public Shift(long id, ZonedDateTime startTS, ZonedDateTime endTS, Shop shop, List<User> users) {
		super();
		this.id = id;
		this.startTS = startTS;
		this.endTS = endTS;
		this.shop = shop;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ZonedDateTime getStartTS() {
		return startTS;
	}

	public void setStartTS(ZonedDateTime startTS) {
		this.startTS = startTS;
	}

	public ZonedDateTime getEndTS() {
		return endTS;
	}

	public void setEndTS(ZonedDateTime endTS) {
		this.endTS = endTS;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public static class Add {
		
		private ZonedDateTime startTS;
		private ZonedDateTime endTS;
		private long shopId;

		public ZonedDateTime getStartTS() {
			return startTS;
		}

		public ZonedDateTime getEndTS() {
			return endTS;
		}

		public long getShopId() {
			return shopId;
		}
	}
	
	public String prettyPrint() {
		return "Shift at " + shop.getName() + startTS.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) + " - " + endTS.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}
}
