/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers;

import java.util.*;

import authentication.ActionAuthenticator;
import models.Following;
import models.FollowingRepository;
import models.User;
import models.UserRepository;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class UserController extends Controller {

	private final UserRepository userRepository;
	private final FollowingRepository followingRepository;
	private SearchController searchController;
	private MLController mlController;
	private long latestID = 0;
	// We are using constructor injection to receive a repository to support our
	// desire for immutability.
	@Inject
	public UserController(final UserRepository userRepository, final FollowingRepository followingRepository, SearchController searchController,
						  MLController mlController) {
		this.userRepository = userRepository;
		this.followingRepository = followingRepository;
		this.searchController=searchController;
		this.mlController = mlController;
		this.mlController.start();
		this.latestID=userRepository.latestID();
	}

	public Result addUser() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("User not created, expecting Json data");
			return badRequest("User not created, expecting Json data");
		}

		// Parse JSON file
		String userName = json.path("username").asText();
		String password = json.path("password").asText();
		String firstName = json.path("firstName").asText();
		String lastName = json.path("lastName").asText();
		String affiliation = json.path("affiliation").asText();
		String token = json.path("token").asText();

		try {
			if (userRepository.findByUserName(userName).size() > 0) {
				System.out.println("UserName has been used: " + userName);
				return badRequest("UserName has been used");
			}
			User user = new User(userName, password, firstName, lastName, affiliation, token);
			userRepository.save(user);
			System.out.println("User saved: " + user.getId());
			latestID++;
			searchController.appendUser(latestID, user);
			return created(new Gson().toJson(user.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("User not saved: " + firstName + " " + lastName);
			return badRequest("User not saved: " + firstName + " " + lastName);
		}
	}

	// No need to do authentication for this method since we are logging in
	public Result loginUser() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("User info not present, expecting json data");
			return badRequest("User info not present, expecting json data");
		}

		String userName = json.path("username").asText();
		String password = json.path("password").asText();

		List<User> users = userRepository.findByUserName(userName);
		if (users.size() == 0) {
			System.out.println("User doesn't exist");
			return badRequest("User doesn't exist");
		}

		User user = users.get(0);

		if (!password.equals(user.getPassword())) {
			System.out.println("Password is wrong");
			return badRequest("Password doesn't match");
		}

		String result = new Gson().toJson(user);
		return ok(result);

	}

	public Result deleteUser(Long id) {
		User deleteUser = userRepository.findOne(id);
		if (deleteUser == null) {
			System.out.println("User not found with id: " + id);
			return notFound("User not found with id: " + id);
		}

		userRepository.delete(deleteUser);
		System.out.println("User is deleted: " + id);
		return ok("User is deleted: " + id);
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result updateUser(long id) {
		request().headers().get("key");
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("User not saved, expecting Json data");
			return badRequest("User not saved, expecting Json data");
		}

		// Parse JSON file
		String firstName = json.path("firstName").asText();
		String lastName = json.path("lastName").asText();
		String middleInitial = json.path("middleInitial").asText();
		String affiliation = json.path("affiliation").asText();
		String title = json.path("title").asText();
		String email = json.path("email").asText();
		String mailingAddress = json.path("mailingAddress").asText();
		String phoneNumber = json.path("phoneNumber").asText();
		String faxNumber = json.path("faxNumber").asText();
		String researchFields = json.path("researchFields").asText();
		String highestDegree = json.path("highestDegree").asText();
		String url = json.path("url").asText();
		try {
			User updateUser = userRepository.findOne(id);

			updateUser.setFirstName(firstName);
			updateUser.setLastName(lastName);
			updateUser.setAffiliation(affiliation);
			updateUser.setEmail(email);
			updateUser.setFaxNumber(faxNumber);
			updateUser.setHighestDegree(highestDegree);
			updateUser.setMailingAddress(mailingAddress);
			updateUser.setMiddleInitial(middleInitial);
			updateUser.setPhoneNumber(phoneNumber);
			updateUser.setResearchFields(researchFields);
			updateUser.setTitle(title);
			updateUser.setUrl(url);

			User savedUser = userRepository.save(updateUser);
			searchController.updateUser(id, updateUser);
			System.out.println("User updated: " + savedUser.getFirstName() + " " + savedUser.getLastName());
			return created("User updated: " + savedUser.getFirstName() + " " + savedUser.getLastName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("User not updated: " + firstName + " " + lastName);
			return badRequest("User not updated: " + firstName + " " + lastName);
		}
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result getUser(Long id, String format) {
		if (id == null) {
			System.out.println("User id is null or empty!");
			return badRequest("User id is null or empty!");
		}

		User user = userRepository.findOne(id);

		if (user == null) {
			System.out.println("User not found with with id: " + id);
			return notFound("User not found with with id: " + id);
		}
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(user);
		}

		return ok(result);
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result getUserByName(String username, String format) {
		if (username == null) {
			System.out.println("username is null or empty!");
			return badRequest("username is null or empty!");
		}

		List<User> users = userRepository.findByUserName(username);
		if (users.size() == 0) {
			System.out.println("User is not existed");
			return badRequest("User is not existed");
		}

		User user = users.get(0);
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(user);
		}

		return ok(result);
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result getAllUsers(String format) {
		Iterable<User> userIterable = userRepository.findAll();
		List<User> userList = new ArrayList<User>();
		for (User user : userIterable) {
			userList.add(user);
		}
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(userList);
		}
		return ok(result);
	}

	public Result isUserValid() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("Cannot check user, expecting Json data");
			return badRequest("Cannot check user, expecting Json data");
		}
		String email = json.path("email").asText();
		String password = json.path("password").asText();
		User user = userRepository.findByEmail(email);
		if (user.getPassword().equals(password)) {
			System.out.println("User is valid");
			return ok("User is valid");
		} else {
			System.out.println("User is not valid");
			return badRequest("User is not valid");
		}
	}

	public Result deleteUserByUserNameandPassword(String userName, String password) {
		try {
			List<User> users = userRepository.findByUserName(userName);
			if (users.size() == 0) {
				System.out.println("User is not existed");
				return badRequest("User is not existed");
			}
			User user = users.get(0);
			if (user.getPassword().equals(password)) {
				System.out.println("User is deleted: " + user.getId());
				userRepository.delete(user);
				return ok("User is deleted");
			} else {
				System.out.println("User is not deleted for wrong password");
				return badRequest("User is not deleted for wrong password");
			}
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("User is not deleted");
			return badRequest("User is not deleted");
		}

	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result follow() {
		final Map<String, String[]> params = request().queryString();
		String source = params.get("source")[0];
		String target = params.get("target")[0];
		Following following = new Following(Long.valueOf(source), Long.valueOf(target));
		followingRepository.save(following);
		return ok();
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result unfollow() {
		final Map<String, String[]> params = request().queryString();

		String source = params.get("source")[0];
		String target = params.get("target")[0];
		Following following = followingRepository.findFollowing(Long.valueOf(source), Long.valueOf(target));
		followingRepository.delete(following);
		return ok();
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result getFollowers(long id, String format) {
		List<User> users = userRepository.findFollowers(id);
		
		String result = null;
		if (format.equals("json")) {
			result = new Gson().toJson(users);
		}
		return ok(result);
	}

	@Security.Authenticated(ActionAuthenticator.class)
	public Result getPeopleYouMayFollow(long id, String format) {
		User user = userRepository.findOne(id);
		List<User> interestingResearchers = userRepository.findByCluster(user.getCluster());

		List<User> followees = userRepository.findFolloweesByUser(id);
		HashSet<Long> userIds = new HashSet<Long>();
		for (User followee : followees) {
			userIds.add(followee.getId());
		}

		List<User> peopleYouMayFollow = new ArrayList<User>();

		for (User interestingResearcher : interestingResearchers) {
			if (interestingResearcher.getId() != id
					&& !userIds.contains(interestingResearcher.getId())) {
				peopleYouMayFollow.add(interestingResearcher);
			}
		}
		String result = null;
		if (format.equals("json")) {
			result = new Gson().toJson(peopleYouMayFollow);
		}
		return ok(result);
	}

}
