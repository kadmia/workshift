package com.adambritt.workshift.resources;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;

import com.adambritt.workshift.api.Shift;
import com.adambritt.workshift.api.User;
import com.adambritt.workshift.db.ShiftDao;

import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@Path("/shift")
@Produces(MediaType.APPLICATION_JSON)
public class ShiftResource {
	
	private static final int MAX_WORK_TIME_MINUTES = 8 * 60;
	private static final int MAX_WORK_TIME_WINDOW_MINUTES = 24 * 60;
	private static final int MAX_CONSECUTIVE_DAYS = 5;
	
	
	private ShiftDao shiftDao;
	
	public ShiftResource(ShiftDao shiftDao) {
		this.shiftDao = shiftDao;
	}
	
	@POST
	public Shift addShift(@Valid Shift.Add shift) {
		long id = shiftDao.addShift(shift);
		return shiftDao.getShiftById(id);
	}

	@GET
	@Path("/{id}")
	public Shift getShift(@PathParam("id") long id) {
		return shiftDao.getShiftById(id);
	}
	
	@GET
	@Path("/{id}/users")
	public List<User> getUsersForShift(@PathParam("id") long id) {
		return shiftDao.getUsersforShift(id);
	}
	
	@PUT
	@Path("/{shiftId}/user/{userId}")
	public Shift addUserToShift(@PathParam("shiftId") long shiftId, @PathParam("userId") long userId) {
		validateShift(shiftId, userId);
		try {
			shiftDao.addUserToShift(shiftId, userId);
		}
		catch (UnableToExecuteStatementException e) {
			if (e.getCause().getMessage().contains("UQ_SHIFTS_USERS")) {
				throw new WebApplicationException("User already assigned to that shift", Status.OK);
			}
			else if (e.getCause().getMessage().contains("FK_SHIFTID_USERID")) {
				throw new WebApplicationException("Unknown shift", Status.BAD_REQUEST);
			}
			else if (e.getCause().getMessage().contains("FK_SHOPID")) {
				throw new WebApplicationException("Unknown shop", Status.BAD_REQUEST);
			}
			throw e;
		}
		Shift shift = shiftDao.getShiftById(shiftId);
		//A user is added to a shop if he works a shift there
		try {
			shiftDao.addUserToShop(shift.getShop().getId(), userId);
		}
		catch (UnableToExecuteStatementException e) {
			//do nothing
		}
		return shift;
	}
	
	@GET
	@Path("all")
	public List<Shift> getAll() {
		return shiftDao.getAllShifts();
	}
	
	private void validateShift(long shiftId, long userId) {
		Shift shift = shiftDao.getShiftById(shiftId);
		List<Shift> userShifts = shiftDao.getShiftsForUser(userId);
		
		tooManyConsecutiveDays(shift, userShifts);
		overlappingShifts(shift, userShifts);
		shiftTooLongWithinWindow(shift, userShifts);
	}

	private void tooManyConsecutiveDays(Shift shift, List<Shift> userShifts) {
		List<Integer> days = new ArrayList<Integer>();
		int shiftDay = shift.getStartTS().getDayOfYear();
		days.add(shiftDay);
		for (Shift userShift: userShifts) {
			if (shift.getStartTS().getYear() == userShift.getStartTS().getYear()) {
				int currentDay = userShift.getStartTS().getDayOfYear(); 
				if (currentDay + MAX_CONSECUTIVE_DAYS > shiftDay || currentDay - MAX_CONSECUTIVE_DAYS < shiftDay) {
					days.add(currentDay);
				}
			}
		}
		
		Collections.sort(days, (c1, c2) -> c1 - c2);
		
		int consecutive = 1;
		int previous = -1;
		for (Integer i: days) {
			if (i - previous == 1) {
				consecutive += 1;
			}
			else {
				consecutive = 1;
			}
			if (consecutive == MAX_CONSECUTIVE_DAYS) {
				throw new WebApplicationException("The requested shift makes too many consecutive days", Status.BAD_REQUEST);
			}
		}
		
	}

	private void overlappingShifts(Shift shift, List<Shift> userShifts) {
		for (Shift userShift: userShifts) {
			if (shift.getStartTS().isBefore(userShift.getEndTS()) && userShift.getEndTS().isBefore(shift.getEndTS())) {
				throw new WebApplicationException("The requested shift overlaps with " + userShift.prettyPrint(), Status.BAD_REQUEST);
			}
		}
	}

	private void shiftTooLongWithinWindow(Shift shift, List<Shift> userShifts) {
		long shiftDuration = shiftLength(shift);
		//Check if the shift is too long
		if (shiftDuration > MAX_WORK_TIME_MINUTES) {
			throw new WebApplicationException("The shift is too long. The max number of minutes is: " + MAX_WORK_TIME_MINUTES, Status.BAD_REQUEST);
		}
		
		//Get a list of possible shifts that could be problematic
		List<Shift> shiftsInWindow = new ArrayList<Shift>();
		long shiftEdge = MAX_WORK_TIME_WINDOW_MINUTES - shiftDuration;
		for (Shift userShift: userShifts) {
			if (shift.getStartTS().minusMinutes(shiftEdge).isBefore(userShift.getEndTS()) && userShift.getEndTS().isBefore(shift.getEndTS().plusMinutes(shiftEdge))) {
				shiftsInWindow.add(userShift);
			}
		}
		Collections.sort(shiftsInWindow, (c1, c2) -> c1.getStartTS().compareTo(c2.getStartTS()));
		
		//Aggregate each shift with shifts within its window and check if we are over the limit
		for (int i = 0; i < shiftsInWindow.size(); i++) {
			Shift currentShift = shiftsInWindow.get(i);
			long currentShiftLength = shiftLength(currentShift);
			long sum = currentShiftLength;
			for (int j = i; j < shiftsInWindow.size(); j++) {
				Shift otherShift = shiftsInWindow.get(j);
				if (currentShift.getEndTS().plusMinutes(MAX_WORK_TIME_WINDOW_MINUTES - currentShiftLength).isAfter(otherShift.getStartTS())) {
					break;
				}
				sum += shiftLength(otherShift);
			}
			if (sum > MAX_WORK_TIME_MINUTES) {
				throw new WebApplicationException("The shift is more than 8 hours within a 24 hour period ", Status.BAD_REQUEST);
			}
		}
	}
	
	private long shiftLength(Shift shift) {
		return Duration.between(shift.getStartTS(), shift.getEndTS()).toMinutes();
	}
}
