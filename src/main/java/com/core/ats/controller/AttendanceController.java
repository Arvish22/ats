package com.core.ats.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.core.ats.model.Attendance;
import com.core.ats.model.User;
import com.core.ats.service.AttendanceService;
import com.core.ats.service.UserService;;

@Controller
public class AttendanceController {
	
	@Autowired
	AttendanceService attendanceService;
	
	@Autowired
	UserService userService;
	
	//listattendance
	
	@RequestMapping(value = { "/", "/attendanceList" }, method = RequestMethod.GET)
	public String listUsers(ModelMap model) {
		
		User user = userService.findBySSO(getPrincipal());
		List<Attendance> attendance = attendanceService.findByUserId(user.getId());
		model.addAttribute("attendance", attendance);
		model.addAttribute("loggedinuser", getPrincipal());
		return "attendanceList";
	}
	
	
	
	@RequestMapping(value = { "/newattendance" }, method = RequestMethod.POST)
	public String saveAttendance(@Valid Attendance attendance, BindingResult result,
			ModelMap model) {

		if (result.hasErrors()) {
			return "addattendance";
		}
		
		System.out.println("adding attendance");
		User user = userService.findBySSO(getPrincipal());
		attendance.setUserId(user.getId());
		attendanceService.saveAttendance(attendance);
		
		model.addAttribute("success", "Attendance updated successfully");
		return "addattendancesuccess";
	}
	
	
	/**
	 * This method will provide the medium to add a new attendance.
	 */
	@RequestMapping(value = { "/newattendance" }, method = RequestMethod.GET)
	public String newAttendance(ModelMap model) {
		Attendance attendance = new Attendance();
		model.addAttribute("attendance", attendance);
		model.addAttribute("edit", false);
		model.addAttribute("loggedinuser", getPrincipal());
		return "addattendance";
	}
	
	@RequestMapping(value = { "/edit-attendance-{id}" }, method = RequestMethod.GET)
	public String editUser(@PathVariable int id, ModelMap model) {
		Attendance attendance = attendanceService.findById(id);
		model.addAttribute("attendance", attendance);
		model.addAttribute("edit", true);
		model.addAttribute("loggedinuser", getPrincipal());
		return "addattendance";
	}
	
	/**
	 * This method will be called on form submission, handling POST request for
	 * updating user in database. It also validates the user input
	 */
	@RequestMapping(value = { "/edit-attendance-{id}" }, method = RequestMethod.POST)
	public String updateUser(@Valid Attendance attendance, BindingResult result,
			ModelMap model, @PathVariable int id) {

		if (result.hasErrors()) {
			return "addattendance";
		}

		attendanceService.updateAttendance(attendance);

		model.addAttribute("success"," updated successfully");
		model.addAttribute("loggedinuser", getPrincipal());
		return "addattendancesuccess";
	}
	
	@RequestMapping(value = { "/delete-attendance-{id}" }, method = RequestMethod.GET)
	public String deleteUser(@PathVariable int id) {
		attendanceService.deleteById(id);
		return "redirect:/attendanceList";
	}
	
	/**
	 * This method returns the principal[user-name] of logged-in user.
	 */
	private String getPrincipal(){
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			userName = ((UserDetails)principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}
}